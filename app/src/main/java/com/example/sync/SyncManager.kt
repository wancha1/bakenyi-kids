package com.example.sync

import android.util.Log
import com.example.data.db.SyncOutboxDao
import com.example.data.model.SyncOutboxEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class SyncManager(
    private val outboxDao: SyncOutboxDao,
    private val transport: SyncTransport,
    private val connectivityMonitor: ConnectivityMonitor? = null
) {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    suspend fun syncNow(): SyncResultSummary {
        val now = System.currentTimeMillis()

        if (connectivityMonitor != null && !connectivityMonitor.isOnline()) {
            Log.d("SYNC_DEBUG", "Sync skipped: device is currently offline.")
            _syncState.value = SyncState.Idle
            return SyncResultSummary(lastError = "Device is offline")
        }

        _syncState.value = SyncState.Syncing

        var totalProcessed = 0
        var successCount = 0
        var retryableFailureCount = 0
        var permanentFailureCount = 0
        var lastError: String? = null
        var earliestNextAttempt: Long? = null

        try {
            // First, recover any stale events left in PROCESSING due to app crashes or process death (>5 mins ago)
            val staleThreshold = now - 300_000L
            outboxDao.recoverStaleProcessingEvents(staleThreshold, now)

            // Make any failed events eligible if their backoff time has passed
            outboxDao.retryEligibleFailedEvents(now)

            val pendingEvents = outboxDao.getEligibleEventsList(now, staleThreshold)
            if (pendingEvents.isEmpty()) {
                Log.d("SYNC_DEBUG", "No pending sync events found.")
                _syncState.value = SyncState.Success
                return SyncResultSummary(0, 0, 0, 0, null)
            }

            Log.d("SYNC_DEBUG", "Starting sync processing for ${pendingEvents.size} outbox events.")

            for (event in pendingEvents) {
                val claimTime = System.currentTimeMillis()
                val claimed = outboxDao.claimForProcessing(event.id, claimTime)
                if (claimed <= 0) {
                    Log.d("SYNC_DEBUG", "Event ${event.id} already claimed or processed, skipping.")
                    continue
                }

                totalProcessed++

                // Validate payload JSON integrity
                val isValidPayload = try {
                    JSONObject(event.payloadJson)
                    true
                } catch (e: Exception) {
                    false
                }

                if (!isValidPayload) {
                    val errorMsg = "Malformed JSON payload in event ${event.id}"
                    Log.e("SYNC_DEBUG", errorMsg)
                    outboxDao.markPermanentFailure(event.id, errorMsg, claimTime)
                    permanentFailureCount++
                    lastError = errorMsg
                    continue
                }

                // Push event via transport
                val result = try {
                    transport.push(event.copy(
                        status = "PROCESSING",
                        attemptCount = event.attemptCount + 1,
                        lastAttemptAtTimestamp = claimTime
                    ))
                } catch (e: Exception) {
                    Log.e("SYNC_DEBUG", "Transport exception for event ${event.id}: ${e.message}", e)
                    SyncResult(SyncResultStatus.RETRYABLE_FAILURE, e.message ?: "Transport error")
                }

                when (result.status) {
                    SyncResultStatus.SUCCESS -> {
                        outboxDao.markCompleted(event.id, System.currentTimeMillis())
                        successCount++
                        Log.d("SYNC_DEBUG", "Event ${event.id} (${event.entityType}:${event.entityId}) successfully synced.")
                    }
                    SyncResultStatus.RETRYABLE_FAILURE -> {
                        val attempts = event.attemptCount + 1
                        val backoffDelay = calculateBackoffDelayMs(attempts)
                        val nextAttempt = System.currentTimeMillis() + backoffDelay
                        val errorMsg = result.errorMessage ?: "Retryable failure"

                        outboxDao.markFailed(event.id, errorMsg, nextAttempt, System.currentTimeMillis())
                        retryableFailureCount++
                        lastError = errorMsg

                        if (earliestNextAttempt == null || nextAttempt < earliestNextAttempt!!) {
                            earliestNextAttempt = nextAttempt
                        }

                        Log.w("SYNC_DEBUG", "Event ${event.id} failed (attempt $attempts). Scheduled retry in ${backoffDelay}ms.")
                    }
                    SyncResultStatus.PERMANENT_FAILURE -> {
                        val errorMsg = result.errorMessage ?: "Permanent failure"
                        outboxDao.markPermanentFailure(event.id, errorMsg, System.currentTimeMillis())
                        permanentFailureCount++
                        lastError = errorMsg
                        Log.e("SYNC_DEBUG", "Event ${event.id} failed permanently: $errorMsg")
                    }
                }
            }

            if (permanentFailureCount > 0 && successCount == 0 && retryableFailureCount == 0) {
                _syncState.value = SyncState.Failed(lastError ?: "Permanent failure occurred")
            } else if (retryableFailureCount > 0) {
                _syncState.value = SyncState.RetryScheduled(earliestNextAttempt)
            } else {
                _syncState.value = SyncState.Success
            }

            return SyncResultSummary(
                totalProcessed = totalProcessed,
                successCount = successCount,
                retryableFailureCount = retryableFailureCount,
                permanentFailureCount = permanentFailureCount,
                lastError = lastError
            )

        } catch (e: Exception) {
            Log.e("SYNC_DEBUG", "Unexpected error during sync execution: ${e.message}", e)
            _syncState.value = SyncState.Failed(e.message ?: "Sync failed")
            return SyncResultSummary(
                totalProcessed = totalProcessed,
                successCount = successCount,
                retryableFailureCount = retryableFailureCount,
                permanentFailureCount = permanentFailureCount,
                lastError = e.message
            )
        }
    }

    companion object {
        fun calculateBackoffDelayMs(attemptCount: Int): Long {
            return when {
                attemptCount <= 1 -> 5_000L
                attemptCount == 2 -> 15_000L
                attemptCount == 3 -> 30_000L
                attemptCount == 4 -> 60_000L
                else -> 300_000L // 5 minutes cap
            }
        }
    }
}
