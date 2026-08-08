package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.db.AppDatabase
import com.example.data.db.BakenyeDao
import com.example.data.db.SyncOutboxDao
import com.example.data.model.Badge
import com.example.data.model.BadgeWithProgress
import com.example.data.model.ChildBadgeUnlockEntity
import com.example.data.model.ChildDiscoveryEntity
import com.example.data.model.ChildLessonProgressEntity
import com.example.data.model.Lesson
import com.example.data.model.LessonWithProgress
import com.example.data.model.LocationProgressEntity
import com.example.data.model.OutboxPayloadSerializer
import com.example.data.model.Phrase
import com.example.data.model.SyncOutboxEntity
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyEntity
import com.example.data.model.World
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class BakenyeRepository(
    private val dao: BakenyeDao,
    private val outboxDao: SyncOutboxDao? = null,
    private val db: AppDatabase? = null
) {
    constructor(db: AppDatabase) : this(db.bakenyeDao(), db.syncOutboxDao(), db)

    val userProfile: Flow<UserProfile?> = dao.getPrimaryUserProfile()
    val worlds: Flow<List<World>> = dao.getAllWorlds()

    @OptIn(ExperimentalCoroutinesApi::class)
    val badges: Flow<List<BadgeWithProgress>> = userProfile.flatMapLatest { profile ->
        if (profile == null) {
            flowOf(emptyList())
        } else {
            getBadgesForChild(profile.id)
        }
    }

    fun getBadgesForChild(childProfileId: String): Flow<List<BadgeWithProgress>> {
        return combine(dao.getAllBadges(), dao.getUnlockedBadgesForChild(childProfileId)) { badgesList, unlocks ->
            val unlockedIds = unlocks.filter { it.isUnlocked }.map { it.badgeId }.toSet()
            badgesList.map { badge ->
                BadgeWithProgress(
                    badge = badge,
                    isUnlocked = unlockedIds.contains(badge.id)
                )
            }
        }
    }

    fun getUserProfileById(childProfileId: String): Flow<UserProfile?> = dao.getUserProfileById(childProfileId)

    fun getLessonsForWorld(childProfileId: String, worldId: Int): Flow<List<LessonWithProgress>> {
        return combine(
            dao.getLessonsForWorld(worldId),
            dao.getLessonProgressForChild(childProfileId)
        ) { lessonsList, progressList ->
            val completedIds = progressList.filter { it.isCompleted }.map { it.lessonId }.toSet()
            lessonsList.map { lesson ->
                LessonWithProgress(
                    lesson = lesson,
                    isCompleted = completedIds.contains(lesson.lessonId)
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLessonsForWorld(worldId: Int): Flow<List<LessonWithProgress>> {
        return userProfile.flatMapLatest { profile ->
            if (profile == null) {
                flowOf(emptyList())
            } else {
                getLessonsForWorld(profile.id, worldId)
            }
        }
    }

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

    suspend fun saveUserProfile(profile: UserProfile) {
        val now = System.currentTimeMillis()
        val updatedProfile = profile.copy(updatedAtTimestamp = now)
        val payload = OutboxPayloadSerializer.serializeUserProfile(updatedProfile)
        performWrite(
            childProfileId = updatedProfile.id,
            entityType = "USER_PROFILE",
            entityId = updatedProfile.id,
            operation = "UPDATE",
            payloadJson = payload,
            timestamp = now
        ) {
            dao.saveUserProfile(updatedProfile)
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
        val payload = OutboxPayloadSerializer.serializeDiscovery(discovery)
        val entityId = "$locationKey:$speciesKey"
        performWrite(
            childProfileId = childProfileId,
            entityType = "CHILD_DISCOVERY",
            entityId = entityId,
            operation = "INSERT",
            payloadJson = payload,
            timestamp = now
        ) {
            dao.recordDiscovery(discovery)
        }
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
        val progress = LocationProgressEntity(
            childProfileId = childProfileId,
            locationId = locationId,
            termsMastered = wordsMastered,
            starsEarned = stars,
            isCompleted = isCompleted,
            updatedAtTimestamp = now,
            completedAtTimestamp = completedAt
        )
        val payload = OutboxPayloadSerializer.serializeLocationProgress(progress)
        performWrite(
            childProfileId = childProfileId,
            entityType = "LOCATION_PROGRESS",
            entityId = locationId,
            operation = "UPDATE",
            payloadJson = payload,
            timestamp = now
        ) {
            dao.saveLocationProgress(progress)
        }
    }

    suspend fun updateLocationProgress(locationId: String, wordsMastered: Int, stars: Int) {
        val activeProfile = dao.getFirstUserProfile()
        if (activeProfile != null) {
            updateLocationProgress(activeProfile.id, locationId, wordsMastered, stars)
        }
    }

    suspend fun completeLesson(childProfileId: String, lessonId: String, starReward: Int, coinReward: Int) {
        val now = System.currentTimeMillis()
        val lessonProgress = ChildLessonProgressEntity(
            childProfileId = childProfileId,
            lessonId = lessonId,
            isCompleted = true,
            updatedAtTimestamp = now,
            completedAtTimestamp = now
        )
        val lessonPayload = OutboxPayloadSerializer.serializeLessonProgress(lessonProgress)

        val database = db
        if (database != null && outboxDao != null) {
            database.withTransaction {
                dao.saveLessonProgress(lessonProgress)
                enqueueOrCoalesceOutbox(childProfileId, "CHILD_LESSON_PROGRESS", lessonId, "UPDATE", lessonPayload, now)
                dao.rewardUser(childProfileId = childProfileId, addStars = starReward, addCoins = coinReward, updatedAt = now)
                val updatedProfile = dao.getUserProfileByIdOnce(childProfileId)
                if (updatedProfile != null) {
                    val profilePayload = OutboxPayloadSerializer.serializeUserProfile(updatedProfile)
                    enqueueOrCoalesceOutbox(childProfileId, "USER_PROFILE", childProfileId, "UPDATE", profilePayload, now)
                }
            }
        } else {
            dao.saveLessonProgress(lessonProgress)
            dao.rewardUser(childProfileId = childProfileId, addStars = starReward, addCoins = coinReward, updatedAt = now)
            val updatedProfile = dao.getUserProfileByIdOnce(childProfileId)
            if (updatedProfile != null && outboxDao != null) {
                val profilePayload = OutboxPayloadSerializer.serializeUserProfile(updatedProfile)
                enqueueOrCoalesceOutbox(childProfileId, "USER_PROFILE", childProfileId, "UPDATE", profilePayload, now)
            }
        }
    }

    suspend fun completeLesson(lessonId: String, starReward: Int, coinReward: Int) {
        val activeProfile = dao.getFirstUserProfile()
        if (activeProfile != null) {
            completeLesson(activeProfile.id, lessonId, starReward, coinReward)
        }
    }

    suspend fun unlockBadge(childProfileId: String, badgeId: String) {
        val now = System.currentTimeMillis()
        val unlock = ChildBadgeUnlockEntity(
            childProfileId = childProfileId,
            badgeId = badgeId,
            isUnlocked = true,
            updatedAtTimestamp = now,
            unlockedAtTimestamp = now
        )
        val payload = OutboxPayloadSerializer.serializeBadgeUnlock(unlock)
        performWrite(
            childProfileId = childProfileId,
            entityType = "CHILD_BADGE_UNLOCK",
            entityId = badgeId,
            operation = "UPDATE",
            payloadJson = payload,
            timestamp = now
        ) {
            dao.saveBadgeUnlock(unlock)
        }
    }

    suspend fun unlockBadge(badgeId: String) {
        val activeProfile = dao.getFirstUserProfile()
        if (activeProfile != null) {
            unlockBadge(activeProfile.id, badgeId)
        }
    }

    private suspend fun performWrite(
        childProfileId: String,
        entityType: String,
        entityId: String,
        operation: String,
        payloadJson: String,
        timestamp: Long,
        writeBlock: suspend () -> Unit
    ) {
        val database = db
        if (database != null && outboxDao != null) {
            database.withTransaction {
                writeBlock()
                enqueueOrCoalesceOutbox(childProfileId, entityType, entityId, operation, payloadJson, timestamp)
            }
        } else {
            writeBlock()
            if (outboxDao != null) {
                enqueueOrCoalesceOutbox(childProfileId, entityType, entityId, operation, payloadJson, timestamp)
            }
        }
    }

    private suspend fun enqueueOrCoalesceOutbox(
        childProfileId: String,
        entityType: String,
        entityId: String,
        operation: String,
        payloadJson: String,
        timestamp: Long
    ) {
        val outbox = outboxDao ?: return
        val pending = outbox.getPendingEventForEntity(childProfileId, entityType, entityId)
        if (pending != null) {
            if (operation == "DELETE" && pending.operation == "INSERT") {
                outbox.deleteOutboxEvent(pending.id)
            } else {
                val updatedOp = if (pending.operation == "INSERT" && operation == "UPDATE") "INSERT" else operation
                val coalesced = pending.copy(
                    operation = updatedOp,
                    payloadJson = payloadJson,
                    updatedAtTimestamp = timestamp
                )
                outbox.updateOutboxEvent(coalesced)
            }
        } else {
            val newEvent = SyncOutboxEntity(
                id = UUID.randomUUID().toString(),
                childProfileId = childProfileId,
                entityType = entityType,
                entityId = entityId,
                operation = operation,
                payloadJson = payloadJson,
                createdAtTimestamp = timestamp,
                updatedAtTimestamp = timestamp,
                attemptCount = 0,
                status = "PENDING"
            )
            outbox.insertOutboxEvent(newEvent)
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
                Lesson("L1_1", 1, "Lesson 1: Vowels", "A, E, I, O, U in Bakenye", "🅰️", isLocked = false, starReward = 3),
                Lesson("L1_2", 1, "Lesson 2: Consonants & Sounds", "K, B, N, Y sound combos", "🎵", isLocked = false, starReward = 3),
                Lesson("L1_3", 1, "Lesson 3: Animal Sounds", "How animals speak", "🌴", isLocked = true, starReward = 3),
                Lesson("L1_4", 1, "Lesson 4: Alphabet Challenge", "Combine sounds!", "🏆", isLocked = true, starReward = 5),
                Lesson("L2_1", 2, "Lesson 1: Parents", "Father & Mother in Bakenye", "👨‍👩‍👦", isLocked = false, starReward = 3),
                Lesson("L2_2", 2, "Lesson 2: Siblings & Grandparents", "Brother, Sister, Jjaja", "👴", isLocked = false, starReward = 3)
            )
            dao.saveLessons(initialLessons)
        }

        if (dao.getLessonProgressCount(profile.id, "L1_1") == 0) {
            val now = System.currentTimeMillis()
            dao.saveLessonProgress(
                ChildLessonProgressEntity(
                    childProfileId = profile.id,
                    lessonId = "L1_1",
                    isCompleted = true,
                    updatedAtTimestamp = now,
                    completedAtTimestamp = now
                )
            )
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
                Badge("B1", "Bakenye Explorer", "Completed first lesson in World 1", "🧭"),
                Badge("B2", "Pronunciation Master", "Listened to 10 Bakenye word audios", "🎧"),
                Badge("B3", "3-Day Safari Streak", "Practiced 3 days in a row", "🔥"),
                Badge("B4", "Culture Keeper", "Unlocked 5 Bakenye traditional proverbs", "🛡️"),
                Badge("B5", "Community Champion", "Reached Level 5 in Kato's Journey", "🌟")
            )
            dao.saveBadges(initialBadges)
        }

        if (dao.getBadgeUnlockCount(profile.id, "B1") == 0) {
            val now = System.currentTimeMillis()
            val initialUnlocks = listOf("B1", "B2", "B3")
            for (badgeId in initialUnlocks) {
                dao.saveBadgeUnlock(
                    ChildBadgeUnlockEntity(
                        childProfileId = profile.id,
                        badgeId = badgeId,
                        isUnlocked = true,
                        updatedAtTimestamp = now,
                        unlockedAtTimestamp = now
                    )
                )
            }
        }

        return profile
    }
}
