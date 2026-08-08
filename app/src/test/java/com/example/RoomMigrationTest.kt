package com.example

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MIGRATION_1_2
import com.example.data.db.MIGRATION_2_3
import com.example.data.db.MIGRATION_3_4
import com.example.data.db.MIGRATION_4_5
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomMigrationTest {

    private val TEST_DB = "migration-test.db"

    @Test
    fun migration1To2_preservesData_andCreatesNewTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val factory = FrameworkSQLiteOpenHelperFactory()
        val callback = object : SupportSQLiteOpenHelper.Callback(1) {
            override fun onCreate(db: SupportSQLiteDatabase) {
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
                db.execSQL(
                    """
                    INSERT INTO `user_profile` (`id`, `name`, `level`, `stars`, `coins`, `streakDays`, `guideAvatar`, `currentWorld`)
                    VALUES (1, 'Kato Legacy Explorer', 5, 250, 600, 7, '🦁', 2)
                    """.trimIndent()
                )
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(callback)
            .build()

        val helper = factory.create(config)
        var db = helper.writableDatabase

        // Verify version 1 initialized with user profile
        var cursor = db.query("SELECT * FROM user_profile WHERE id = 1")
        assertTrue("User profile from v1 should exist", cursor.moveToFirst())
        assertEquals("Kato Legacy Explorer", cursor.getString(cursor.getColumnIndex("name")))
        cursor.close()

        // Apply MIGRATION_1_2 directly
        MIGRATION_1_2.migrate(db)

        // Verify user profile still preserved
        cursor = db.query("SELECT * FROM user_profile WHERE id = 1")
        assertTrue("User profile should be preserved after migration", cursor.moveToFirst())
        assertEquals("Kato Legacy Explorer", cursor.getString(cursor.getColumnIndex("name")))
        assertEquals(250, cursor.getInt(cursor.getColumnIndex("stars")))
        cursor.close()

        // Verify new tables created in MIGRATION_1_2 exist and are queryable
        val vocabularyCursor = db.query("SELECT count(*) FROM vocabulary_items")
        assertNotNull("vocabulary_items table must exist", vocabularyCursor)
        vocabularyCursor.close()

        val discoveriesCursor = db.query("SELECT count(*) FROM child_discoveries")
        assertNotNull("child_discoveries table must exist", discoveriesCursor)
        discoveriesCursor.close()

        val progressCursor = db.query("SELECT count(*) FROM location_progress")
        assertNotNull("location_progress table must exist", progressCursor)
        progressCursor.close()

        db.close()
    }

    @Test
    fun migration2To3_migratesIdentity_andPreservesAllData() {
        val testDbName = "migration-v2-v3-test.db"
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(testDbName)

        val factory = FrameworkSQLiteOpenHelperFactory()
        val callback = object : SupportSQLiteOpenHelper.Callback(2) {
            override fun onCreate(db: SupportSQLiteDatabase) {
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
                db.execSQL("INSERT INTO `user_profile` VALUES (1, 'Legacy Kato Explorer', 4, 150, 500, 3, '🦒', 1)")

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
                db.execSQL("INSERT INTO `child_discoveries` VALUES ('FISHING_AREA_V_MUKENE', '1', 'FISHING_AREA', 'V_MUKENE', 1700000000000)")

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
                db.execSQL("INSERT INTO `location_progress` VALUES ('FISHING_AREA', '1', 5, 3, 1)")
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(testDbName)
            .callback(callback)
            .build()

        val helper = factory.create(config)
        val db = helper.writableDatabase

        // Apply MIGRATION_2_3
        MIGRATION_2_3.migrate(db)

        // Verify user profile migrated from Int ID 1 to String UUID
        val profileCursor = db.query("SELECT * FROM user_profile")
        assertTrue("User profile should be present after MIGRATION_2_3", profileCursor.moveToFirst())
        val migratedId = profileCursor.getString(profileCursor.getColumnIndex("id"))
        assertEquals("00000000-0000-0000-0000-000000000001", migratedId)
        assertEquals("Legacy Kato Explorer", profileCursor.getString(profileCursor.getColumnIndex("name")))
        assertEquals(150, profileCursor.getInt(profileCursor.getColumnIndex("stars")))
        profileCursor.close()

        // Verify child_discoveries migrated to composite key and updated profile ID
        val discCursor = db.query("SELECT * FROM child_discoveries WHERE childProfileId = '$migratedId'")
        assertTrue("Discovery should be migrated under new childProfileId UUID", discCursor.moveToFirst())
        assertEquals("V_MUKENE", discCursor.getString(discCursor.getColumnIndex("itemKey")))
        discCursor.close()

        // Verify location_progress migrated to composite key and updated profile ID
        val progCursor = db.query("SELECT * FROM location_progress WHERE childProfileId = '$migratedId'")
        assertTrue("Progress should be migrated under new childProfileId UUID", progCursor.moveToFirst())
        assertEquals(5, progCursor.getInt(progCursor.getColumnIndex("termsMastered")))
        assertEquals(3, progCursor.getInt(progCursor.getColumnIndex("starsEarned")))
        progCursor.close()

        db.close()
    }

    @Test
    fun migration3To4_separatesCatalogFromChildProgress() {
        val testDbName = "migration-v3-v4-test.db"
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(testDbName)

        val factory = FrameworkSQLiteOpenHelperFactory()
        val callback = object : SupportSQLiteOpenHelper.Callback(3) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_profile` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `level` INTEGER NOT NULL,
                        `stars` INTEGER NOT NULL,
                        `coins` INTEGER NOT NULL,
                        `streakDays` INTEGER NOT NULL,
                        `guideAvatar` TEXT NOT NULL,
                        `currentWorld` INTEGER NOT NULL,
                        `createdAtTimestamp` INTEGER NOT NULL,
                        `updatedAtTimestamp` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT INTO `user_profile` VALUES ('00000000-0000-0000-0000-000000000001', 'Kato v3 Explorer', 4, 150, 500, 3, '🦒', 1, 1000, 1000)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `lessons` (
                        `lessonId` TEXT NOT NULL,
                        `worldId` INTEGER NOT NULL,
                        `title` TEXT NOT NULL,
                        `subtitle` TEXT NOT NULL,
                        `iconEmoji` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `isLocked` INTEGER NOT NULL,
                        `starReward` INTEGER NOT NULL,
                        PRIMARY KEY(`lessonId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT INTO `lessons` VALUES ('L1_1', 1, 'Lesson 1', 'Sub 1', '🅰️', 1, 0, 3)")
                db.execSQL("INSERT INTO `lessons` VALUES ('L1_2', 1, 'Lesson 2', 'Sub 2', '🎵', 0, 0, 3)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `badges` (
                        `id` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `iconEmoji` TEXT NOT NULL,
                        `isUnlocked` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT INTO `badges` VALUES ('B1', 'Explorer', 'Desc 1', '🧭', 1)")
                db.execSQL("INSERT INTO `badges` VALUES ('B2', 'Master', 'Desc 2', '🎧', 0)")
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(testDbName)
            .callback(callback)
            .build()

        val helper = factory.create(config)
        val db = helper.writableDatabase

        // Apply MIGRATION_3_4
        MIGRATION_3_4.migrate(db)

        // Verify child_lesson_progress table contains completed lesson L1_1
        val lessonProgCursor = db.query("SELECT * FROM child_lesson_progress WHERE childProfileId = '00000000-0000-0000-0000-000000000001'")
        assertTrue("Child lesson progress must be created for legacy completed lesson", lessonProgCursor.moveToFirst())
        assertEquals("L1_1", lessonProgCursor.getString(lessonProgCursor.getColumnIndex("lessonId")))
        assertEquals(1, lessonProgCursor.getInt(lessonProgCursor.getColumnIndex("isCompleted")))
        lessonProgCursor.close()

        // Verify child_badge_unlocks table contains unlocked badge B1
        val badgeUnlockCursor = db.query("SELECT * FROM child_badge_unlocks WHERE childProfileId = '00000000-0000-0000-0000-000000000001'")
        assertTrue("Child badge unlock must be created for legacy unlocked badge", badgeUnlockCursor.moveToFirst())
        assertEquals("B1", badgeUnlockCursor.getString(badgeUnlockCursor.getColumnIndex("badgeId")))
        assertEquals(1, badgeUnlockCursor.getInt(badgeUnlockCursor.getColumnIndex("isUnlocked")))
        badgeUnlockCursor.close()

        // Verify lessons catalog no longer has isCompleted column
        val lessonsCursor = db.query("SELECT * FROM lessons")
        assertEquals(-1, lessonsCursor.getColumnIndex("isCompleted"))
        assertTrue(lessonsCursor.moveToFirst())
        lessonsCursor.close()

        // Verify badges catalog no longer has isUnlocked column
        val badgesCursor = db.query("SELECT * FROM badges")
        assertEquals(-1, badgesCursor.getColumnIndex("isUnlocked"))
        assertTrue(badgesCursor.moveToFirst())
        badgesCursor.close()

        db.close()
    }

    @Test
    fun migration4To5_createsSyncOutboxTableAndPreservesData() {
        val testDbName = "migration-v4-v5-test.db"
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(testDbName)

        val factory = FrameworkSQLiteOpenHelperFactory()
        val callback = object : SupportSQLiteOpenHelper.Callback(4) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_profile` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `level` INTEGER NOT NULL,
                        `stars` INTEGER NOT NULL,
                        `coins` INTEGER NOT NULL,
                        `streakDays` INTEGER NOT NULL,
                        `guideAvatar` TEXT NOT NULL,
                        `currentWorld` INTEGER NOT NULL,
                        `createdAtTimestamp` INTEGER NOT NULL,
                        `updatedAtTimestamp` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT INTO `user_profile` VALUES ('v4-child-123', 'Child V4', 5, 200, 600, 4, '🦒', 1, 1000, 1000)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `child_lesson_progress` (
                        `childProfileId` TEXT NOT NULL,
                        `lessonId` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `updatedAtTimestamp` INTEGER NOT NULL,
                        `completedAtTimestamp` INTEGER,
                        PRIMARY KEY(`childProfileId`, `lessonId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT INTO `child_lesson_progress` VALUES ('v4-child-123', 'L1_1', 1, 1000, 1000)")
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(testDbName)
            .callback(callback)
            .build()

        val helper = factory.create(config)
        val db = helper.writableDatabase

        // Apply MIGRATION_4_5
        MIGRATION_4_5.migrate(db)

        // Verify sync_outbox table exists
        val outboxCursor = db.query("SELECT * FROM sync_outbox")
        assertNotNull(outboxCursor)
        outboxCursor.close()

        // Verify user_profile data remains intact
        val userCursor = db.query("SELECT * FROM user_profile WHERE id = 'v4-child-123'")
        assertTrue(userCursor.moveToFirst())
        assertEquals("Child V4", userCursor.getString(userCursor.getColumnIndex("name")))
        userCursor.close()

        // Verify lesson progress remains intact
        val lessonCursor = db.query("SELECT * FROM child_lesson_progress WHERE childProfileId = 'v4-child-123'")
        assertTrue(lessonCursor.moveToFirst())
        assertEquals("L1_1", lessonCursor.getString(lessonCursor.getColumnIndex("lessonId")))
        lessonCursor.close()

        db.close()
    }
}
