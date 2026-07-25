package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Badge
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
    val badges: List<Badge> = emptyList(),
    val activeTab: NavigationTab = NavigationTab.LEARN,
    val activeLesson: Lesson? = null,
    val activePhraseIndex: Int = 0,
    val isPlayingAudio: Boolean = false,
    val showRewardModal: Boolean = false,
    val earnedRewardStars: Int = 0
)

enum class NavigationTab {
    LEARN, RANK, GIFT, AVATAR, SETTINGS
}

class BakenyeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BakenyeRepository

    private val _selectedWorldId = MutableStateFlow(1)
    private val _activeTab = MutableStateFlow(NavigationTab.LEARN)
    private val _activeLesson = MutableStateFlow<Lesson?>(null)
    private val _activePhraseIndex = MutableStateFlow(0)
    private val _isPlayingAudio = MutableStateFlow(false)
    private val _showRewardModal = MutableStateFlow(false)
    private val _earnedRewardStars = MutableStateFlow(0)

    val uiState: StateFlow<BakenyeUiState>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BakenyeRepository(database.bakenyeDao())

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

        uiState = combine(dbState, sessionState, rewardState) { db, session, reward ->
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
                earnedRewardStars = reward.third
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BakenyeUiState()
        )
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
            if (currentLesson != null) {
                viewModelScope.launch {
                    repository.completeLesson(currentLesson.lessonId, starReward = 3, coinReward = 20)
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
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

