package com.example.data.repository

import com.example.data.db.BakenyeDao
import com.example.data.model.Badge
import com.example.data.model.ChildDiscoveryEntity
import com.example.data.model.Lesson
import com.example.data.model.LocationProgressEntity
import com.example.data.model.Phrase
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyEntity
import com.example.data.model.World
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class BakenyeRepository(private val dao: BakenyeDao) {

    val userProfile: Flow<UserProfile?> = dao.getPrimaryUserProfile()
    val worlds: Flow<List<World>> = dao.getAllWorlds()
    val badges: Flow<List<Badge>> = dao.getAllBadges()

    fun getUserProfileById(childProfileId: String): Flow<UserProfile?> = dao.getUserProfileById(childProfileId)
    fun getLessonsForWorld(worldId: Int): Flow<List<Lesson>> = dao.getLessonsForWorld(worldId)
    fun getPhrasesForWorld(worldId: Int): Flow<List<Phrase>> = dao.getPhrasesForWorld(worldId)
    fun getVocabularyForLocation(locationId: String): Flow<List<VocabularyEntity>> = dao.getVocabularyForLocation(locationId)

    fun getChildDiscoveries(childProfileId: String, locationKey: String): Flow<List<ChildDiscoveryEntity>> =
        dao.getChildDiscoveries(childProfileId, locationKey)

    fun getLocationProgress(childProfileId: String, locationId: String): Flow<LocationProgressEntity?> =
        dao.getLocationProgress(childProfileId, locationId)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getChildDiscoveries(locationKey: String): Flow<List<ChildDiscoveryEntity>> {
        return userProfile.flatMapLatest { profile ->
            val profileId = profile?.id ?: return@flatMapLatest flowOf(emptyList())
            dao.getChildDiscoveries(profileId, locationKey)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLocationProgress(locationId: String): Flow<LocationProgressEntity?> {
        return userProfile.flatMapLatest { profile ->
            val profileId = profile?.id ?: return@flatMapLatest flowOf(null)
            dao.getLocationProgress(profileId, locationId)
        }
    }

    suspend fun recordDiscovery(childProfileId: String, locationKey: String, speciesKey: String) {
        val now = System.currentTimeMillis()
        val discovery = ChildDiscoveryEntity(
            childProfileId = childProfileId,
            locationKey = locationKey,
            itemKey = speciesKey,
            discoveredAtTimestamp = now,
            updatedAtTimestamp = now
        )
        dao.recordDiscovery(discovery)
    }

    suspend fun recordDiscovery(locationKey: String, speciesKey: String) {
        val activeProfile = dao.getFirstUserProfile()
        if (activeProfile != null) {
            recordDiscovery(activeProfile.id, locationKey, speciesKey)
        }
    }

    suspend fun updateLocationProgress(
        childProfileId: String,
        locationId: String,
        wordsMastered: Int,
        stars: Int,
        isCompleted: Boolean = false
    ) {
        val now = System.currentTimeMillis()
        val existingProgress = dao.getLocationProgressOnce(childProfileId, locationId)
        val completedAt = if (isCompleted) (existingProgress?.completedAtTimestamp ?: now) else existingProgress?.completedAtTimestamp
        dao.saveLocationProgress(
            LocationProgressEntity(
                childProfileId = childProfileId,
                locationId = locationId,
                termsMastered = wordsMastered,
                starsEarned = stars,
                isCompleted = isCompleted,
                updatedAtTimestamp = now,
                completedAtTimestamp = completedAt
            )
        )
    }

    suspend fun updateLocationProgress(locationId: String, wordsMastered: Int, stars: Int) {
        val activeProfile = dao.getFirstUserProfile()
        if (activeProfile != null) {
            updateLocationProgress(activeProfile.id, locationId, wordsMastered, stars)
        }
    }

    suspend fun completeLesson(childProfileId: String, lessonId: String, starReward: Int, coinReward: Int) {
        dao.markLessonCompleted(lessonId)
        dao.rewardUser(childProfileId = childProfileId, addStars = starReward, addCoins = coinReward)
    }

    suspend fun completeLesson(lessonId: String, starReward: Int, coinReward: Int) {
        val activeProfile = dao.getFirstUserProfile()
        if (activeProfile != null) {
            completeLesson(activeProfile.id, lessonId, starReward, coinReward)
        }
    }

    suspend fun seedInitialDataIfEmpty(): UserProfile {
        var profile = dao.getFirstUserProfile()
        if (profile == null) {
            val now = System.currentTimeMillis()
            profile = UserProfile(
                id = UUID.randomUUID().toString(),
                name = "Kato's Adventure",
                level = 4,
                stars = 125,
                coins = 450,
                streakDays = 3,
                guideAvatar = "🦒",
                currentWorld = 1,
                createdAtTimestamp = now,
                updatedAtTimestamp = now
            )
            dao.saveUserProfile(profile)
        }

        if (dao.getWorldCount() == 0) {
            val initialWorlds = listOf(
                World(1, "World 1", "Alphabet & Sounds", "🦁", isUnlocked = true, totalLessons = 4),
                World(2, "World 2", "Family & Home", "🏠", isUnlocked = true, totalLessons = 4),
                World(3, "World 3", "Wild & Farm Animals", "🐘", isUnlocked = true, totalLessons = 4),
                World(4, "World 4", "Traditional Food", "🍌", isUnlocked = false, totalLessons = 4),
                World(5, "World 5", "Numbers & Counting", "🔢", isUnlocked = false, totalLessons = 4),
                World(6, "World 6", "Colours & Shapes", "🎨", isUnlocked = false, totalLessons = 4),
                World(7, "World 7", "Warm Greetings", "🤝", isUnlocked = false, totalLessons = 4),
                World(8, "World 8", "Simple Conversations", "💬", isUnlocked = false, totalLessons = 4),
                World(9, "World 9", "Songs & Cultural Rhymes", "🎵", isUnlocked = false, totalLessons = 3),
                World(10, "World 10", "Folk Stories & Legends", "📖", isUnlocked = false, totalLessons = 3),
                World(11, "World 11", "Wisdom & Proverbs", "👑", isUnlocked = false, totalLessons = 3)
            )
            dao.saveWorlds(initialWorlds)
        }

        if (dao.getVocabularyCount() == 0) {
            val fishingVocabulary = listOf(
                VocabularyEntity("V_ENSOMBA", "FISHING_AREA", "Ensomba", "Fish (General)", "En-sohm-bah", "audio_ensomba", "Fish are the heart of life along Lake Kyoga Shores."),
                VocabularyEntity("V_MUKENE", "FISHING_AREA", "Mukene", "Silver Cyprinid Fish", "Moo-keh-neh", "audio_mukene", "Tiny silver fish harvested under moonlight with lanterns."),
                VocabularyEntity("V_NGEGE", "FISHING_AREA", "Ngege", "Tilapia Fish", "Ngeh-gheh", "audio_ngege", "Prized freshwater fish cooked in rich sesame paste."),
                VocabularyEntity("V_ERYATO", "FISHING_AREA", "Eryato", "Traditional Wooden Canoe", "Eh-ryah-toh", "audio_eryato", "Handcrafted canoe hollowed from sacred MVule trees."),
                VocabularyEntity("V_EKITIMBA", "FISHING_AREA", "Ekitimba", "Woven Fishing Net", "Eh-kee-teem-bah", "audio_ekitimba", "Hand-knotted reed mesh passed down through fisherman elders.")
            )
            dao.saveVocabularyItems(fishingVocabulary)
        }

        if (dao.getLocationProgressCount(profile.id, "FISHING_AREA") == 0) {
            val now = System.currentTimeMillis()
            dao.saveLocationProgress(
                LocationProgressEntity(
                    childProfileId = profile.id,
                    locationId = "FISHING_AREA",
                    termsMastered = 1,
                    starsEarned = 3,
                    isCompleted = false,
                    updatedAtTimestamp = now,
                    completedAtTimestamp = null
                )
            )
        }

        if (dao.getLessonCount() == 0) {
            val initialLessons = listOf(
                Lesson("L1_1", 1, "Lesson 1: Vowels", "A, E, I, O, U in Bakenye", "🅰️", isCompleted = true, isLocked = false, starReward = 3),
                Lesson("L1_2", 1, "Lesson 2: Consonants & Sounds", "K, B, N, Y sound combos", "🎵", isCompleted = false, isLocked = false, starReward = 3),
                Lesson("L1_3", 1, "Lesson 3: Animal Sounds", "How animals speak", "🌴", isCompleted = false, isLocked = true, starReward = 3),
                Lesson("L1_4", 1, "Lesson 4: Alphabet Challenge", "Combine sounds!", "🏆", isCompleted = false, isLocked = true, starReward = 5),
                Lesson("L2_1", 2, "Lesson 1: Parents", "Father & Mother in Bakenye", "👨‍👩‍👦", isCompleted = false, isLocked = false, starReward = 3),
                Lesson("L2_2", 2, "Lesson 2: Siblings & Grandparents", "Brother, Sister, Jjaja", "👴", isCompleted = false, isLocked = false, starReward = 3)
            )
            dao.saveLessons(initialLessons)
        }

        if (dao.getPhraseCount() == 0) {
            val initialPhrases = listOf(
                Phrase("P1", 1, "A - Abalimi", "Farmers", "Ah-bah-lee-mee", "🌾", "Alphabet", "Pronunciation guide: rolled r"),
                Phrase("P2", 1, "E - Enkoko", "Chicken", "En-koh-koh", "🐔", "Alphabet", "Pronunciation: clear vowel"),
                Phrase("P3", 1, "I - Inzu", "House / Home", "Een-zoo", "🏠", "Alphabet", "Soft z sound"),
                Phrase("P4", 1, "O - Omwana", "Child", "Oh-mwah-nah", "👶", "Alphabet", "Warm tone"),
                Phrase("P5", 1, "U - Urukundo", "Love", "Oo-roo-koon-doh", "❤️", "Alphabet", "Expressive"),
                Phrase("P6", 2, "Taata", "Father", "Tah-tah", "👨", "Family", "Respectful form"),
                Phrase("P7", 2, "Maama", "Mother", "Mah-mah", "👩", "Family", "Warm form"),
                Phrase("P8", 2, "Jjaja", "Grandparent", "Jjah-jjah", "👵", "Family", "Elder title"),
                Phrase("P9", 2, "Mugenzi", "Friend / Companion", "Moo-ghen-zee", "🤝", "Family", "Common greeting term")
            )
            dao.savePhrases(initialPhrases)
        }

        if (dao.getBadgeCount() == 0) {
            val initialBadges = listOf(
                Badge("B1", "Bakenye Explorer", "Completed first lesson in World 1", "🧭", isUnlocked = true),
                Badge("B2", "Pronunciation Master", "Listened to 10 Bakenye word audios", "🎧", isUnlocked = true),
                Badge("B3", "3-Day Safari Streak", "Practiced 3 days in a row", "🔥", isUnlocked = true),
                Badge("B4", "Culture Keeper", "Unlocked 5 Bakenye traditional proverbs", "🛡️", isUnlocked = false),
                Badge("B5", "Community Champion", "Reached Level 5 in Kato's Journey", "🌟", isUnlocked = false)
            )
            dao.saveBadges(initialBadges)
        }

        return profile
    }
}
