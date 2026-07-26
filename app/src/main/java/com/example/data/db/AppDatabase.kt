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
    version = 2,
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
                    Log.d("DATABASE_DEBUG", "Initializing AppDatabase version 2")
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "bakenye_kids_db"
                    ).fallbackToDestructiveMigration(dropAllTables = true).build()
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
