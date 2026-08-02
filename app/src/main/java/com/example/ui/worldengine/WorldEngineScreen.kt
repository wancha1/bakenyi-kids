package com.example.ui.worldengine

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AuthenticAudioManager
import com.example.data.model.HeritageLocation
import com.example.data.model.LocationWorldState
import com.example.data.model.VocabularyItem
import com.example.ui.BakenyeViewModel
import com.example.ui.theme.GoldText
import com.example.ui.theme.LightGoldBg
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

@Composable
fun WorldEngineScreen(
    viewModel: BakenyeViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val audioManager = remember { AuthenticAudioManager.getInstance(context) }
    
    val uiState by viewModel.uiState.collectAsState()

    var worldState by remember { mutableStateOf<LocationWorldState>(LocationWorldState.Arrival) }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var selectedItem by remember {
        mutableStateOf(
            VocabularyItem(
                id = "V_ENSOMBA",
                lugandaTerm = "Ensomba",
                englishMeaning = "Fish (General)",
                phonetic = "En-sohm-bah",
                iconEmoji = "🐟",
                culturalFact = "Fish are the heart of life along Lake Kyoga Shores.",
                audioResName = "audio_ensomba"
            )
        )
    }

    val fishingLocation = remember {
        HeritageLocation(
            id = "FISHING_AREA",
            name = "Lake Kyoga Shores",
            subtitle = "Fishing Area Discovery",
            backgroundDesc = "Gentle lake waves, papyrus reeds & traditional wooden canoes.",
            discoveries = listOf(
                VocabularyItem("V_ENSOMBA", "Ensomba", "Fish (General)", "En-sohm-bah", "🐟", "Fish are the heart of life along Lake Kyoga Shores.", "audio_ensomba"),
                VocabularyItem("V_MUKENE", "Mukene", "Silver Cyprinid Fish", "Moo-keh-neh", "🐟", "Tiny silver fish harvested under moonlight with lanterns.", "audio_mukene"),
                VocabularyItem("V_NGEGE", "Ngege", "Tilapia Fish", "Ngeh-gheh", "🐠", "Prized freshwater fish cooked in rich sesame paste.", "audio_ngege"),
                VocabularyItem("V_ERYATO", "Eryato", "Wooden Canoe", "Eh-ryah-toh", "🛶", "Handcrafted canoe hollowed from sacred MVule trees.", "audio_eryato"),
                VocabularyItem("V_EKITIMBA", "Ekitimba", "Fishing Net", "Eh-kee-teem-bah", "🕸️", "Hand-knotted reed mesh passed down through fisherman elders.", "audio_ekitimba")
            )
        )
    }

    var guideSpeech by remember {
        mutableStateOf("Oli otya! I am Kato! Look at the water ripples on Lake Kyoga! Tap the fish hotspot!")
    }

    // Cleanup audio manager when screen disposes
    DisposableEffect(Unit) {
        onDispose {
            audioManager.release()
        }
    }

    Scaffold(
        topBar = {
            WorldEngineHeaderBar(
                locationName = fishingLocation.name,
                subtitle = fishingLocation.subtitle,
                stars = uiState.profile.stars,
                streakDays = uiState.profile.streakDays,
                onBackClick = onNavigateBack
            )
        },
        containerColor = WarmCreamBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("world_engine_screen")
        ) {
            // 1. Lake Kyoga Animated World Canvas
            WorldCanvas(
                modifier = Modifier.fillMaxSize(),
                isWaterAnimating = true
            )

            // 2. Hotspot Ripple Overlay (Ensomba Hotspot)
            HotspotOverlay(
                item = selectedItem,
                isDiscovered = worldState !is LocationWorldState.Arrival,
                onHotspotTap = { item ->
                    Log.d("WORLD_ENGINE_DEBUG", "Flow D: Tapped Ensomba hotspot. Transitioning Arrival -> Discovery -> Learning")
                    selectedItem = item
                    worldState = LocationWorldState.Discovery
                    guideSpeech = "Webale! You found ${item.lugandaTerm}! Let's learn to pronounce it together!"
                    
                    // Trigger Audio Pronunciation
                    isPlayingAudio = true
                    audioManager.playPronunciation(item.audioResName) {
                        isPlayingAudio = false
                        worldState = LocationWorldState.Learning
                    }

                    // Record discovery in DB
                    viewModel.selectLocationNode("FISHING_AREA")
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 40.dp, y = 30.dp)
            )

            // 3. Kato Otter Companion Anchor
            KatoOtterCompanion(
                worldState = worldState,
                speechText = guideSpeech,
                isPlayingAudio = isPlayingAudio,
                onKatoTap = {
                    Log.d("WORLD_ENGINE_DEBUG", "Flow C: Tapped Kato. Transitioning Arrival -> Discovery")
                    if (worldState is LocationWorldState.Arrival) {
                        worldState = LocationWorldState.Discovery
                        guideSpeech = "Look close at Lake Kyoga! Tap the shimmering fish ripple in the water!"
                        isPlayingAudio = true
                        audioManager.playKatoVoice("welcome") {
                            isPlayingAudio = false
                        }
                    } else {
                        isPlayingAudio = true
                        audioManager.playKatoVoice("cheer") {
                            isPlayingAudio = false
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 24.dp)
            )

            // 4. Learning Journey Modal Overlays
            when (val currentState = worldState) {
                is LocationWorldState.Learning -> {
                    VocabularyLearningModal(
                        item = selectedItem,
                        isPlayingAudio = isPlayingAudio,
                        onPlayAudio = {
                            isPlayingAudio = true
                            audioManager.playPronunciation(selectedItem.audioResName) {
                                isPlayingAudio = false
                            }
                        },
                        onNextToPractice = {
                            Log.d("WORLD_ENGINE_DEBUG", "Transitioning Learning -> Practice")
                            worldState = LocationWorldState.Practice
                            guideSpeech = "Great job! Now let's practice repeating ${selectedItem.lugandaTerm}!"
                        },
                        onClose = { worldState = LocationWorldState.Arrival }
                    )
                }
                is LocationWorldState.Practice -> {
                    PracticeInteractionModal(
                        item = selectedItem,
                        isPlayingAudio = isPlayingAudio,
                        onPlayAudio = {
                            isPlayingAudio = true
                            audioManager.playPronunciation(selectedItem.audioResName) {
                                isPlayingAudio = false
                            }
                        },
                        onStartChallenge = {
                            Log.d("WORLD_ENGINE_DEBUG", "Transitioning Practice -> Challenge")
                            worldState = LocationWorldState.Challenge
                            guideSpeech = "Time for the Fishing Area Challenge! Pick the right word for Fish!"
                        },
                        onClose = { worldState = LocationWorldState.Arrival }
                    )
                }
                is LocationWorldState.Challenge -> {
                    ChallengeGameModal(
                        targetItem = selectedItem,
                        options = listOf("Ensomba", "Inzu", "Taata", "Omwana"),
                        onSuccess = {
                            Log.d("WORLD_ENGINE_DEBUG", "Challenge passed! Transitioning Challenge -> Reward")
                            worldState = LocationWorldState.Reward
                            guideSpeech = "Kulika! You earned the Lake Kyoga Fisherman Badge!"
                        },
                        onClose = { worldState = LocationWorldState.Arrival }
                    )
                }
                is LocationWorldState.Reward -> {
                    RewardBadgeModal(
                        badgeTitle = "Lake Kyoga Fisherman",
                        badgeEmoji = "🐟",
                        description = "Discovered Ensomba and mastered authentic Bakenye lakeside vocabulary!",
                        starsEarned = 5,
                        onClaim = {
                            Log.d("WORLD_ENGINE_DEBUG", "Reward claimed! Returning Arrival state")
                            worldState = LocationWorldState.Arrival
                            guideSpeech = "You are a true Bakenye Heritage Explorer! Explore more water life tomorrow!"
                        }
                    )
                }
                else -> { /* Arrival / Discovery - canvas & Kato handle on screen */ }
            }
        }
    }
}

@Composable
fun WorldEngineHeaderBar(
    locationName: String,
    subtitle: String,
    stars: Int,
    streakDays: Int,
    onBackClick: () -> Unit
) {
    Surface(
        color = SurfaceWhite.copy(alpha = 0.95f),
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0))
            .testTag("world_engine_header")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WarmCreamBg)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = OliveGreenPrimary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = locationName.uppercase(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = OliveGreenPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryMuted
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(LightGoldBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$stars", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldText)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(StreakGreenBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$streakDays", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StreakGreenText)
                    }
                }
            }
        }
    }
}

@Composable
fun VocabularyLearningModal(
    item: VocabularyItem,
    isPlayingAudio: Boolean,
    onPlayAudio: () -> Unit,
    onNextToPractice: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("vocabulary_learning_modal"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "DISCOVERY ITEM", fontSize = 11.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(LightGoldBg)
                        .border(3.dp, StarGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.iconEmoji, fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = item.lugandaTerm, fontSize = 28.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)
                Text(text = item.englishMeaning, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextSecondaryMuted)

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(StreakGreenBg)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = "Phonetic: ${item.phonetic}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StreakGreenText)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Audio Play Button
                Button(
                    onClick = onPlayAudio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("listen_audio_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = StarGold),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Audio", tint = SurfaceWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isPlayingAudio) "PLAYING..." else "HEAR PRONUNCIATION", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.culturalFact,
                    fontSize = 12.sp,
                    color = TextPrimaryDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onNextToPractice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("practice_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = OliveGreenPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "PRACTICE REPEATING ➔", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                }
            }
        }
    }
}

@Composable
fun PracticeInteractionModal(
    item: VocabularyItem,
    isPlayingAudio: Boolean,
    onPlayAudio: () -> Unit,
    onStartChallenge: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("practice_modal"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "PRACTICE STEP", fontSize = 11.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Tap and repeat after Kato!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(StreakGreenBg)
                        .clickable { onPlayAudio() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.iconEmoji, fontSize = 52.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = item.lugandaTerm, fontSize = 26.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onStartChallenge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("challenge_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = OliveGreenPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "TAKE CHALLENGE 🧠", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                }
            }
        }
    }
}

@Composable
fun ChallengeGameModal(
    targetItem: VocabularyItem,
    options: List<String>,
    onSuccess: () -> Unit,
    onClose: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("challenge_game_modal"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "FISHING AREA CHALLENGE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Which Bakenye word means Fish?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(16.dp))

                options.forEach { option ->
                    val isSelected = selectedOption == option
                    Button(
                        onClick = {
                            selectedOption = option
                            if (option == targetItem.lugandaTerm) {
                                isError = false
                                onSuccess()
                            } else {
                                isError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected && isError) Color(0xFFFFEBEE) else WarmCreamBg
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected && isError) Color.Red else OliveGreenPrimary
                        )
                    }
                }

                if (isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Try again! Kato believes in you! 🦦", fontSize = 12.sp, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun RewardBadgeModal(
    badgeTitle: String,
    badgeEmoji: String,
    description: String,
    starsEarned: Int,
    onClaim: () -> Unit
) {
    Dialog(onDismissRequest = onClaim) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("reward_badge_modal"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "HERITAGE BADGE UNLOCKED! 🎉", fontSize = 13.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(StarGold)
                        .border(4.dp, SurfaceWhite, CircleShape)
                        .shadow(8.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = badgeEmoji, fontSize = 52.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = badgeTitle, fontSize = 22.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = description, fontSize = 13.sp, color = TextSecondaryMuted, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "+$starsEarned ⭐", fontSize = 18.sp, fontWeight = FontWeight.Black, color = GoldText)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onClaim,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("claim_reward_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = OliveGreenPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "CLAIM & CONTINUE ➔", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                }
            }
        }
    }
}
