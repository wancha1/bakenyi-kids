package com.example.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Badge
import com.example.data.model.ChildDiscoveryEntity
import com.example.data.model.Lesson
import com.example.data.model.LocationProgressEntity
import com.example.data.model.Phrase
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

@Database(
    entities = [
        UserProfile::class,
        World::class,
        Lesson::class,
        Phrase::class,
        Badge::class,
        VocabularyEntity::class,
        ChildDiscoveryEntity::class,
        LocationProgressEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bakenyeDao(): BakenyeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    Log.d("DATABASE_DEBUG", "Initializing AppDatabase version 3 with MIGRATION_1_2 and MIGRATION_2_3")
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "bakenye_kids_db"
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
