package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Badge
import com.example.data.model.ChildBadgeUnlockEntity
import com.example.data.model.ChildDiscoveryEntity
import com.example.data.model.ChildLessonProgressEntity
import com.example.data.model.Lesson
import com.example.data.model.LocationProgressEntity
import com.example.data.model.Phrase
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyEntity
import com.example.data.model.World
import kotlinx.coroutines.flow.Flow

@Dao
interface BakenyeDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getPrimaryUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = :childProfileId")
    fun getUserProfileById(childProfileId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = :childProfileId")
    suspend fun getUserProfileByIdOnce(childProfileId: String): UserProfile?

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getFirstUserProfile(): UserProfile?

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun getUserProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("SELECT * FROM worlds ORDER BY worldId ASC")
    fun getAllWorlds(): Flow<List<World>>

    @Query("SELECT COUNT(*) FROM worlds")
    suspend fun getWorldCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWorlds(worlds: List<World>)

    @Query("SELECT * FROM lessons WHERE worldId = :worldId ORDER BY lessonId ASC")
    fun getLessonsForWorld(worldId: Int): Flow<List<Lesson>>

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getLessonCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLessons(lessons: List<Lesson>)

    @Query("SELECT * FROM child_lesson_progress WHERE childProfileId = :childProfileId")
    fun getLessonProgressForChild(childProfileId: String): Flow<List<ChildLessonProgressEntity>>

    @Query("SELECT * FROM child_lesson_progress WHERE childProfileId = :childProfileId AND lessonId = :lessonId")
    fun getLessonProgress(childProfileId: String, lessonId: String): Flow<ChildLessonProgressEntity?>

    @Query("SELECT COUNT(*) FROM child_lesson_progress WHERE childProfileId = :childProfileId AND lessonId = :lessonId")
    suspend fun getLessonProgressCount(childProfileId: String, lessonId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLessonProgress(progress: ChildLessonProgressEntity)

    @Query("SELECT * FROM phrases WHERE worldId = :worldId")
    fun getPhrasesForWorld(worldId: Int): Flow<List<Phrase>>

    @Query("SELECT COUNT(*) FROM phrases")
    suspend fun getPhraseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePhrases(phrases: List<Phrase>)

    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<Badge>>

    @Query("SELECT COUNT(*) FROM badges")
    suspend fun getBadgeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBadges(badges: List<Badge>)

    @Query("SELECT * FROM child_badge_unlocks WHERE childProfileId = :childProfileId")
    fun getUnlockedBadgesForChild(childProfileId: String): Flow<List<ChildBadgeUnlockEntity>>

    @Query("SELECT * FROM child_badge_unlocks WHERE childProfileId = :childProfileId AND badgeId = :badgeId")
    fun getBadgeUnlock(childProfileId: String, badgeId: String): Flow<ChildBadgeUnlockEntity?>

    @Query("SELECT COUNT(*) FROM child_badge_unlocks WHERE childProfileId = :childProfileId AND badgeId = :badgeId")
    suspend fun getBadgeUnlockCount(childProfileId: String, badgeId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBadgeUnlock(unlock: ChildBadgeUnlockEntity)

    @Query("UPDATE user_profile SET stars = stars + :addStars, coins = coins + :addCoins, updatedAtTimestamp = :updatedAt WHERE id = :childProfileId")
    suspend fun rewardUser(childProfileId: String, addStars: Int, addCoins: Int, updatedAt: Long = System.currentTimeMillis())

    // World Engine Vocabulary & Progress
    @Query("SELECT * FROM vocabulary_items WHERE locationId = :locationId")
    fun getVocabularyForLocation(locationId: String): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary_items")
    suspend fun getVocabularyCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVocabularyItems(items: List<VocabularyEntity>)

    @Query("SELECT * FROM child_discoveries WHERE childProfileId = :childProfileId AND locationKey = :locationKey")
    fun getChildDiscoveries(childProfileId: String, locationKey: String): Flow<List<ChildDiscoveryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordDiscovery(discovery: ChildDiscoveryEntity)

    @Query("SELECT * FROM location_progress WHERE childProfileId = :childProfileId AND locationId = :locationId")
    fun getLocationProgress(childProfileId: String, locationId: String): Flow<LocationProgressEntity?>

    @Query("SELECT * FROM location_progress WHERE childProfileId = :childProfileId AND locationId = :locationId")
    suspend fun getLocationProgressOnce(childProfileId: String, locationId: String): LocationProgressEntity?

    @Query("SELECT COUNT(*) FROM location_progress WHERE childProfileId = :childProfileId AND locationId = :locationId")
    suspend fun getLocationProgressCount(childProfileId: String, locationId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocationProgress(progress: LocationProgressEntity)
}
