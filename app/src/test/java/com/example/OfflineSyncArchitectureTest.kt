package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.db.MIGRATION_1_2
import com.example.data.db.MIGRATION_2_3
import com.example.data.db.MIGRATION_3_4
import com.example.data.db.MIGRATION_4_5
import com.example.data.model.ChildDiscoveryEntity
import com.example.data.model.LocationProgressEntity
import com.example.data.model.UserProfile
import com.example.data.repository.BakenyeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OfflineSyncArchitectureTest {

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
    fun test1_UuidPersistence() = runBlocking {
        repository.seedInitialDataIfEmpty()
        val profile = repository.userProfile.first()
        assertNotNull(profile)
        val initialUuid = profile!!.id
        assertTrue("Profile ID must be a non-empty String UUID", initialUuid.length > 10)

        // Run seed again and verify UUID does not change
        repository.seedInitialDataIfEmpty()
        val reloadedProfile = repository.userProfile.first()
        assertEquals("Profile UUID must remain persistent across re-seed/re-open", initialUuid, reloadedProfile?.id)
    }

    @Test
    fun test2_TwoChildProgressIsolation() = runBlocking {
        val childAId = UUID.randomUUID().toString()
        val childBId = UUID.randomUUID().toString()

        val profileA = UserProfile(id = childAId, name = "Child A")
        val profileB = UserProfile(id = childBId, name = "Child B")
        db.bakenyeDao().saveUserProfile(profileA)
        db.bakenyeDao().saveUserProfile(profileB)

        // Give Child A 5 stars in FISHING_AREA
        repository.updateLocationProgress(childProfileId = childAId, locationId = "FISHING_AREA", wordsMastered = 5, stars = 5, isCompleted = true)

        // Give Child B 2 stars in FISHING_AREA
        repository.updateLocationProgress(childProfileId = childBId, locationId = "FISHING_AREA", wordsMastered = 2, stars = 2, isCompleted = false)

        val progressA = repository.getLocationProgress(childProfileId = childAId, locationId = "FISHING_AREA").first()
        val progressB = repository.getLocationProgress(childProfileId = childBId, locationId = "FISHING_AREA").first()

        assertNotNull(progressA)
        assertNotNull(progressB)
        assertEquals(5, progressA?.starsEarned)
        assertTrue(progressA?.isCompleted == true)

        assertEquals(2, progressB?.starsEarned)
        assertFalse(progressB?.isCompleted == true)
    }

    @Test
    fun test3_DiscoveryIsolation() = runBlocking {
        val childAId = UUID.randomUUID().toString()
        val childBId = UUID.randomUUID().toString()

        // Child A discovers V_MUKENE
        repository.recordDiscovery(childProfileId = childAId, locationKey = "FISHING_AREA", speciesKey = "V_MUKENE")

        val discoveriesA = repository.getChildDiscoveries(childProfileId = childAId, locationKey = "FISHING_AREA").first()
        val discoveriesB = repository.getChildDiscoveries(childProfileId = childBId, locationKey = "FISHING_AREA").first()

        assertTrue("Child A must have discovery recorded", discoveriesA.any { it.itemKey == "V_MUKENE" })
        assertTrue("Child B must not have Child A's discovery", discoveriesB.none { it.itemKey == "V_MUKENE" })
    }

    @Test
    fun test4_SeedSafetyAndIdempotency() = runBlocking {
        val seededProfile = repository.seedInitialDataIfEmpty()
        val initialStars = seededProfile.stars

        // Modify user profile state (e.g., gain stars)
        db.bakenyeDao().rewardUser(childProfileId = seededProfile.id, addStars = 10, addCoins = 50)

        val updatedProfileBeforeReseed = repository.getUserProfileById(seededProfile.id).first()
        assertEquals(initialStars + 10, updatedProfileBeforeReseed?.stars)

        // Run seeding multiple times
        repository.seedInitialDataIfEmpty()
        repository.seedInitialDataIfEmpty()

        val profileAfterReseed = repository.getUserProfileById(seededProfile.id).first()
        assertEquals("Seeding must never overwrite user profile progress or stars", initialStars + 10, profileAfterReseed?.stars)
    }

    @Test
    fun test5_MigrationPreservationFromV2ToV3() = runBlocking {
        val testDbName = "test_migration_v2_v3.db"
        context.deleteDatabase(testDbName)

        val factory = FrameworkSQLiteOpenHelperFactory()
        val callback = object : SupportSQLiteOpenHelper.Callback(2) {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_profile` (
                        `id` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `level` INTEGER NOT NULL,
                        `stars` INTEGER NOT NULL,
                        `coins` INTEGER NOT NULL,
                        `streakDays` INTEGER NOT NULL,
                        `guideAvatar` TEXT NOT NULL,
                        `currentWorld` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT INTO `user_profile` VALUES (1, 'Legacy Child 1', 3, 99, 200, 5, '🦁', 1)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `child_discoveries` (
                        `id` TEXT NOT NULL,
                        `childProfileId` TEXT NOT NULL,
                        `locationKey` TEXT NOT NULL,
                        `itemKey` TEXT NOT NULL,
                        `discoveredAtTimestamp` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT INTO `child_discoveries` VALUES ('FISHING_AREA_V_ENSOMBA', '1', 'FISHING_AREA', 'V_ENSOMBA', 1600000000000)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `location_progress` (
                        `locationId` TEXT NOT NULL,
                        `childProfileId` TEXT NOT NULL,
                        `termsMastered` INTEGER NOT NULL,
                        `starsEarned` INTEGER NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        PRIMARY KEY(`locationId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT INTO `location_progress` VALUES ('FISHING_AREA', '1', 4, 3, 1)")
            }

            override fun onUpgrade(db: androidx.sqlite.db.SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(testDbName)
            .callback(callback)
            .build()

        val helper = factory.create(config)
        val dbV2 = helper.writableDatabase

        // Execute MIGRATION_2_3
        MIGRATION_2_3.migrate(dbV2)

        // Verify user profile TEXT id migration
        val profileCursor = dbV2.query("SELECT * FROM user_profile")
        assertTrue("Migrated profile must exist", profileCursor.moveToFirst())
        val migratedId = profileCursor.getString(profileCursor.getColumnIndex("id"))
        assertEquals("00000000-0000-0000-0000-000000000001", migratedId)
        assertEquals("Legacy Child 1", profileCursor.getString(profileCursor.getColumnIndex("name")))
        assertEquals(99, profileCursor.getInt(profileCursor.getColumnIndex("stars")))
        profileCursor.close()

        // Verify child_discoveries migration
        val discCursor = dbV2.query("SELECT * FROM child_discoveries WHERE childProfileId = '$migratedId'")
        assertTrue("Child discovery must be preserved under migrated UUID", discCursor.moveToFirst())
        assertEquals("FISHING_AREA", discCursor.getString(discCursor.getColumnIndex("locationKey")))
        assertEquals("V_ENSOMBA", discCursor.getString(discCursor.getColumnIndex("itemKey")))
        discCursor.close()

        // Verify location_progress migration
        val progCursor = dbV2.query("SELECT * FROM location_progress WHERE childProfileId = '$migratedId'")
        assertTrue("Location progress must be preserved under migrated UUID", progCursor.moveToFirst())
        assertEquals(4, progCursor.getInt(progCursor.getColumnIndex("termsMastered")))
        assertEquals(3, progCursor.getInt(progCursor.getColumnIndex("starsEarned")))
        progCursor.close()

        dbV2.close()
    }

    @Test
    fun test6_DaoIsolationQueries() = runBlocking {
        val childAId = UUID.randomUUID().toString()
        val childBId = UUID.randomUUID().toString()

        val progressA = LocationProgressEntity(childProfileId = childAId, locationId = "PAPYRUS_GARDEN", termsMastered = 3, starsEarned = 3)
        val progressB = LocationProgressEntity(childProfileId = childBId, locationId = "PAPYRUS_GARDEN", termsMastered = 1, starsEarned = 1)

        db.bakenyeDao().saveLocationProgress(progressA)
        db.bakenyeDao().saveLocationProgress(progressB)

        val resultA = db.bakenyeDao().getLocationProgress(childAId, "PAPYRUS_GARDEN").first()
        assertNotNull(resultA)
        assertEquals(childAId, resultA?.childProfileId)
        assertEquals(3, resultA?.termsMastered)
    }

    @Test
    fun test7_LessonAndBadgeChildIsolation() = runBlocking {
        repository.seedInitialDataIfEmpty()
        val childAId = UUID.randomUUID().toString()
        val childBId = UUID.randomUUID().toString()

        val profileA = UserProfile(id = childAId, name = "Child A")
        val profileB = UserProfile(id = childBId, name = "Child B")
        db.bakenyeDao().saveUserProfile(profileA)
        db.bakenyeDao().saveUserProfile(profileB)

        // Complete lesson L1_2 for Child A
        repository.completeLesson(childProfileId = childAId, lessonId = "L1_2", starReward = 3, coinReward = 20)

        // Unlock badge B4 for Child A
        repository.unlockBadge(childProfileId = childAId, badgeId = "B4")

        // Query lessons and badges for Child A and Child B
        val lessonsA = repository.getLessonsForWorld(childProfileId = childAId, worldId = 1).first()
        val lessonsB = repository.getLessonsForWorld(childProfileId = childBId, worldId = 1).first()

        val badgesA = repository.getBadgesForChild(childProfileId = childAId).first()
        val badgesB = repository.getBadgesForChild(childProfileId = childBId).first()

        val lessonL1_2A = lessonsA.find { it.lessonId == "L1_2" }
        val lessonL1_2B = lessonsB.find { it.lessonId == "L1_2" }

        assertTrue("Child A must have L1_2 completed", lessonL1_2A?.isCompleted == true)
        assertFalse("Child B must NOT have L1_2 completed", lessonL1_2B?.isCompleted == true)

        val badgeB4A = badgesA.find { it.id == "B4" }
        val badgeB4B = badgesB.find { it.id == "B4" }

        assertTrue("Child A must have B4 unlocked", badgeB4A?.isUnlocked == true)
        assertFalse("Child B must NOT have B4 unlocked", badgeB4B?.isUnlocked == true)
    }
}
