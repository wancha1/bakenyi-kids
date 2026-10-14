package com.example.ui.worldengine

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.launch
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
    // Intercept hardware/system back button to safely return to previous destination
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    val audioManager = remember { AuthenticAudioManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()
    
    val uiState by viewModel.uiState.collectAsState()
    val fishingDiscoveries by viewModel.fishingDiscoveries.collectAsState()

    val discoveredKeys = remember(fishingDiscoveries) {
        fishingDiscoveries.map { it.itemKey }.toSet()
    }
    val completedKeys = remember(fishingDiscoveries) {
        fishingDiscoveries.filter { it.isCompleted }.map { it.itemKey }.toSet()
    }

    var worldState by remember { mutableStateOf<LocationWorldState>(LocationWorldState.Arrival) }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var dynamicQuizOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isNewRewardEarned by remember { mutableStateOf(false) }

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
                VocabularyItem("V_ERYATO", "Eryato", "Traditional Wooden Canoe", "Eh-ryah-toh", "🛶", "Handcrafted canoe hollowed from sacred MVule trees.", "audio_eryato"),
                VocabularyItem("V_EKITIMBA", "Ekitimba", "Woven Fishing Net", "Eh-kee-teem-bah", "🕸️", "Hand-knotted reed mesh passed down through fisherman elders.", "audio_ekitimba")
            )
        )
    }

    var selectedItem by remember {
        mutableStateOf(fishingLocation.discoveries[0])
    }

    val hotspotPositions = remember {
        mapOf(
            "V_ENSOMBA" to (Alignment.Center to Offset(10f, 15f)),
            "V_MUKENE" to (Alignment.CenterStart to Offset(35f, -35f)),
            "V_NGEGE" to (Alignment.CenterEnd to Offset(-35f, 45f)),
            "V_ERYATO" to (Alignment.TopStart to Offset(40f, 55f)),
            "V_EKITIMBA" to (Alignment.TopEnd to Offset(-40f, 65f))
        )
    }

    var guideSpeech by remember {
        mutableStateOf("Oli otya! I am Kato the Otter! Explore the water ripples and reeds across Lake Kyoga to find all 5 treasures!")
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

            // 2. Exploration Progress Tracker HUD Card
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .testTag("adventure_progress_tracker"),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceWhite.copy(alpha = 0.94f),
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎣 Lake Kyoga:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OliveGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Found ${discoveredKeys.size}/5",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                    Text(text = " • ", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "Mastered ${completedKeys.size}/5 ⭐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }

            // 3. Five Interactive Lake Kyoga Hotspots
            fishingLocation.discoveries.forEach { item ->
                val isDiscovered = discoveredKeys.contains(item.id)
                val isCompleted = completedKeys.contains(item.id)
                val pos = hotspotPositions[item.id] ?: (Alignment.Center to Offset.Zero)

                HotspotOverlay(
                    item = item,
                    isDiscovered = isDiscovered,
                    isCompleted = isCompleted,
                    onHotspotTap = { tappedItem ->
                        Log.d("WORLD_ENGINE_DEBUG", "Tapped hotspot: ${tappedItem.lukenyeTerm}")
                        selectedItem = tappedItem
                        val alreadyDiscovered = discoveredKeys.contains(tappedItem.id)
                        if (!alreadyDiscovered) {
                            worldState = LocationWorldState.Discovery
                            guideSpeech = "Webale! You found ${tappedItem.lukenyeTerm}! Let's learn to pronounce it together!"
                            viewModel.recordDiscovery(tappedItem.id)
                            isPlayingAudio = true
                            audioManager.playPronunciation(tappedItem.audioResName) {
                                isPlayingAudio = false
                                worldState = LocationWorldState.Learning
                            }
                        } else {
                            worldState = LocationWorldState.Learning
                            guideSpeech = "Reviewing ${tappedItem.lukenyeTerm}! Listen closely to the authentic pronunciation."
                            isPlayingAudio = true
                            audioManager.playPronunciation(tappedItem.audioResName) {
                                isPlayingAudio = false
                            }
                        }
                    },
                    modifier = Modifier
                        .align(pos.first)
                        .offset(x = pos.second.x.dp, y = pos.second.y.dp)
                )
            }

            // 4. Kato Otter Companion Anchor
            KatoOtterCompanion(
                worldState = worldState,
                speechText = guideSpeech,
                isPlayingAudio = isPlayingAudio,
                onKatoTap = {
                    Log.d("WORLD_ENGINE_DEBUG", "Tapped Kato companion")
                    val remaining = fishingLocation.discoveries.filterNot { discoveredKeys.contains(it.id) }
                    val toMaster = fishingLocation.discoveries.filterNot { completedKeys.contains(it.id) }
                    if (remaining.isNotEmpty()) {
                        val nextItem = remaining.first()
                        guideSpeech = "Kato's Clue: Look around Lake Kyoga for water ripples and reeds to find ${nextItem.englishMeaning}!"
                        isPlayingAudio = true
                        audioManager.playKatoVoice("welcome") {
                            isPlayingAudio = false
                        }
                    } else if (toMaster.isNotEmpty()) {
                        val nextItem = toMaster.first()
                        guideSpeech = "Kato's Clue: Tap ${nextItem.lukenyeTerm} to complete the quiz and earn its golden star! ⭐"
                        isPlayingAudio = true
                        audioManager.playKatoVoice("cheer") {
                            isPlayingAudio = false
                        }
                    } else {
                        guideSpeech = "Incredible! You have discovered and mastered all 5 Lake Kyoga treasures! 🏆"
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

            // 5. Learning Journey Modal Overlays
            when (worldState) {
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
                            guideSpeech = "Great job! Now let's practice repeating ${selectedItem.lukenyeTerm}!"
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
                            coroutineScope.launch {
                                Log.d("WORLD_ENGINE_DEBUG", "Generating dynamic quiz options for ${selectedItem.lukenyeTerm}")
                                dynamicQuizOptions = viewModel.generateQuizOptions(selectedItem.lukenyeTerm)
                                worldState = LocationWorldState.Challenge
                                guideSpeech = "Time for the Fishing Area Challenge! Which Lukenye word means ${selectedItem.englishMeaning}?"
                            }
                        },
                        onClose = { worldState = LocationWorldState.Arrival }
                    )
                }
                is LocationWorldState.Challenge -> {
                    ChallengeGameModal(
                        targetItem = selectedItem,
                        options = if (dynamicQuizOptions.isNotEmpty()) dynamicQuizOptions else listOf(selectedItem.lukenyeTerm),
                        onSuccess = {
                            coroutineScope.launch {
                                Log.d("WORLD_ENGINE_DEBUG", "Challenge passed! Evaluating reward for ${selectedItem.lukenyeTerm}")
                                isNewRewardEarned = viewModel.completeDiscoveryChallenge(selectedItem.id)
                                worldState = LocationWorldState.Reward
                                guideSpeech = if (isNewRewardEarned) {
                                    "Kulika! You mastered ${selectedItem.lukenyeTerm} and earned 5 stars! ⭐"
                                } else {
                                    "Well done! You reviewed ${selectedItem.lukenyeTerm}!"
                                }
                            }
                        },
                        onClose = { worldState = LocationWorldState.Arrival }
                    )
                }
                is LocationWorldState.Reward -> {
                    RewardBadgeModal(
                        badgeTitle = if (isNewRewardEarned) "Lake Kyoga Master: ${selectedItem.lukenyeTerm}" else "Discovery Revisited: ${selectedItem.lukenyeTerm}",
                        badgeEmoji = selectedItem.iconEmoji,
                        description = selectedItem.culturalFact,
                        starsEarned = if (isNewRewardEarned) 5 else 0,
                        isNewReward = isNewRewardEarned,
                        onClaim = {
                            Log.d("WORLD_ENGINE_DEBUG", "Reward claimed. Returning to Arrival state.")
                            worldState = LocationWorldState.Arrival
                            guideSpeech = "Webale! Discovered ${discoveredKeys.size}/5 • Mastered ${completedKeys.size}/5! Explore more water treasures!"
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

                Text(text = item.lukenyeTerm, fontSize = 28.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)
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
                Text(text = item.lukenyeTerm, fontSize = 26.sp, fontWeight = FontWeight.Black, color = OliveGreenPrimary)

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
                Text(
                    text = "LAKE KYOGA CULTURAL CHALLENGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = OliveGreenPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Which Lukenye word means:\n\"${targetItem.englishMeaning}\"?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("quiz_question_text")
                )

                Spacer(modifier = Modifier.height(16.dp))

                options.forEach { option ->
                    val isSelected = selectedOption == option
                    val optionTag = "quiz_option_${option.lowercase().replace(" ", "_")}"
                    Button(
                        onClick = {
                            selectedOption = option
                            if (option.equals(targetItem.lukenyeTerm, ignoreCase = true)) {
                                isError = false
                                onSuccess()
                            } else {
                                isError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(48.dp)
                            .testTag(optionTag),
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
                    Text(
                        text = "Try again! '$selectedOption' is not '${targetItem.englishMeaning}'. Kato believes in you! 🦦",
                        fontSize = 12.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
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
    isNewReward: Boolean = true,
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
                Text(
                    text = if (isNewReward) "HERITAGE DISCOVERY MASTERED! 🎉" else "DISCOVERY REVISITED! 📚",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = OliveGreenPrimary
                )
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
                Text(
                    text = badgeTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = OliveGreenPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextSecondaryMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isNewReward) {
                        Text(text = "+$starsEarned ⭐", fontSize = 18.sp, fontWeight = FontWeight.Black, color = GoldText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "+20 🪙", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                    } else {
                        Text(text = "+0 ⭐ (Already Mastered)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSecondaryMuted)
                    }
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
