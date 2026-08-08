package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.data.db.AppDatabase
import com.example.data.db.MIGRATION_1_2
import com.example.data.db.MIGRATION_2_3
import com.example.data.db.MIGRATION_3_4
import com.example.data.db.MIGRATION_4_5
import com.example.data.model.SyncOutboxEntity
import com.example.data.model.UserProfile
import com.example.data.repository.BakenyeRepository
import com.example.sync.ConnectivityMonitor
import com.example.sync.FakeSyncTransport
import com.example.sync.SyncManager
import com.example.sync.SyncResult
import com.example.sync.SyncResultStatus
import com.example.sync.SyncScheduler
import com.example.sync.SyncState
import com.example.sync.SyncTransportProvider
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OfflineSyncEngineTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repository: BakenyeRepository
    private lateinit var fakeTransport: FakeSyncTransport
    private lateinit var syncManager: SyncManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        
        // Initialize WorkManager test environment
        WorkManagerTestInitHelper.initializeTestWorkManager(context)

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        repository = BakenyeRepository(db)
        fakeTransport = FakeSyncTransport()
        SyncTransportProvider.setTransport(fakeTransport)

        syncManager = SyncManager(
            outboxDao = db.syncOutboxDao(),
            transport = fakeTransport,
            connectivityMonitor = null // Default to online for direct unit tests unless overridden
        )
    }

    @After
    fun tearDown() {
        db.close()
        SyncTransportProvider.reset()
        try {
            val workManagerClass = Class.forName("androidx.work.impl.WorkManagerImpl")
            val getInstanceMethod = workManagerClass.getMethod("getInstance", Context::class.java)
            val workManagerImpl = getInstanceMethod.invoke(null, context)
            if (workManagerImpl != null) {
                val getWorkDatabaseMethod = workManagerClass.getMethod("getWorkDatabase")
                val workDatabase = getWorkDatabaseMethod.invoke(workManagerImpl) as? androidx.room.RoomDatabase
                workDatabase?.close()
            }
        } catch (e: Exception) {
            // WorkManager not initialized or reflection failure
        }
    }

    // TEST 1 — Empty Outbox
    @Test
    fun test1_EmptyOutbox_CompletesSuccessfullyWithoutTransportCalls() = runBlocking {
        val summary = syncManager.syncNow()

        assertEquals(0, summary.totalProcessed)
        assertEquals(0, summary.successCount)
        assertEquals(0, summary.retryableFailureCount)
        assertEquals(0, summary.permanentFailureCount)
        assertEquals(0, fakeTransport.pushedEvents.size)
        assertTrue(syncManager.syncState.value is SyncState.Success)
    }

    // TEST 2 — Successful Event
    @Test
    fun test2_SuccessfulEvent_TransitionsToCompletedAndIncrementsAttemptCount() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Test Child 2"))

        repository.updateLocationProgress(
            childProfileId = childId,
            locationId = "BOAT_VILLAGE",
            wordsMastered = 5,
            stars = 3,
            isCompleted = true
        )

        val pendingBefore = db.syncOutboxDao().getPendingEventsForChildList(childId)
        assertEquals(1, pendingBefore.size)
        val eventBefore = pendingBefore.first()

        val summary = syncManager.syncNow()

        assertEquals(1, summary.totalProcessed)
        assertEquals(1, summary.successCount)
        assertEquals(0, summary.retryableFailureCount)
        assertEquals(0, summary.permanentFailureCount)

        assertEquals(1, fakeTransport.pushedEvents.size)
        val pushed = fakeTransport.pushedEvents.first()
        assertEquals(eventBefore.id, pushed.id)

        val allOutbox = db.syncOutboxDao().getAllOutboxEventsOnce()
        val completedEvent = allOutbox.find { it.id == eventBefore.id }
        assertNotNull(completedEvent)
        assertEquals("COMPLETED", completedEvent?.status)
        assertEquals(1, completedEvent?.attemptCount)
        assertNotNull(completedEvent?.lastAttemptAtTimestamp)
    }

    // TEST 3 — Retryable Failure
    @Test
    fun test3_RetryableFailure_SchedulesRetryAndPopulatesErrorAndBackoff() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Test Child 3"))

        repository.unlockBadge(childProfileId = childId, badgeId = "B1")

        val event = db.syncOutboxDao().getPendingEventsForChildList(childId).first()
        fakeTransport.setResponseForEvent(event.id, SyncResult(SyncResultStatus.RETRYABLE_FAILURE, "HTTP 503 Server Unavailable"))

        val summary = syncManager.syncNow()

        assertEquals(1, summary.totalProcessed)
        assertEquals(0, summary.successCount)
        assertEquals(1, summary.retryableFailureCount)
        assertEquals("HTTP 503 Server Unavailable", summary.lastError)

        val updated = db.syncOutboxDao().getAllOutboxEventsOnce().find { it.id == event.id }
        assertNotNull(updated)
        assertEquals("FAILED", updated?.status)
        assertEquals(1, updated?.attemptCount)
        assertEquals("HTTP 503 Server Unavailable", updated?.lastError)
        assertNotNull(updated?.nextAttemptAtTimestamp)
        assertTrue(updated!!.nextAttemptAtTimestamp!! > System.currentTimeMillis())

        assertTrue(syncManager.syncState.value is SyncState.RetryScheduled)
    }

    // TEST 4 — Permanent Failure
    @Test
    fun test4_PermanentFailure_MarksPermanentFailureWithoutInfiniteRetry() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Test Child 4"))

        repository.recordDiscovery(childProfileId = childId, locationKey = "ELDER_BAOBAB", speciesKey = "V_ENKOBA")

        val event = db.syncOutboxDao().getPendingEventsForChildList(childId).first()
        fakeTransport.setResponseForEvent(event.id, SyncResult(SyncResultStatus.PERMANENT_FAILURE, "400 Bad Request: Invalid Entity Structure"))

        val summary = syncManager.syncNow()

        assertEquals(1, summary.totalProcessed)
        assertEquals(0, summary.successCount)
        assertEquals(1, summary.permanentFailureCount)

        val updated = db.syncOutboxDao().getAllOutboxEventsOnce().find { it.id == event.id }
        assertNotNull(updated)
        assertEquals("PERMANENT_FAILURE", updated?.status)
        assertEquals("400 Bad Request: Invalid Entity Structure", updated?.lastError)
        assertNull("Permanent failure must not set nextAttemptAtTimestamp", updated?.nextAttemptAtTimestamp)
    }

    // TEST 5 — Multiple Events
    @Test
    fun test5_MultipleEvents_ProcessedInDeterministicCreatedAtAscendingOrder() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Test Child 5"))

        repository.updateLocationProgress(childProfileId = childId, locationId = "LOC_1", wordsMastered = 1, stars = 1, isCompleted = false)
        kotlinx.coroutines.delay(10)
        repository.updateLocationProgress(childProfileId = childId, locationId = "LOC_2", wordsMastered = 2, stars = 2, isCompleted = false)
        kotlinx.coroutines.delay(10)
        repository.updateLocationProgress(childProfileId = childId, locationId = "LOC_3", wordsMastered = 3, stars = 3, isCompleted = false)

        val summary = syncManager.syncNow()

        assertEquals(3, summary.totalProcessed)
        assertEquals(3, summary.successCount)

        assertEquals(3, fakeTransport.pushedEvents.size)
        val loc1Index = fakeTransport.pushedEvents.indexOfFirst { it.entityId == "LOC_1" }
        val loc2Index = fakeTransport.pushedEvents.indexOfFirst { it.entityId == "LOC_2" }
        val loc3Index = fakeTransport.pushedEvents.indexOfFirst { it.entityId == "LOC_3" }

        assertTrue("LOC_1 must be pushed before LOC_2", loc1Index < loc2Index)
        assertTrue("LOC_2 must be pushed before LOC_3", loc2Index < loc3Index)
    }

    // TEST 6 — Child Isolation
    @Test
    fun test6_ChildIsolation_PreservesChildProfileIdsAndDoesNotMixData() = runBlocking {
        val childA = UUID.randomUUID().toString()
        val childB = UUID.randomUUID().toString()

        db.bakenyeDao().saveUserProfile(UserProfile(id = childA, name = "Child A"))
        db.bakenyeDao().saveUserProfile(UserProfile(id = childB, name = "Child B"))

        repository.unlockBadge(childProfileId = childA, badgeId = "BADGE_A")
        repository.unlockBadge(childProfileId = childB, badgeId = "BADGE_B")

        val summary = syncManager.syncNow()
        assertEquals(2, summary.totalProcessed)

        val eventA = fakeTransport.pushedEvents.find { it.entityId == "BADGE_A" }
        val eventB = fakeTransport.pushedEvents.find { it.entityId == "BADGE_B" }

        assertNotNull(eventA)
        assertNotNull(eventB)
        assertEquals(childA, eventA?.childProfileId)
        assertEquals(childB, eventB?.childProfileId)
    }

    // TEST 7 — Event Coalescing Regression
    @Test
    fun test7_EventCoalescingRegression_MultipleMutationsCoalesceToOnePendingEvent() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Coalesce Child"))

        repository.updateLocationProgress(childProfileId = childId, locationId = "FISHING_AREA", wordsMastered = 1, stars = 1, isCompleted = false)
        repository.updateLocationProgress(childProfileId = childId, locationId = "FISHING_AREA", wordsMastered = 3, stars = 2, isCompleted = false)
        repository.updateLocationProgress(childProfileId = childId, locationId = "FISHING_AREA", wordsMastered = 5, stars = 3, isCompleted = true)

        val pendingList = db.syncOutboxDao().getPendingEventsForChildList(childId)
        assertEquals("Multiple updates to same entity must coalesce to 1 pending outbox event", 1, pendingList.size)

        val summary = syncManager.syncNow()
        assertEquals(1, summary.totalProcessed)

        val pushedPayload = JSONObject(fakeTransport.pushedEvents.first().payloadJson)
        assertEquals(5, pushedPayload.getInt("termsMastered"))
        assertEquals(3, pushedPayload.getInt("starsEarned"))
        assertTrue(pushedPayload.getBoolean("isCompleted"))
    }

    // TEST 8 — Restart Persistence
    @Test
    fun test8_RestartPersistence_OutboxEventsRemainAvailableAfterDatabaseReopen() = runBlocking {
        val dbFile = context.getDatabasePath("sync_restart_test.db")
        context.deleteDatabase("sync_restart_test.db")

        val db1 = Room.databaseBuilder(context, AppDatabase::class.java, "sync_restart_test.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val repo1 = BakenyeRepository(db1)
        val childId = UUID.randomUUID().toString()
        db1.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Persistent Child"))

        repo1.completeLesson(childProfileId = childId, lessonId = "L1_1", starReward = 3, coinReward = 20)

        val pendingBeforeClose = db1.syncOutboxDao().getPendingCountForChild(childId)
        assertTrue(pendingBeforeClose > 0)
        db1.close()

        // Reopen database
        val db2 = Room.databaseBuilder(context, AppDatabase::class.java, "sync_restart_test.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val syncManager2 = SyncManager(outboxDao = db2.syncOutboxDao(), transport = fakeTransport)
        val summary = syncManager2.syncNow()

        assertTrue("Pending events must be successfully processed after DB reopen", summary.totalProcessed > 0)
        assertEquals(0, db2.syncOutboxDao().getPendingCountForChild(childId))

        db2.close()
    }

    // TEST 9 — Offline Network
    @Test
    fun test9_OfflineNetwork_AppDoesNotCrashAndEventsRemainPending() = runBlocking {
        val offlineMonitor = object : ConnectivityMonitor(context) {
            override fun isOnline(): Boolean = false
        }

        val offlineManager = SyncManager(
            outboxDao = db.syncOutboxDao(),
            transport = fakeTransport,
            connectivityMonitor = offlineMonitor
        )

        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Offline Child"))
        repository.unlockBadge(childProfileId = childId, badgeId = "B_OFFLINE")

        val summary = offlineManager.syncNow()

        assertEquals(0, summary.totalProcessed)
        assertEquals("Device is offline", summary.lastError)
        assertEquals(0, fakeTransport.pushedEvents.size)

        val pendingCount = db.syncOutboxDao().getPendingCountForChild(childId)
        assertEquals("Events must remain pending when offline", 1, pendingCount)
    }

    // TEST 10 — Connectivity Restoration
    @Test
    fun test10_ConnectivityRestoration_TriggersOneTimeSyncSchedule() = runBlocking {
        SyncScheduler.scheduleOneTimeSync(context)

        val workManager = WorkManager.getInstance(context)
        val workInfos = workManager.getWorkInfosForUniqueWork(SyncScheduler.ONE_TIME_SYNC_WORK_NAME).get()

        assertNotNull(workInfos)
        assertFalse(workInfos.isEmpty())
        val workInfo = workInfos.first()
        assertTrue(workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING)
    }

    // TEST 11 — Duplicate Worker Protection
    @Test
    fun test11_DuplicateWorkerProtection_WorkManagerUniqueWorkPreventsDuplicates() = runBlocking {
        SyncScheduler.scheduleOneTimeSync(context, replaceExisting = false)
        SyncScheduler.scheduleOneTimeSync(context, replaceExisting = false)
        SyncScheduler.scheduleOneTimeSync(context, replaceExisting = false)

        val workManager = WorkManager.getInstance(context)
        val workInfos = workManager.getWorkInfosForUniqueWork(SyncScheduler.ONE_TIME_SYNC_WORK_NAME).get()

        assertNotNull(workInfos)
        assertEquals("Unique work policy KEEP must maintain exactly 1 work request", 1, workInfos.size)
    }

    // TEST 12 — Manual Sync
    @Test
    fun test12_ManualSync_ProcessesPendingEventsWithoutBlocking() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Manual Child"))

        repository.updateLocationProgress(childProfileId = childId, locationId = "PAPYRUS_GARDEN", wordsMastered = 4, stars = 2, isCompleted = true)

        val summary = syncManager.syncNow()

        assertEquals(1, summary.totalProcessed)
        assertEquals(1, summary.successCount)
        assertEquals("PAPYRUS_GARDEN", fakeTransport.pushedEvents.first().entityId)
    }

    // TEST 13 — Malformed Payload
    @Test
    fun test13_MalformedPayload_ClassifiedAsPermanentFailureAndRetainsError() = runBlocking {
        val childId = UUID.randomUUID().toString()
        
        val malformedEvent = SyncOutboxEntity(
            id = UUID.randomUUID().toString(),
            childProfileId = childId,
            entityType = "CORRUPTED_ENTITY",
            entityId = "CORRUPTED_123",
            operation = "UPDATE",
            payloadJson = "{ INVALID JSON STRING >>>",
            createdAtTimestamp = System.currentTimeMillis()
        )

        db.syncOutboxDao().insertOutboxEvent(malformedEvent)

        val summary = syncManager.syncNow()

        assertEquals(1, summary.totalProcessed)
        assertEquals(0, summary.successCount)
        assertEquals(1, summary.permanentFailureCount)

        val updated = db.syncOutboxDao().getAllOutboxEventsOnce().find { it.id == malformedEvent.id }
        assertNotNull(updated)
        assertEquals("PERMANENT_FAILURE", updated?.status)
        assertTrue(updated?.lastError?.contains("Malformed JSON") == true)
        assertEquals(0, fakeTransport.pushedEvents.size)
    }

    // TEST 14 — Stale Processing Event Recovery
    @Test
    fun test14_StaleProcessingEventRecovery_RecoversEventsInterruptedByProcessDeath() = runBlocking {
        val childId = UUID.randomUUID().toString()
        val staleTime = System.currentTimeMillis() - 600_000L // 10 minutes ago
        
        val staleProcessingEvent = SyncOutboxEntity(
            id = UUID.randomUUID().toString(),
            childProfileId = childId,
            entityType = "LOCATION_PROGRESS",
            entityId = "STALE_LOC",
            operation = "UPDATE",
            payloadJson = "{\"locationId\":\"STALE_LOC\"}",
            createdAtTimestamp = staleTime,
            lastAttemptAtTimestamp = staleTime,
            status = "PROCESSING"
        )

        db.syncOutboxDao().insertOutboxEvent(staleProcessingEvent)

        val summary = syncManager.syncNow()

        assertEquals(1, summary.totalProcessed)
        assertEquals(1, summary.successCount)

        val completed = db.syncOutboxDao().getAllOutboxEventsOnce().find { it.id == staleProcessingEvent.id }
        assertNotNull(completed)
        assertEquals("COMPLETED", completed?.status)
    }
}
