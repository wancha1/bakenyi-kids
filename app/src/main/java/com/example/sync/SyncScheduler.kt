package com.example.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncScheduler {

    const val ONE_TIME_SYNC_WORK_NAME = "bakenye_one_time_sync_work"
    const val PERIODIC_SYNC_WORK_NAME = "bakenye_periodic_sync_work"

    fun scheduleOneTimeSync(context: Context, replaceExisting: Boolean = false) {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            val policy = if (replaceExisting) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP

            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_TIME_SYNC_WORK_NAME,
                policy,
                workRequest
            )
            Log.d("SYNC_DEBUG", "Scheduled one-time sync work (policy: $policy).")
        } catch (e: Exception) {
            Log.e("SYNC_DEBUG", "Failed to schedule one-time sync work: ${e.message}", e)
        }
    }

    fun schedulePeriodicSync(context: Context) {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
            Log.d("SYNC_DEBUG", "Scheduled periodic sync work.")
        } catch (e: Exception) {
            Log.e("SYNC_DEBUG", "Failed to schedule periodic sync work: ${e.message}", e)
        }
    }

    fun cancelAllSyncWork(context: Context) {
        try {
            WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_SYNC_WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
            Log.d("SYNC_DEBUG", "Cancelled all sync work.")
        } catch (e: Exception) {
            Log.e("SYNC_DEBUG", "Failed to cancel sync work: ${e.message}", e)
        }
    }
}
