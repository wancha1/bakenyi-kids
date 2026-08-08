package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.Badge
import com.example.data.model.BadgeWithProgress
import com.example.data.model.Lesson
import com.example.data.model.LessonWithProgress
import com.example.data.model.Phrase
import com.example.data.model.World
import com.example.ui.BakenyeUiState
import com.example.ui.BakenyeViewModel
import com.example.ui.NavigationTab
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldText
import com.example.ui.theme.LightGoldBg
import com.example.ui.theme.LockedGray
import com.example.ui.theme.OliveGreenDark
import com.example.ui.theme.OliveGreenLight
import com.example.ui.theme.OliveGreenPrimary
import com.example.ui.theme.StarGold
import com.example.ui.theme.StreakGreenBg
import com.example.ui.theme.StreakGreenText
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.WarmCreamBg
import com.example.ui.worldengine.WorldEngineScreen

@Composable
fun MainAppScreen(
    viewModel: BakenyeViewModel,
    initialShowWorldEngine: Boolean = true
) {
    val state by viewModel.uiState.collectAsState()
    var showWorldEngine by remember { mutableStateOf(initialShowWorldEngine) }

    if (state.showOpeningIntro) {
        OpeningIntroExperienceScreen(viewModel = viewModel, state = state)
    } else if (showWorldEngine) {
        WorldEngineScreen(
            viewModel = viewModel,
            onNavigateBack = { showWorldEngine = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopHeaderBar(
                    state = state,
                    onReplayIntro = viewModel::replayOpeningIntro,
                    onOpenWorldEngine = { showWorldEngine = true }
                )
            },
            bottomBar = { BottomNavBar(activeTab = state.activeTab, onTabSelected = viewModel::selectTab) },
            containerColor = WarmCreamBg
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (state.activeTab) {
                    NavigationTab.LEARN -> LearnWorldMapScreen(viewModel = viewModel, state = state)
                    NavigationTab.RANK -> RankLeaderboardScreen(badges = state.badges)
                    NavigationTab.GIFT -> GiftCenterScreen(coins = state.profile.coins)
                    NavigationTab.AVATAR -> AvatarGuideScreen(profile = state.profile, onReplayIntro = viewModel::replayOpeningIntro)
                    NavigationTab.SETTINGS -> SettingsParentScreen(onReplayIntro = viewModel::replayOpeningIntro)
                }

                // Interactive Lesson Overlay
                state.activeLesson?.let { lesson ->
                    val phrases by viewModel.loadPhrasesForWorld(lesson.worldId).collectAsState(initial = emptyList())
                    InteractiveLessonDialog(
                        lesson = lesson,
                        phrases = phrases,
                        phraseIndex = state.activePhraseIndex,
                        isPlayingAudio = state.isPlayingAudio,
                        onPlayAudio = viewModel::playAudioPronunciation,
                        onNext = { viewModel.nextPhrase(phrases.size) },
                        onClose = viewModel::closeLesson
                    )
                }

                // Reward Celebration Popup
                if (state.showRewardModal) {
                    RewardCelebrationModal(
                        stars = state.earnedRewardStars,
                        onDismiss = viewModel::dismissRewardModal
                    )
                }
            }
        }
    }
}

@Composable
fun TopHeaderBar(
    state: BakenyeUiState,
    onReplayIntro: () -> Unit = {},
    onOpenWorldEngine: () -> Unit = {}
) {
    Surface(
        color = SurfaceWhite.copy(alpha = 0.95f),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle)
            .testTag("top_header_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Guide Kato Level Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenWorldEngine() }
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(StarGold)
                        .border(2.dp, SurfaceWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.profile.guideAvatar, fontSize = 22.sp)
                }
                Column {
                    Text(
                        text = "LEVEL ${state.profile.level}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = state.profile.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }
            }

            // Stats & Intro Replay Badge
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Replay Story Intro Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(StreakGreenBg)
                        .border(1.dp, OliveGreenLight, RoundedCornerShape(20.dp))
                        .clickable { onReplayIntro() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "STORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OliveGreenPrimary
                        )
                    }
                }

                // Stars Counter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LightGoldBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.profile.stars}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldText
                        )
                    }
                }

                // Streak Counter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(StreakGreenBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔥", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.profile.streakDays}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StreakGreenText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LearnWorldMapScreen(viewModel: BakenyeViewModel, state: BakenyeUiState) {
    val lessons by viewModel.loadLessonsForWorld(state.selectedWorldId).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // World Header Title & Active Quest Badge
        val activeWorld = state.worlds.find { it.worldId == state.selectedWorldId }
            ?: World(1, "World 1", "Alphabet & Sounds", "🦁")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = activeWorld.title.uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = OliveGreenPrimary
                )
                Text(
                    text = activeWorld.subtitle.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryMuted,
                    letterSpacing = 1.sp
                )
            }

            // Quest Mode Pill Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightGoldBg)
                    .border(1.dp, GoldAccent, RoundedCornerShape(16.dp))
                    .clickable { viewModel.replayOpeningIntro() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🛶", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (state.selectedQuestMode == "STORY_FINDER") "STORY TALE" else if (state.selectedQuestMode == "HERO_QUEST") "HERO QUEST" else "EXPLORER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val infiniteTransition = rememberInfiniteTransition()
        val katoScale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Kato Companion Live Speech Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .clickable { viewModel.playAudioPronunciation() },
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .scale(if (state.isPlayingAudio) katoScale else 1.0f)
                        .clip(CircleShape)
                        .background(StarGold)
                        .border(2.dp, SurfaceWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🦦", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "KATO SAYS:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldText,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = state.guideMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark,
                        lineHeight = 16.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (state.isPlayingAudio) LightGoldBg else StreakGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Hear Kato",
                        tint = OliveGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Adaptive Age Depth Selector Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AGE DEPTH:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = TextSecondaryMuted,
                letterSpacing = 1.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("5-7", "8-10", "11-13").forEach { age ->
                    val isSelected = state.targetAgeGroup == age
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GoldAccent else SurfaceWhite)
                            .border(1.dp, if (isSelected) GoldText else BorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectAgeGroup(age) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = age,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) SurfaceWhite else TextPrimaryDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // World & Lake Kyoga Location Explorer Horizontal Scroll
        LazyRow(
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val locations = listOf(
                Triple("ALL", "🌍 All Shores", "Explore entire world map"),
                Triple("BOAT_VILLAGE", "🛶 Boat Village", "Canoe craft & fisher tales"),
                Triple("FISHING_AREA", "🐟 Fishing Area", "Water life & fish species"),
                Triple("PAPYRUS_GARDEN", "🌿 Papyrus Garden", "Lakeside flora & nature"),
                Triple("VILLAGE_HOME", "🏠 Village Home", "Family greetings & daily life"),
                Triple("ELDER_BAOBAB", "📖 Elder Baobab", "Proverbs & folklore stories")
            )

            items(locations) { (locKey, locTitle, _) ->
                val isSelected = state.selectedLocationNode == locKey
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) OliveGreenPrimary else SurfaceWhite)
                        .border(1.dp, if (isSelected) OliveGreenDark else BorderSubtle, RoundedCornerShape(14.dp))
                        .clickable { viewModel.selectLocationNode(locKey) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = locTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) SurfaceWhite else TextPrimaryDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vertical Node Trail / World Map Path
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(lessons) { lesson ->
                    LessonNodeItem(
                        lesson = lesson,
                        onNodeClick = { viewModel.startLesson(lesson.lesson) }
                    )
                }
            }
        }

        // Play Main Action Button with Tactile 3D Shadow
        Button(
            onClick = {
                val nextLesson = lessons.firstOrNull { !it.isCompleted && !it.isLocked } ?: lessons.firstOrNull()
                if (nextLesson != null) {
                    viewModel.startLesson(nextLesson.lesson)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp))
                .testTag("start_adventure_btn"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OliveGreenPrimary,
                contentColor = SurfaceWhite
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "START ADVENTURE",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "➔", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun LessonNodeItem(lesson: LessonWithProgress, onNodeClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = !lesson.isLocked) { onNodeClick() }
            .testTag("lesson_node_${lesson.lessonId}")
    ) {
        Box(contentAlignment = Alignment.Center) {
            when {
                lesson.isCompleted -> {
                    // Completed Node
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(SurfaceWhite)
                            .border(4.dp, OliveGreenPrimary, RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = lesson.iconEmoji, fontSize = 32.sp)
                    }
                    Box(
                        modifier = Modifier
                            .offset(y = 36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(OliveGreenPrimary)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "COMPLETED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = SurfaceWhite
                        )
                    }
                }
                !lesson.isLocked -> {
                    // Active Playable Node
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(GoldAccent)
                            .border(4.dp, SurfaceWhite, CircleShape)
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = lesson.iconEmoji, fontSize = 32.sp)
                            Text(
                                text = "PLAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = SurfaceWhite
                            )
                        }
                    }
                }
                else -> {
                    // Locked Node
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(LockedGray)
                            .border(3.dp, SurfaceWhite, RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = TextSecondaryMuted,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lesson Title Card
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = lesson.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (lesson.isLocked) TextSecondaryMuted else TextPrimaryDark
            )
        }
    }
}

@Composable
fun InteractiveLessonDialog(
    lesson: Lesson,
    phrases: List<Phrase>,
    phraseIndex: Int,
    isPlayingAudio: Boolean,
    onPlayAudio: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp)),
            color = WarmCreamBg,
            tonalElevation = 8.dp
        ) {
            val currentPhrase = phrases.getOrNull(phraseIndex) ?: Phrase(
                "P1", lesson.worldId, "Omwana", "Child", "Oh-mwah-nah", "👶", "General", "Pronounce softly"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header & Close Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lesson.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OliveGreenPrimary
                    )
                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryMuted,
                        modifier = Modifier
                            .clickable { onClose() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Indicator
                val progress = if (phrases.isNotEmpty()) (phraseIndex + 1).toFloat() / phrases.size else 0.5f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = OliveGreenPrimary,
                    trackColor = BorderSubtle
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Guide Kato Character Tip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    Text(text = "🦒", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Listen & repeat with Kato! Tap the speaker to hear Bakenye pronunciation.",
                        fontSize = 12.sp,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Main Flashcard View
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = currentPhrase.iconEmoji, fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Bakenye Word
                        Text(
                            text = currentPhrase.bakenyeText,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = OliveGreenPrimary
                        )

                        // English Translation
                        Text(
                            text = currentPhrase.englishText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondaryMuted
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Phonetic Pronunciation Guide
                        Text(
                            text = "🗣️ ${currentPhrase.pronunciation}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldText
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Speaker Audio Play Button
                        Button(
                            onClick = onPlayAudio,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingAudio) GoldAccent else LightGoldBg,
                                contentColor = GoldText
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("audio_play_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play Pronunciation",
                                tint = if (isPlayingAudio) SurfaceWhite else GoldText,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Continue / Next Phrase Button
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OliveGreenPrimary,
                        contentColor = SurfaceWhite
                    )
                ) {
                    Text(
                        text = if (phraseIndex < phrases.size - 1) "CONTINUE ➔" else "FINISH LESSON 🎉",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RewardCelebrationModal(stars: Int, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp)),
            color = SurfaceWhite,
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎉", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LESSON COMPLETE!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = OliveGreenPrimary
                )
                Text(
                    text = "You earned +$stars Stars & 20 Coins!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LightGoldBg)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "⭐ ⭐ ⭐",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SurfaceWhite)
                ) {
                    Text(
                        text = "COLLECT REWARDS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun RankLeaderboardScreen(badges: List<BadgeWithProgress>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CULTURAL BADGES & RANK",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = OliveGreenPrimary
        )
        Text(
            text = "Earn badges by exploring Bakenye language & culture",
            fontSize = 12.sp,
            color = TextSecondaryMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(badges) { badge ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (badge.isUnlocked) LightGoldBg else LockedGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.iconEmoji, fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = badge.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = badge.description,
                            fontSize = 12.sp,
                            color = TextSecondaryMuted
                        )
                    }

                    if (badge.isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Unlocked",
                            tint = OliveGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GiftCenterScreen(coins: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "REWARDS & COIN SHOP",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = OliveGreenPrimary
        )
        Text(
            text = "Use earned coins to unlock guide outfits & stories",
            fontSize = 12.sp,
            color = TextSecondaryMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(LightGoldBg)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "💰 Your Coins: $coins",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = GoldText
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        val rewards = listOf(
            Triple("Traditional Barkcloth Kato", "👔 Outfit", "100 Coins"),
            Triple("Bakenye Canoe Storybook", "📖 Story", "200 Coins"),
            Triple("Drum Beats Audio Pack", "🎵 Songs", "150 Coins")
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(rewards) { (title, category, cost) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Text(text = category, fontSize = 12.sp, color = TextSecondaryMuted)
                    }
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = cost, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarGuideScreen(profile: com.example.data.model.UserProfile, onReplayIntro: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GUIDE KATO",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = OliveGreenPrimary
        )
        Text(
            text = "Your Bakenye Cultural Ambassador",
            fontSize = 12.sp,
            color = TextSecondaryMuted
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(StarGold)
                .border(4.dp, SurfaceWhite, CircleShape)
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = profile.guideAvatar, fontSize = 64.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onReplayIntro,
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "✨ Replay Kato Intro Story", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Did You Know?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OliveGreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The Bakenye people originally lived along the waters and islands of Lake Kyoga in Uganda. They are renowned for traditional canoe building, fishing, and rich oral folk stories passed down through generations!",
                    fontSize = 13.sp,
                    color = TextPrimaryDark,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun SettingsParentScreen(onReplayIntro: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "PARENTS & SCHOOLS PORTAL",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = OliveGreenPrimary
        )
        Text(
            text = "COPPA-friendly offline language platform",
            fontSize = 12.sp,
            color = TextSecondaryMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "📶 Offline Mode Active", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = StreakGreenText)
                Text(
                    text = "All 11 Bakenye worlds, audio clips, and stories operate 100% offline without requiring internet.",
                    fontSize = 12.sp,
                    color = TextSecondaryMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "✨ Replay Opening Intro", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Text(text = "Re-experience the 30-second opening adventure splash", fontSize = 12.sp, color = TextSecondaryMuted)
                    }
                    Button(onClick = onReplayIntro, shape = RoundedCornerShape(12.dp)) {
                        Text(text = "Replay", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "🛡️ Privacy & Child Safety", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Text(
                    text = "No personal data or photos collected. Fully safe for kids aged 4-12.",
                    fontSize = 12.sp,
                    color = TextSecondaryMuted
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(activeTab: NavigationTab, onTabSelected: (NavigationTab) -> Unit) {
    Surface(
        color = SurfaceWhite,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle)
            .testTag("bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Learn
            NavTabButton(
                icon = "🌍",
                label = "LEARN",
                isSelected = activeTab == NavigationTab.LEARN,
                onClick = { onTabSelected(NavigationTab.LEARN) }
            )

            // Tab 2: Rank
            NavTabButton(
                icon = "🏆",
                label = "RANK",
                isSelected = activeTab == NavigationTab.RANK,
                onClick = { onTabSelected(NavigationTab.RANK) }
            )

            // Tab 3: Gift (Center Floating Button)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GoldAccent)
                    .clickable { onTabSelected(NavigationTab.GIFT) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎁", fontSize = 26.sp)
            }

            // Tab 4: Avatar
            NavTabButton(
                icon = "🦒",
                label = "AVATAR",
                isSelected = activeTab == NavigationTab.AVATAR,
                onClick = { onTabSelected(NavigationTab.AVATAR) }
            )

            // Tab 5: Settings / More
            NavTabButton(
                icon = "⚙️",
                label = "MORE",
                isSelected = activeTab == NavigationTab.SETTINGS,
                onClick = { onTabSelected(NavigationTab.SETTINGS) }
            )
        }
    }
}

@Composable
fun NavTabButton(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) OliveGreenPrimary else TextSecondaryMuted
        )
    }
}

@Composable
fun OpeningIntroExperienceScreen(
    viewModel: BakenyeViewModel,
    state: BakenyeUiState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDF5),
                        WarmCreamBg,
                        Color(0xFFF4EAD3)
                    )
                )
            )
    ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
            // Top Bar with Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Indicator Pills (5 steps for 60s journey)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(5) { index ->
                        val active = index == state.currentIntroStep
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (active) 24.dp else 10.dp)
                                .clip(CircleShape)
                                .background(if (active) OliveGreenPrimary else BorderSubtle)
                        )
                    }
                }

                Text(
                    text = "SKIP ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryMuted,
                    modifier = Modifier
                        .clickable { viewModel.skipIntroToWorld() }
                        .padding(8.dp)
                        .testTag("skip_intro_btn")
                )
            }

            // Main Dynamic 60-Second Journey Step Content
            when (state.currentIntroStep) {
                0 -> IntroStepSplash(onNext = viewModel::nextIntroStep)
                1 -> IntroStepMeetKato(
                    guideMessage = state.guideMessage,
                    isPlayingAudio = state.isPlayingAudio,
                    onPlayAudio = viewModel::playAudioPronunciation,
                    onNext = viewModel::nextIntroStep
                )
                2 -> IntroStepFirstDiscovery(
                    isPlayingAudio = state.isPlayingAudio,
                    onPlayAudio = viewModel::playAudioPronunciation,
                    onNext = viewModel::nextIntroStep
                )
                3 -> IntroStepFirstChallenge(
                    onNext = viewModel::nextIntroStep
                )
                else -> IntroStepRewardAndQuest(
                    selectedQuest = state.selectedQuestMode,
                    onSelectQuest = viewModel::selectQuestMode,
                    onStartWorld = viewModel::nextIntroStep
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun IntroStepSplash(onNext: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Hero Image Artwork Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(28.dp))
                .shadow(8.dp, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_bakenye_world_intro_1785052707928),
                contentDescription = "Bakenye World Intro Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                        )
                    )
            )
            Text(
                text = "✨ LAKE KYOGA DISCOVERY ✨",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = SurfaceWhite,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "WELCOME TO\nBAKENYE KIDS",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = OliveGreenPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "You are entering a living world of stories, language, and discovery!",
            fontSize = 15.sp,
            color = TextPrimaryDark,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .testTag("begin_journey_btn"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OliveGreenPrimary, contentColor = SurfaceWhite)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "BEGIN YOUR JOURNEY",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "➔", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun IntroStepMeetKato(
    guideMessage: String,
    isPlayingAudio: Boolean,
    onPlayAudio: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "MEET YOUR GUIDE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = GoldText,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "KATO THE OTTER 🦦",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = OliveGreenPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar Companion Box
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(StarGold)
                .border(4.dp, SurfaceWhite, CircleShape)
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🦦", fontSize = 60.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Interactive Speech Bubble
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = guideMessage,
                    fontSize = 15.sp,
                    color = TextPrimaryDark,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onPlayAudio,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isPlayingAudio) LightGoldBg else SurfaceWhite,
                        contentColor = GoldText
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Voice greeting",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlayingAudio) "Kato is speaking..." else "🔊 Hear Kato's Voice",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .testTag("meet_kato_continue_btn"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = SurfaceWhite)
        ) {
            Text(
                text = "MEET KATO & CHOOSE QUEST ➔",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun IntroStepFirstDiscovery(
    isPlayingAudio: Boolean,
    onPlayAudio: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "FIRST CULTURAL DISCOVERY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = GoldText,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "TAP THE REEDS! 🐟",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = OliveGreenPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Object Card (The Ensomba / Fish in Papyrus Reeds)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(28.dp))
                .border(3.dp, GoldAccent, RoundedCornerShape(28.dp))
                .clickable { onPlayAudio() }
                .testTag("discovery_fish_card"),
            colors = CardDefaults.cardColors(containerColor = LightGoldBg),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🌿 🐟 🌿", fontSize = 54.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ENSOMBA",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = OliveGreenPrimary,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "/en-som-ba/ • Meaning: Fish",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onPlayAudio,
                    colors = ButtonDefaults.buttonColors(containerColor = OliveGreenPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Listen native audio",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlayingAudio) "Speaking: ENSOMBA..." else "🔊 Hear Native Pronunciation",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .testTag("discovery_continue_btn"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OliveGreenPrimary, contentColor = SurfaceWhite)
        ) {
            Text(
                text = "TRY QUICK CHALLENGE ➔",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun IntroStepFirstChallenge(
    onNext: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "30-SECOND CHALLENGE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = GoldText,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "HELP KATO FIND THE ENSOMBA! 🦦",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = OliveGreenPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Visual Choice Cards
        val options = listOf(
            Triple(0, "🐟", "Ensomba"),
            Triple(1, "🚣", "Obwato"),
            Triple(2, "🏠", "Ennyumba")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { (id, emoji, word) ->
                val isThisSelected = selectedOption == id
                val isThisCorrect = id == 0 && isThisSelected

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            3.dp,
                            if (isThisCorrect) OliveGreenPrimary else if (isThisSelected) Color.Red.copy(alpha = 0.5f) else BorderSubtle,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            selectedOption = id
                            isCorrect = (id == 0)
                        }
                        .testTag("challenge_option_$id"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isThisCorrect) StreakGreenBg else SurfaceWhite
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = emoji, fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = word,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dynamic Feedback Banner
        if (isCorrect) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StreakGreenBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎉", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "PERFECT DISCOVERY!", fontSize = 14.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)
                        Text(text = "You found the Ensomba! Kato is super happy!", fontSize = 12.sp, color = TextPrimaryDark)
                    }
                }
            }
        } else if (selectedOption != null) {
            Text(
                text = "Almost! Tap the Fish (🐟 Ensomba) to help Kato!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            enabled = isCorrect,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .testTag("challenge_claim_reward_btn"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCorrect) GoldAccent else LockedGray,
                contentColor = SurfaceWhite
            )
        ) {
            Text(
                text = if (isCorrect) "CLAIM CULTURAL BADGE 🏆" else "SELECT THE ENSOMBA 🐟",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun IntroStepRewardAndQuest(
    selectedQuest: String,
    onSelectQuest: (String) -> Unit,
    onStartWorld: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Badge Celebration Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = LightGoldBg),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(StarGold)
                        .border(3.dp, SurfaceWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", fontSize = 34.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "HERITAGE EXPLORER BADGE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldText
                    )
                    Text(
                        text = "Lake Kyoga Pioneer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = OliveGreenPrimary
                    )
                    Text(
                        text = "+5 Cultural Stars ⭐ Earned",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CHOOSE YOUR MAP QUEST",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(10.dp))

        val quests = listOf(
            Triple("ISLAND_EXPLORER", "🛶 Island Explorer", "Discover Bakenye words node by node across floating islands"),
            Triple("STORY_FINDER", "📖 Story Tale Finder", "Listen to traditional elder folklore stories under the Baobab"),
            Triple("HERO_QUEST", "🏆 Heritage Hero Quest", "Collect badges, stars, and climb the leaderboard")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            quests.forEach { (modeKey, title, desc) ->
                val isSelected = selectedQuest == modeKey
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            2.dp,
                            if (isSelected) OliveGreenPrimary else BorderSubtle,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectQuest(modeKey) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) LightGoldBg.copy(alpha = 0.5f) else SurfaceWhite
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (modeKey == "ISLAND_EXPLORER") "🛶" else if (modeKey == "STORY_FINDER") "📖" else "🏆",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text(text = desc, fontSize = 10.sp, color = TextSecondaryMuted)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onStartWorld,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .testTag("enter_bakenye_world_btn"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OliveGreenPrimary, contentColor = SurfaceWhite)
        ) {
            Text(
                text = "ENTER BAKENYE WORLD MAP ✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
