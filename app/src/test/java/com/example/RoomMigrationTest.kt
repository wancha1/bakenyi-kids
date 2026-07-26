package com.example

import android.content.Context
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.db.AppDatabase
import com.example.data.db.MIGRATION_1_2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomMigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migration1To2_preservesData_andCreatesNewTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Create database at version 1 and insert legacy user progress
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
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

            execSQL(
                """
                INSERT INTO `user_profile` (`id`, `name`, `level`, `stars`, `coins`, `streakDays`, `guideAvatar`, `currentWorld`)
                VALUES (1, 'Kato Legacy Explorer', 5, 250, 600, 7, '🦁', 2)
                """.trimIndent()
            )
            close()
        }

        // 2. Run MIGRATION_1_2 and validate schema
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        // 3. Verify existing user_profile data from Version 1 is preserved
        val cursor = db.query("SELECT * FROM user_profile WHERE id = 1")
        assertTrue("User profile from v1 should exist", cursor.moveToFirst())
        val nameIndex = cursor.getColumnIndex("name")
        val starsIndex = cursor.getColumnIndex("stars")
        assertEquals("Kato Legacy Explorer", cursor.getString(nameIndex))
        assertEquals(250, cursor.getInt(starsIndex))
        cursor.close()

        // 4. Verify new tables created in MIGRATION_1_2 exist
        val vocabularyCursor = db.query("SELECT count(*) FROM vocabulary_items")
        assertNotNull(vocabularyCursor)
        vocabularyCursor.close()

        val discoveriesCursor = db.query("SELECT count(*) FROM child_discoveries")
        assertNotNull(discoveriesCursor)
        discoveriesCursor.close()

        val progressCursor = db.query("SELECT count(*) FROM location_progress")
        assertNotNull(progressCursor)
        progressCursor.close()

        db.close()
    }
}
