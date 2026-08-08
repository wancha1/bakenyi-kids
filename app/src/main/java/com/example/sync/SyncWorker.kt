package com.example.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.db.AppDatabase

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SYNC_DEBUG", "SyncWorker started execution.")
        return try {
            val db = AppDatabase.getDatabase(applicationContext)
            val connectivityMonitor = ConnectivityMonitor(applicationContext)
            val transport = SyncTransportProvider.getTransport()
            val syncManager = SyncManager(
                outboxDao = db.syncOutboxDao(),
                transport = transport,
                connectivityMonitor = connectivityMonitor
            )

            val summary = syncManager.syncNow()
            Log.d("SYNC_DEBUG", "SyncWorker finished with summary: $summary")

            when {
                summary.retryableFailureCount > 0 -> {
                    Log.w("SYNC_DEBUG", "SyncWorker returning Result.retry() due to retryable failures.")
                    Result.retry()
                }
                summary.permanentFailureCount > 0 && summary.successCount == 0 -> {
                    Log.e("SYNC_DEBUG", "SyncWorker returning Result.failure() due to unrecoverable permanent failures.")
                    Result.failure()
                }
                else -> {
                    Log.d("SYNC_DEBUG", "SyncWorker returning Result.success().")
                    Result.success()
                }
            }
        } catch (e: Exception) {
            Log.e("SYNC_DEBUG", "SyncWorker encountered uncaught exception: ${e.message}", e)
            Result.retry()
        }
    }
}

object SyncTransportProvider {
    @Volatile
    private var transportInstance: SyncTransport? = null

    fun getTransport(): SyncTransport {
        return transportInstance ?: FakeSyncTransport()
    }

    fun setTransport(transport: SyncTransport) {
        transportInstance = transport
    }

    fun reset() {
        transportInstance = null
    }
}
