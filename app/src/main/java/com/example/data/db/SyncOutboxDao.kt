package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SyncOutboxEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncOutboxDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutboxEvent(event: SyncOutboxEntity)

    @Update
    suspend fun updateOutboxEvent(event: SyncOutboxEntity)

    @Query("SELECT * FROM sync_outbox WHERE status = 'PENDING' ORDER BY createdAtTimestamp ASC")
    fun getPendingEvents(): Flow<List<SyncOutboxEntity>>

    @Query("SELECT * FROM sync_outbox WHERE status = 'PENDING' ORDER BY createdAtTimestamp ASC")
    suspend fun getPendingEventsList(): List<SyncOutboxEntity>

    @Query("SELECT * FROM sync_outbox WHERE childProfileId = :childProfileId AND status = 'PENDING' ORDER BY createdAtTimestamp ASC")
    fun getPendingEventsForChild(childProfileId: String): Flow<List<SyncOutboxEntity>>

    @Query("SELECT * FROM sync_outbox WHERE childProfileId = :childProfileId AND status = 'PENDING' ORDER BY createdAtTimestamp ASC")
    suspend fun getPendingEventsForChildList(childProfileId: String): List<SyncOutboxEntity>

    @Query("SELECT * FROM sync_outbox WHERE childProfileId = :childProfileId AND entityType = :entityType AND entityId = :entityId AND status = 'PENDING' LIMIT 1")
    suspend fun getPendingEventForEntity(childProfileId: String, entityType: String, entityId: String): SyncOutboxEntity?

    @Query("SELECT * FROM sync_outbox WHERE status = 'PENDING' ORDER BY createdAtTimestamp ASC LIMIT 1")
    suspend fun getOldestPendingEvent(): SyncOutboxEntity?

    @Query("SELECT COUNT(*) FROM sync_outbox WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int

    @Query("SELECT COUNT(*) FROM sync_outbox WHERE childProfileId = :childProfileId AND status = 'PENDING'")
    suspend fun getPendingCountForChild(childProfileId: String): Int

    @Query("SELECT * FROM sync_outbox ORDER BY createdAtTimestamp ASC")
    suspend fun getAllOutboxEventsOnce(): List<SyncOutboxEntity>

    @Query("SELECT * FROM sync_outbox WHERE status = 'PENDING' OR (status = 'FAILED' AND nextAttemptAtTimestamp IS NOT NULL AND nextAttemptAtTimestamp <= :now) OR (status = 'PROCESSING' AND lastAttemptAtTimestamp <= :staleThreshold) ORDER BY createdAtTimestamp ASC")
    suspend fun getEligibleEventsList(now: Long = System.currentTimeMillis(), staleThreshold: Long = now - 300000L): List<SyncOutboxEntity>

    @Query("UPDATE sync_outbox SET status = 'PROCESSING', attemptCount = attemptCount + 1, lastAttemptAtTimestamp = :now, updatedAtTimestamp = :now WHERE id = :id AND (status IN ('PENDING', 'FAILED') OR (status = 'PROCESSING' AND lastAttemptAtTimestamp <= :staleThreshold))")
    suspend fun claimForProcessing(id: String, now: Long = System.currentTimeMillis(), staleThreshold: Long = now - 300000L): Int

    @Query("UPDATE sync_outbox SET status = 'PENDING', updatedAtTimestamp = :now WHERE status = 'PROCESSING' AND lastAttemptAtTimestamp <= :staleThreshold")
    suspend fun recoverStaleProcessingEvents(staleThreshold: Long = System.currentTimeMillis() - 300000L, now: Long = System.currentTimeMillis()): Int

    @Query("UPDATE sync_outbox SET status = 'PROCESSING', updatedAtTimestamp = :now WHERE id = :id")
    suspend fun markProcessing(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE sync_outbox SET status = 'FAILED', lastAttemptAtTimestamp = :now, nextAttemptAtTimestamp = :nextAttempt, lastError = :error, updatedAtTimestamp = :now WHERE id = :id")
    suspend fun markFailed(id: String, error: String, nextAttempt: Long, now: Long = System.currentTimeMillis())

    @Query("UPDATE sync_outbox SET status = 'PERMANENT_FAILURE', nextAttemptAtTimestamp = NULL, lastError = :error, updatedAtTimestamp = :now WHERE id = :id")
    suspend fun markPermanentFailure(id: String, error: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE sync_outbox SET status = 'COMPLETED', updatedAtTimestamp = :now WHERE id = :id")
    suspend fun markCompleted(id: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM sync_outbox WHERE id = :id")
    suspend fun deleteOutboxEvent(id: String)

    @Query("DELETE FROM sync_outbox WHERE status = 'COMPLETED'")
    suspend fun deleteCompletedEvents()

    @Query("UPDATE sync_outbox SET status = 'PENDING', updatedAtTimestamp = :now WHERE status = 'FAILED' AND nextAttemptAtTimestamp IS NOT NULL AND nextAttemptAtTimestamp <= :now")
    suspend fun retryEligibleFailedEvents(now: Long = System.currentTimeMillis())
}
