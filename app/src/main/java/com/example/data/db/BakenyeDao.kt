package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Badge
import com.example.data.model.ChildDiscoveryEntity
import com.example.data.model.Lesson
import com.example.data.model.LocationProgressEntity
import com.example.data.model.Phrase
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyEntity
import com.example.data.model.World
import kotlinx.coroutines.flow.Flow

@Dao
interface BakenyeDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("SELECT * FROM worlds ORDER BY worldId ASC")
    fun getAllWorlds(): Flow<List<World>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWorlds(worlds: List<World>)

    @Query("SELECT * FROM lessons WHERE worldId = :worldId ORDER BY lessonId ASC")
    fun getLessonsForWorld(worldId: Int): Flow<List<Lesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLessons(lessons: List<Lesson>)

    @Query("UPDATE lessons SET isCompleted = 1 WHERE lessonId = :lessonId")
    suspend fun markLessonCompleted(lessonId: String)

    @Query("SELECT * FROM phrases WHERE worldId = :worldId")
    fun getPhrasesForWorld(worldId: Int): Flow<List<Phrase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePhrases(phrases: List<Phrase>)

    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<Badge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBadges(badges: List<Badge>)

    @Query("UPDATE user_profile SET stars = stars + :addStars, coins = coins + :addCoins WHERE id = 1")
    suspend fun rewardUser(addStars: Int, addCoins: Int)

    // World Engine Vocabulary & Progress
    @Query("SELECT * FROM vocabulary_items WHERE locationId = :locationId")
    fun getVocabularyForLocation(locationId: String): Flow<List<VocabularyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVocabularyItems(items: List<VocabularyEntity>)

    @Query("SELECT * FROM child_discoveries WHERE locationKey = :locationKey")
    fun getChildDiscoveries(locationKey: String): Flow<List<ChildDiscoveryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordDiscovery(discovery: ChildDiscoveryEntity)

    @Query("SELECT * FROM location_progress WHERE locationId = :locationId")
    fun getLocationProgress(locationId: String): Flow<LocationProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocationProgress(progress: LocationProgressEntity)
}
