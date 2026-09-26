package com.example.ui.sanctuary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AuthenticAudioManager

/**
 * Storybook Narrative Bar ("Soma Nange" / Read With Me).
 * Displays rich story sentences in Lukenye and English with an audio narration trigger
 * and an animated soundwave indicator.
 */
@Composable
fun StorybookNarrativeBar(
    spread: StorybookSpread,
    audioManager: AuthenticAudioManager,
    modifier: Modifier = Modifier
) {
    if (spread.narrativeLukenye.isEmpty()) return

    var isNarrating by remember { mutableStateOf(false) }
    var showProverb by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "WaveEqualizer")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Wave1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Wave2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(280, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Wave3"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFFFDF8).copy(alpha = 0.94f))
            .border(1.5.dp, Color(0xFFC4A482), RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("storybook_narrative_bar")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spread.narrativeLukenye,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )
                    Text(
                        text = spread.narrativeEnglish,
                        fontSize = 11.sp,
                        color = Color(0xFF6D4C41)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // "Soma Nange" Audio Read Aloud Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isNarrating) Color(0xFFD2B48C) else Color(0xFFEFE8D8))
                        .clickable {
                            if (isNarrating) {
                                audioManager.stopSpeaking()
                                isNarrating = false
                            } else {
                                isNarrating = true
                                audioManager.speakStorySentence(
                                    sentenceLukenye = spread.narrativeLukenye,
                                    sentenceEnglish = spread.narrativeEnglish,
                                    onComplete = { isNarrating = false }
                                )
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isNarrating) {
                            // Animated Soundwave Equalizer Bars
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(wave1.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3E2723))
                                )
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(wave2.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3E2723))
                                )
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(wave3.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3E2723))
                                )
                            }
                        } else {
                            Text(text = "🔊", fontSize = 14.sp, modifier = Modifier.padding(end = 4.dp))
                        }

                        Text(
                            text = if (isNarrating) "Stop" else "Soma Nange",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }
                }
            }

            // Expandable Proverb / Cultural Insight
            if (spread.proverbLukenye.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showProverb = !showProverb }
                ) {
                    Text(
                        text = "🌱 Enfumo (Proverb): \"${spread.proverbLukenye}\"",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8D6E63)
                    )
                }

                AnimatedVisibility(visible = showProverb) {
                    Text(
                        text = "Wisdom: \"${spread.proverbEnglish}\" • ${spread.culturalFact}",
                        fontSize = 9.sp,
                        color = Color(0xFF5D4037),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
