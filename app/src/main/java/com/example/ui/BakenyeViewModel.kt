package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.BadgeWithProgress
import com.example.data.model.Lesson
import com.example.data.model.Phrase
import com.example.data.model.UserProfile
import com.example.data.model.World
import com.example.data.repository.BakenyeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BakenyeUiState(
    val profile: UserProfile = UserProfile(),
    val worlds: List<World> = emptyList(),
    val selectedWorldId: Int = 1,
    val currentLessons: List<Lesson> = emptyList(),
    val currentPhrases: List<Phrase> = emptyList(),
    val badges: List<BadgeWithProgress> = emptyList(),
    val activeTab: NavigationTab = NavigationTab.LEARN,
    val activeLesson: Lesson? = null,
    val activePhraseIndex: Int = 0,
    val isPlayingAudio: Boolean = false,
    val showRewardModal: Boolean = false,
    val earnedRewardStars: Int = 0,
    val showOpeningIntro: Boolean = true,
    val currentIntroStep: Int = 0,
    val guideMessage: String = "Oli otya! I am Kato the Otter! Ready to explore Lake Kyoga's hidden story islands?",
    val selectedQuestMode: String = "ISLAND_EXPLORER",
    val targetAgeGroup: String = "5-7",
    val selectedLocationNode: String = "ALL"
)

enum class NavigationTab {
    LEARN, RANK, GIFT, AVATAR, SETTINGS
}

class BakenyeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BakenyeRepository
    private val syncManager: com.example.sync.SyncManager

    val syncState: StateFlow<com.example.sync.SyncState>

    private val _selectedWorldId = MutableStateFlow(1)
    private val _activeTab = MutableStateFlow(NavigationTab.LEARN)
    private val _activeLesson = MutableStateFlow<Lesson?>(null)
    private val _activePhraseIndex = MutableStateFlow(0)
    private val _isPlayingAudio = MutableStateFlow(false)
    private val _showRewardModal = MutableStateFlow(false)
    private val _earnedRewardStars = MutableStateFlow(0)
    private val _showOpeningIntro = MutableStateFlow(true)
    private val _currentIntroStep = MutableStateFlow(0)
    private val _guideMessage = MutableStateFlow("Oli otya! I am Kato the Otter! Ready to explore Lake Kyoga's hidden story islands?")
    private val _selectedQuestMode = MutableStateFlow("ISLAND_EXPLORER")
    private val _targetAgeGroup = MutableStateFlow("5-7")
    private val _selectedLocationNode = MutableStateFlow("ALL")

    val uiState: StateFlow<BakenyeUiState>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BakenyeRepository(database)
        syncManager = com.example.sync.SyncManager(
            outboxDao = database.syncOutboxDao(),
            transport = com.example.sync.SyncTransportProvider.getTransport(),
            connectivityMonitor = com.example.sync.ConnectivityMonitor(application)
        )
        syncState = syncManager.syncState

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        val dbState = combine(
            repository.userProfile,
            repository.worlds,
            repository.badges
        ) { profile, worlds, badges ->
            Triple(profile ?: UserProfile(), worlds, badges)
        }

        val sessionState = combine(
            _selectedWorldId,
            _activeTab,
            _activeLesson,
            _activePhraseIndex
        ) { worldId, tab, activeLesson, phraseIdx ->
            Quad(worldId, tab, activeLesson, phraseIdx)
        }

        val rewardState = combine(
            _isPlayingAudio,
            _showRewardModal,
            _earnedRewardStars
        ) { playing, showReward, earnedStars ->
            Triple(playing, showReward, earnedStars)
        }

        val introState = combine(
            _showOpeningIntro,
            _currentIntroStep,
            _guideMessage,
            _selectedQuestMode
        ) { showIntro, step, guideMsg, questMode ->
            Quad(showIntro, step, guideMsg, questMode)
        }

        val adaptiveState = combine(
            _targetAgeGroup,
            _selectedLocationNode
        ) { ageGroup, locNode ->
            Pair(ageGroup, locNode)
        }

        uiState = combine(dbState, sessionState, rewardState, introState, adaptiveState) { db, session, reward, intro, adaptive ->
            BakenyeUiState(
                profile = db.first,
                worlds = db.second,
                badges = db.third,
                selectedWorldId = session.first,
                activeTab = session.second,
                activeLesson = session.third,
                activePhraseIndex = session.fourth,
                isPlayingAudio = reward.first,
                showRewardModal = reward.second,
                earnedRewardStars = reward.third,
                showOpeningIntro = intro.first,
                currentIntroStep = intro.second,
                guideMessage = intro.third,
                selectedQuestMode = intro.fourth,
                targetAgeGroup = adaptive.first,
                selectedLocationNode = adaptive.second
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BakenyeUiState()
        )
    }

    fun nextIntroStep() {
        if (_currentIntroStep.value < 4) {
            _currentIntroStep.value += 1
        } else {
            _showOpeningIntro.value = false
        }
    }

    fun skipIntroToWorld() {
        _showOpeningIntro.value = false
    }

    fun replayOpeningIntro() {
        _currentIntroStep.value = 0
        _showOpeningIntro.value = true
    }

    fun selectQuestMode(mode: String) {
        _selectedQuestMode.value = mode
        when (mode) {
            "ISLAND_EXPLORER" -> _guideMessage.value = "Awesome! We'll explore Lake Kyoga's islands node by node!"
            "STORY_FINDER" -> _guideMessage.value = "Great choice! Elder stories await under the Great Baobab!"
            "HERO_QUEST" -> _guideMessage.value = "Hero status! Collect stars and unlock traditional Bakenye badges!"
        }
    }

    fun selectAgeGroup(group: String) {
        _targetAgeGroup.value = group
        when (group) {
            "5-7" -> _guideMessage.value = "Ages 5-7 Mode: High visuals, audio-first stories with Kato! 🦦"
            "8-10" -> _guideMessage.value = "Ages 8-10 Mode: Interactive word games & cultural challenges!"
            "11-13" -> _guideMessage.value = "Ages 11-13 Mode: Elder proverbs, folklore history & word archives!"
        }
    }

    fun selectLocationNode(node: String) {
        _selectedLocationNode.value = node
        when (node) {
            "BOAT_VILLAGE" -> _guideMessage.value = "🛶 Welcome to Boat Village! Learn canoe craft & fisher tales."
            "FISHING_AREA" -> _guideMessage.value = "🐟 Fishing Area! Discover Lake Kyoga water life & species names."
            "PAPYRUS_GARDEN" -> _guideMessage.value = "🌿 Papyrus Garden! Learn sacred flora & lakeside nature words."
            "VILLAGE_HOME" -> _guideMessage.value = "🏠 Village Home! Family greetings & daily Bakenye life."
            "ELDER_BAOBAB" -> _guideMessage.value = "📖 Elder Baobab! Ancient folktales & wisdom under the tree."
            else -> _guideMessage.value = "Oli otya! Explore all locations across Lake Kyoga Shores!"
        }
    }

    fun selectTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun selectWorld(worldId: Int) {
        _selectedWorldId.value = worldId
    }

    fun startLesson(lesson: Lesson) {
        _activeLesson.value = lesson
        _activePhraseIndex.value = 0
    }

    fun closeLesson() {
        _activeLesson.value = null
    }

    fun nextPhrase(totalPhrases: Int) {
        if (_activePhraseIndex.value < totalPhrases - 1) {
            _activePhraseIndex.value += 1
        } else {
            // Lesson completed!
            val currentLesson = _activeLesson.value
            val profileId = uiState.value.profile.id
            if (currentLesson != null) {
                viewModelScope.launch {
                    if (profileId.isNotEmpty()) {
                        repository.completeLesson(childProfileId = profileId, lessonId = currentLesson.lessonId, starReward = 3, coinReward = 20)
                    } else {
                        repository.completeLesson(lessonId = currentLesson.lessonId, starReward = 3, coinReward = 20)
                    }
                    _earnedRewardStars.value = 3
                    _showRewardModal.value = true
                    _activeLesson.value = null
                }
            }
        }
    }

    fun dismissRewardModal() {
        _showRewardModal.value = false
    }

    fun playAudioPronunciation() {
        _isPlayingAudio.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            _isPlayingAudio.value = false
        }
    }

    fun loadPhrasesForWorld(worldId: Int) = repository.getPhrasesForWorld(worldId)
    fun loadLessonsForWorld(worldId: Int) = repository.getLessonsForWorld(worldId)

    fun syncNow() {
        viewModelScope.launch {
            syncManager.syncNow()
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

