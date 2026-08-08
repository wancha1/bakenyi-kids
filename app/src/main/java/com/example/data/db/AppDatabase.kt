package com.example.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Badge
import com.example.data.model.ChildBadgeUnlockEntity
import com.example.data.model.ChildDiscoveryEntity
import com.example.data.model.ChildLessonProgressEntity
import com.example.data.model.Lesson
import com.example.data.model.LocationProgressEntity
import com.example.data.model.Phrase
import com.example.data.model.SyncOutboxEntity
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyEntity
import com.example.data.model.World

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `vocabulary_items` (
                `id` TEXT NOT NULL,
                `locationId` TEXT NOT NULL,
                `lugandaTerm` TEXT NOT NULL,
                `englishMeaning` TEXT NOT NULL,
                `phonetic` TEXT NOT NULL,
                `audioPath` TEXT NOT NULL,
                `culturalFact` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )

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
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val existingProfileUuid = "00000000-0000-0000-0000-000000000001"
        val now = System.currentTimeMillis()

        // 1. Migrate user_profile (id Int -> String UUID + timestamps)
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `user_profile_new` (
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

        db.execSQL(
            """
            INSERT INTO `user_profile_new` (id, name, level, stars, coins, streakDays, guideAvatar, currentWorld, createdAtTimestamp, updatedAtTimestamp)
            SELECT
                CASE WHEN CAST(id AS TEXT) = '1' THEN '$existingProfileUuid' ELSE CAST(id AS TEXT) END,
                name, level, stars, coins, streakDays, guideAvatar, currentWorld,
                $now, $now
            FROM `user_profile`
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `user_profile`")
        db.execSQL("ALTER TABLE `user_profile_new` RENAME TO `user_profile`")

        // 2. Migrate child_discoveries (composite primary key [childProfileId, locationKey, itemKey] + timestamps)
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `child_discoveries_new` (
                `childProfileId` TEXT NOT NULL,
                `locationKey` TEXT NOT NULL,
                `itemKey` TEXT NOT NULL,
                `discoveredAtTimestamp` INTEGER NOT NULL,
                `updatedAtTimestamp` INTEGER NOT NULL,
                PRIMARY KEY(`childProfileId`, `locationKey`, `itemKey`)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO `child_discoveries_new` (childProfileId, locationKey, itemKey, discoveredAtTimestamp, updatedAtTimestamp)
            SELECT
                CASE WHEN childProfileId = '1' THEN '$existingProfileUuid' ELSE childProfileId END,
                locationKey, itemKey, discoveredAtTimestamp, discoveredAtTimestamp
            FROM `child_discoveries`
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `child_discoveries`")
        db.execSQL("ALTER TABLE `child_discoveries_new` RENAME TO `child_discoveries`")

        // 3. Migrate location_progress (composite primary key [childProfileId, locationId] + timestamps)
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `location_progress_new` (
                `childProfileId` TEXT NOT NULL,
                `locationId` TEXT NOT NULL,
                `termsMastered` INTEGER NOT NULL,
                `starsEarned` INTEGER NOT NULL,
                `isCompleted` INTEGER NOT NULL,
                `updatedAtTimestamp` INTEGER NOT NULL,
                `completedAtTimestamp` INTEGER,
                PRIMARY KEY(`childProfileId`, `locationId`)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO `location_progress_new` (childProfileId, locationId, termsMastered, starsEarned, isCompleted, updatedAtTimestamp, completedAtTimestamp)
            SELECT
                CASE WHEN childProfileId = '1' THEN '$existingProfileUuid' ELSE childProfileId END,
                locationId, termsMastered, starsEarned, isCompleted,
                $now,
                CASE WHEN isCompleted = 1 THEN $now ELSE NULL END
            FROM `location_progress`
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `location_progress`")
        db.execSQL("ALTER TABLE `location_progress_new` RENAME TO `location_progress`")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()

        // 1. Create child_lesson_progress table
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

        // 2. Migrate existing completed lessons from `lessons` table where isCompleted = 1
        db.execSQL(
            """
            INSERT INTO `child_lesson_progress` (childProfileId, lessonId, isCompleted, updatedAtTimestamp, completedAtTimestamp)
            SELECT
                COALESCE((SELECT id FROM user_profile LIMIT 1), '00000000-0000-0000-0000-000000000001'),
                lessonId,
                isCompleted,
                $now,
                $now
            FROM `lessons`
            WHERE isCompleted = 1
            """.trimIndent()
        )

        // 3. Re-create `lessons` table without `isCompleted` column
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `lessons_new` (
                `lessonId` TEXT NOT NULL,
                `worldId` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `subtitle` TEXT NOT NULL,
                `iconEmoji` TEXT NOT NULL,
                `isLocked` INTEGER NOT NULL,
                `starReward` INTEGER NOT NULL,
                PRIMARY KEY(`lessonId`)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO `lessons_new` (lessonId, worldId, title, subtitle, iconEmoji, isLocked, starReward)
            SELECT lessonId, worldId, title, subtitle, iconEmoji, isLocked, starReward FROM `lessons`
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `lessons`")
        db.execSQL("ALTER TABLE `lessons_new` RENAME TO `lessons`")

        // 4. Create child_badge_unlocks table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `child_badge_unlocks` (
                `childProfileId` TEXT NOT NULL,
                `badgeId` TEXT NOT NULL,
                `isUnlocked` INTEGER NOT NULL,
                `updatedAtTimestamp` INTEGER NOT NULL,
                `unlockedAtTimestamp` INTEGER,
                PRIMARY KEY(`childProfileId`, `badgeId`)
            )
            """.trimIndent()
        )

        // 5. Migrate existing unlocked badges from `badges` table where isUnlocked = 1
        db.execSQL(
            """
            INSERT INTO `child_badge_unlocks` (childProfileId, badgeId, isUnlocked, updatedAtTimestamp, unlockedAtTimestamp)
            SELECT
                COALESCE((SELECT id FROM user_profile LIMIT 1), '00000000-0000-0000-0000-000000000001'),
                id,
                isUnlocked,
                $now,
                $now
            FROM `badges`
            WHERE isUnlocked = 1
            """.trimIndent()
        )

        // 6. Re-create `badges` table without `isUnlocked` column
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `badges_new` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `iconEmoji` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO `badges_new` (id, title, description, iconEmoji)
            SELECT id, title, description, iconEmoji FROM `badges`
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `badges`")
        db.execSQL("ALTER TABLE `badges_new` RENAME TO `badges`")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `sync_outbox` (
                `id` TEXT NOT NULL,
                `childProfileId` TEXT NOT NULL,
                `entityType` TEXT NOT NULL,
                `entityId` TEXT NOT NULL,
                `operation` TEXT NOT NULL,
                `payloadJson` TEXT NOT NULL,
                `createdAtTimestamp` INTEGER NOT NULL,
                `updatedAtTimestamp` INTEGER NOT NULL,
                `attemptCount` INTEGER NOT NULL,
                `lastAttemptAtTimestamp` INTEGER,
                `nextAttemptAtTimestamp` INTEGER,
                `status` TEXT NOT NULL,
                `lastError` TEXT,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }
}

@Database(
    entities = [
        UserProfile::class,
        World::class,
        Lesson::class,
        Phrase::class,
        Badge::class,
        VocabularyEntity::class,
        ChildDiscoveryEntity::class,
        LocationProgressEntity::class,
        ChildLessonProgressEntity::class,
        ChildBadgeUnlockEntity::class,
        SyncOutboxEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bakenyeDao(): BakenyeDao
    abstract fun syncOutboxDao(): SyncOutboxDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    Log.d("DATABASE_DEBUG", "Initializing AppDatabase version 5 with MIGRATIONS 1_2, 2_3, 3_4, 4_5")
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "bakenye_kids_db"
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                        .build()
                    INSTANCE = instance
                    Log.d("DATABASE_DEBUG", "AppDatabase initialized successfully")
                    instance
                } catch (e: Exception) {
                    Log.e("DATABASE_DEBUG", "Error initializing AppDatabase", e)
                    Log.e("BAKENYE_CRASH", "Database initialization failure", e)
                    throw e
                }
            }
        }
    }
}
