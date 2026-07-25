package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Badge
import com.example.data.model.Lesson
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
import com.example.ui.theme.OliveGreenPrimary
import com.example.ui.theme.StarGold
import com.example.ui.theme.StreakGreenBg
import com.example.ui.theme.StreakGreenText
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.WarmCreamBg

@Composable
fun MainAppScreen(viewModel: BakenyeViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopHeaderBar(state = state) },
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
                NavigationTab.AVATAR -> AvatarGuideScreen(profile = state.profile)
                NavigationTab.SETTINGS -> SettingsParentScreen()
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

@Composable
fun TopHeaderBar(state: BakenyeUiState) {
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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

            // Stats Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // World Header Title
        val activeWorld = state.worlds.find { it.worldId == state.selectedWorldId }
            ?: World(1, "World 1", "Alphabet & Sounds", "🦁")

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = activeWorld.title.uppercase(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = OliveGreenPrimary
            )
            Text(
                text = activeWorld.subtitle.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryMuted,
                letterSpacing = 1.5.sp
            )
        }

        // World Selector Bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.worlds) { world ->
                val isSelected = world.worldId == state.selectedWorldId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) OliveGreenPrimary else SurfaceWhite)
                        .border(1.dp, if (isSelected) OliveGreenDark else BorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectWorld(world.worldId) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = world.iconEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "W${world.worldId}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) SurfaceWhite else TextPrimaryDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(lessons) { lesson ->
                    LessonNodeItem(
                        lesson = lesson,
                        onNodeClick = { viewModel.startLesson(lesson) }
                    )
                }
            }
        }

        // Play Main Action Button with 3D Effect
        Button(
            onClick = {
                val nextLesson = lessons.firstOrNull { !it.isCompleted && !it.isLocked } ?: lessons.firstOrNull()
                if (nextLesson != null) {
                    viewModel.startLesson(nextLesson)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
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
                    fontSize = 18.sp,
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
fun LessonNodeItem(lesson: Lesson, onNodeClick: () -> Unit) {
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
                    .padding(20.dp),
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
fun RankLeaderboardScreen(badges: List<Badge>) {
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
fun AvatarGuideScreen(profile: com.example.data.model.UserProfile) {
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
fun SettingsParentScreen() {
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
