package com.example.sync

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class RetryScheduled(val nextAttemptMs: Long? = null) : SyncState()
    data class Failed(val error: String) : SyncState()
}

data class SyncResultSummary(
    val totalProcessed: Int = 0,
    val successCount: Int = 0,
    val retryableFailureCount: Int = 0,
    val permanentFailureCount: Int = 0,
    val lastError: String? = null
)
