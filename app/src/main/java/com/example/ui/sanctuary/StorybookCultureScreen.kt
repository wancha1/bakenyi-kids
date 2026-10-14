package com.example.ui.sanctuary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AuthenticAudioManager
import kotlinx.coroutines.launch

/**
 * Standard Modern Culture & Music Screen ("Obuwangwa n'Ennyimba").
 * Offers authentic interactive Engoma drums, Kalimba thumb piano, and Elder Proverbs ("Enfumo").
 */
@Composable
fun StorybookCultureScreen(
    audioManager: AuthenticAudioManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val spreads = remember { getAllStorybookSpreads() }
    val proverbs = remember(spreads) {
        spreads.filter { it.proverbLukenye.isNotEmpty() }.distinctBy { it.proverbLukenye }
    }

    val drumPads = remember {
        listOf(
            Triple("Engoma Enene", "Deep Bass Beat", 0),
            Triple("Empuunyi", "Low Resonant Tone", 1),
            Triple("Nankasa", "High Melody Beat", 2),
            Triple("Engalabi", "Long Drum Snap", 3)
        )
    }

    val kalimbaTines = remember {
        listOf(
            Pair("C4 (Do)", 261.63f),
            Pair("D4 (Re)", 293.66f),
            Pair("E4 (Mi)", 329.63f),
            Pair("G4 (Sol)", 392.00f),
            Pair("A4 (La)", 440.00f)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
            .testTag("storybook_culture_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Engoma Drum Playpad Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDDD2BC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪘", fontSize = 28.sp, modifier = Modifier.padding(end = 8.dp))
                        Column {
                            Text(
                                text = "Engoma z'Ekikuye (Royal Drums)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C1810)
                            )
                            Text(
                                text = "Tap the authentic drums to play traditional rhythm beats",
                                fontSize = 11.sp,
                                color = Color(0xFF8D6E63)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        drumPads.forEach { (name, desc, toneIndex) ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = if (isPressed) 0.92f else 1.0f
                                        scaleY = if (isPressed) 0.92f else 1.0f
                                    }
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) {
                                        when (toneIndex) {
                                            0 -> audioManager.playVillageDrum()
                                            1 -> audioManager.playCelebrationChime()
                                            2 -> audioManager.playWaterDrop()
                                            else -> audioManager.playBirdWhistle()
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(if (isPressed) Color(0xFFB8860B) else Color(0xFFEFE8D8))
                                        .border(2.dp, Color(0xFF8D6E63), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🪘", fontSize = 26.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C1810)
                                )
                                Text(
                                    text = desc,
                                    fontSize = 9.sp,
                                    color = Color(0xFF8D6E63)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Kalimba Thumb Piano Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDDD2BC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎵", fontSize = 28.sp, modifier = Modifier.padding(end = 8.dp))
                        Column {
                            Text(
                                text = "Akogo / Kalimba (Thumb Piano)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C1810)
                            )
                            Text(
                                text = "Pentatonic acoustic scales for Lake Kyoga melodies",
                                fontSize = 11.sp,
                                color = Color(0xFF8D6E63)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        kalimbaTines.forEachIndexed { idx, (note, freq) ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .graphicsLayer {
                                        scaleY = if (isPressed) 0.94f else 1.0f
                                    }
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) {
                                        audioManager.playKalimbaChime(idx)
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(84.dp - (idx * 6).dp)
                                        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                                        .background(if (isPressed) Color(0xFFD4AF37) else Color(0xFFD7CCC8))
                                        .border(
                                            1.5.dp,
                                            Color(0xFF8D6E63),
                                            RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                                        ),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2C1810),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note,
                                    fontSize = 10.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Section Title: Elder Proverbs
        item {
            Text(
                text = "🌾 Enfumo z'Abakulu (Elder Proverbs)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C1810)
            )
        }

        // 4. Proverbs List
        items(proverbs) { spread ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        audioManager.speakStorySentence(
                            sentenceLukenye = spread.proverbLukenye,
                            sentenceEnglish = spread.proverbEnglish
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD2BC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\"${spread.proverbLukenye}\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C1810),
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFE8D8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔊", fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Wisdom: \"${spread.proverbEnglish}\"",
                        fontSize = 12.sp,
                        color = Color(0xFF6D4C41)
                    )

                    if (spread.culturalFact.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAF7F0), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE8E0D2), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "📜 Heritage: ${spread.culturalFact}",
                                fontSize = 11.sp,
                                color = Color(0xFF8D6E63)
                            )
                        }
                    }
                }
            }
        }
    }
}
