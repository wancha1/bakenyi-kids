package com.example

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.db.MIGRATION_1_2
import com.example.data.db.MIGRATION_2_3
import com.example.data.db.MIGRATION_3_4
import com.example.data.db.MIGRATION_4_5
import com.example.data.model.UserProfile
import com.example.data.repository.BakenyeRepository
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
class OfflineSyncOutboxTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repository: BakenyeRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()
        repository = BakenyeRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun test1_OutboxCreation_LocationProgress() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Outbox Child"))

        repository.updateLocationProgress(
            childProfileId = childId,
            locationId = "FISHING_AREA",
            wordsMastered = 4,
            stars = 5,
            isCompleted = true
        )

        val progress = db.bakenyeDao().getLocationProgressOnce(childId, "FISHING_AREA")
        assertNotNull(progress)
        assertEquals(4, progress?.termsMastered)

        val outboxEvents = db.syncOutboxDao().getPendingEventsForChildList(childId)
        val locOutbox = outboxEvents.find { it.entityType == "LOCATION_PROGRESS" && it.entityId == "FISHING_AREA" }
        assertNotNull("Outbox event must be created for location progress update", locOutbox)
        assertEquals("UPDATE", locOutbox?.operation)

        val payload = JSONObject(locOutbox?.payloadJson ?: "")
        assertEquals("FISHING_AREA", payload.getString("locationId"))
        assertEquals(4, payload.getInt("termsMastered"))
    }

    @Test
    fun test2_Atomicity_RollbackOnFailure() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Atomicity Child"))

        var exceptionThrown = false
        try {
            db.withTransaction {
                db.bakenyeDao().rewardUser(childId, 50, 100)
                throw RuntimeException("Simulated failure during transaction")
            }
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assertTrue(exceptionThrown)

        // Verify state is rolled back
        val profile = db.bakenyeDao().getUserProfileByIdOnce(childId)
        assertEquals("Stars must be rolled back on failure", 125, profile?.stars)

        val pendingCount = db.syncOutboxDao().getPendingCountForChild(childId)
        assertEquals("Outbox must be empty if transaction rolled back", 0, pendingCount)
    }

    @Test
    fun test3_LessonCompletion_Outbox() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Lesson Child"))

        repository.completeLesson(
            childProfileId = childId,
            lessonId = "L1_2",
            starReward = 3,
            coinReward = 20
        )

        val outboxEvents = db.syncOutboxDao().getPendingEventsForChildList(childId)
        val lessonEvent = outboxEvents.find { it.entityType == "CHILD_LESSON_PROGRESS" && it.entityId == "L1_2" }
        assertNotNull("Outbox event for lesson completion must exist", lessonEvent)

        val lessonPayload = JSONObject(lessonEvent?.payloadJson ?: "")
        assertEquals("L1_2", lessonPayload.getString("lessonId"))
        assertTrue(lessonPayload.getBoolean("isCompleted"))
    }

    @Test
    fun test4_BadgeUnlock_Outbox() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Badge Child"))

        repository.unlockBadge(childProfileId = childId, badgeId = "B4")

        val outboxEvents = db.syncOutboxDao().getPendingEventsForChildList(childId)
        val badgeEvent = outboxEvents.find { it.entityType == "CHILD_BADGE_UNLOCK" && it.entityId == "B4" }
        assertNotNull("Outbox event for badge unlock must exist", badgeEvent)

        val badgePayload = JSONObject(badgeEvent?.payloadJson ?: "")
        assertEquals("B4", badgePayload.getString("badgeId"))
        assertTrue(badgePayload.getBoolean("isUnlocked"))
    }

    @Test
    fun test5_Discovery_Outbox() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Discovery Child"))

        repository.recordDiscovery(childProfileId = childId, locationKey = "FISHING_AREA", speciesKey = "V_MUKENE")

        val outboxEvents = db.syncOutboxDao().getPendingEventsForChildList(childId)
        val discoveryEvent = outboxEvents.find { it.entityType == "CHILD_DISCOVERY" && it.entityId == "FISHING_AREA:V_MUKENE" }
        assertNotNull("Outbox event for discovery must exist", discoveryEvent)

        val payload = JSONObject(discoveryEvent?.payloadJson ?: "")
        assertEquals("FISHING_AREA", payload.getString("locationKey"))
        assertEquals("V_MUKENE", payload.getString("itemKey"))
        assertEquals("INSERT", discoveryEvent?.operation)
    }

    @Test
    fun test6_TimestampConsistency() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Timestamp Child"))

        repository.updateLocationProgress(
            childProfileId = childId,
            locationId = "PAPYRUS_GARDEN",
            wordsMastered = 2,
            stars = 3,
            isCompleted = false
        )

        val entity = db.bakenyeDao().getLocationProgressOnce(childId, "PAPYRUS_GARDEN")
        assertNotNull(entity)

        val outboxList = db.syncOutboxDao().getPendingEventsForChildList(childId)
        val outbox = outboxList.find { it.entityType == "LOCATION_PROGRESS" && it.entityId == "PAPYRUS_GARDEN" }
        assertNotNull(outbox)

        val payload = JSONObject(outbox?.payloadJson ?: "")
        val payloadTs = payload.getLong("updatedAtTimestamp")

        assertEquals("Entity updatedAtTimestamp must match outbox updatedAtTimestamp", entity?.updatedAtTimestamp, outbox?.updatedAtTimestamp)
        assertEquals("Outbox updatedAtTimestamp must match payloadJson updatedAtTimestamp", outbox?.updatedAtTimestamp, payloadTs)
    }

    @Test
    fun test7_ChildIsolation() = runBlocking {
        val childAId = UUID.randomUUID().toString()
        val childBId = UUID.randomUUID().toString()

        db.bakenyeDao().saveUserProfile(UserProfile(id = childAId, name = "Child A"))
        db.bakenyeDao().saveUserProfile(UserProfile(id = childBId, name = "Child B"))

        repository.unlockBadge(childProfileId = childAId, badgeId = "B1")
        repository.unlockBadge(childProfileId = childBId, badgeId = "B2")

        val outboxA = db.syncOutboxDao().getPendingEventsForChildList(childAId)
        val outboxB = db.syncOutboxDao().getPendingEventsForChildList(childBId)

        assertTrue(outboxA.all { it.childProfileId == childAId })
        assertTrue(outboxB.all { it.childProfileId == childBId })

        assertNotNull(outboxA.find { it.entityId == "B1" })
        assertNull(outboxA.find { it.entityId == "B2" })

        assertNotNull(outboxB.find { it.entityId == "B2" })
        assertNull(outboxB.find { it.entityId == "B1" })
    }

    @Test
    fun test8_RestartPersistence() = runBlocking {
        val dbFile = context.getDatabasePath("restart_outbox_test.db")
        context.deleteDatabase("restart_outbox_test.db")

        val persistentDb1 = Room.databaseBuilder(context, AppDatabase::class.java, "restart_outbox_test.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val repo1 = BakenyeRepository(persistentDb1)
        val childId = UUID.randomUUID().toString()
        persistentDb1.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Restart Child"))

        repo1.recordDiscovery(childProfileId = childId, locationKey = "BOAT_VILLAGE", speciesKey = "V_ERYATO")

        val countBeforeClose = persistentDb1.syncOutboxDao().getPendingCountForChild(childId)
        assertEquals(1, countBeforeClose)
        persistentDb1.close()

        // Re-open persistent DB
        val persistentDb2 = Room.databaseBuilder(context, AppDatabase::class.java, "restart_outbox_test.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val countAfterReopen = persistentDb2.syncOutboxDao().getPendingCountForChild(childId)
        assertEquals("Pending outbox events must survive DB restart", 1, countAfterReopen)

        val outbox = persistentDb2.syncOutboxDao().getPendingEventsForChildList(childId).first()
        assertEquals("BOAT_VILLAGE:V_ERYATO", outbox.entityId)

        persistentDb2.close()
    }

    @Test
    fun test9_NoStaticCatalogEvents() = runBlocking {
        val initialCount = db.syncOutboxDao().getPendingCount()

        repository.seedInitialDataIfEmpty()

        val allOutbox = db.syncOutboxDao().getAllOutboxEventsOnce()

        // Check that NO outbox events exist for static catalog types
        val forbiddenTypes = setOf("WORLD", "LESSON", "PHRASE", "BADGE", "VOCABULARY")
        val forbiddenEvents = allOutbox.filter { forbiddenTypes.contains(it.entityType) }

        assertTrue("Static catalog entities must never generate outbox records", forbiddenEvents.isEmpty())
    }

    @Test
    fun test10_RepeatedUpdateCoalescing() = runBlocking {
        val childId = UUID.randomUUID().toString()
        db.bakenyeDao().saveUserProfile(UserProfile(id = childId, name = "Coalesce Child"))

        // Update 1
        repository.updateLocationProgress(childProfileId = childId, locationId = "FISHING_AREA", wordsMastered = 1, stars = 1, isCompleted = false)

        // Update 2
        repository.updateLocationProgress(childProfileId = childId, locationId = "FISHING_AREA", wordsMastered = 2, stars = 2, isCompleted = false)

        // Update 3
        repository.updateLocationProgress(childProfileId = childId, locationId = "FISHING_AREA", wordsMastered = 3, stars = 3, isCompleted = true)

        val outboxEvents = db.syncOutboxDao().getPendingEventsForChildList(childId)
        val locEvents = outboxEvents.filter { it.entityType == "LOCATION_PROGRESS" && it.entityId == "FISHING_AREA" }

        assertEquals("Repeated updates must coalesce into a single pending outbox record", 1, locEvents.size)

        val coalescedPayload = JSONObject(locEvents.first().payloadJson)
        assertEquals(3, coalescedPayload.getInt("termsMastered"))
        assertEquals(3, coalescedPayload.getInt("starsEarned"))
        assertTrue(coalescedPayload.getBoolean("isCompleted"))
    }
}
