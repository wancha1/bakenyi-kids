package com.example.ui.sanctuary

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AuthenticAudioManager
import kotlinx.coroutines.launch

/**
 * Interactive Engoma (Drum) & Kalimba (Thumb Piano) Playpad.
 * Allows children to play authentic, acoustically synthesized African pentatonic tones
 * and village drum beats with tactile visual bounce feedback.
 */
@Composable
fun MusicalPlaypadModal(
    audioManager: AuthenticAudioManager,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("musical_playpad_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6EE)),
            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFC4A482))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎵", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Engoma n'Endere",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E2723)
                            )
                            Text(
                                text = "Interactive Village Instruments",
                                fontSize = 11.sp,
                                color = Color(0xFF6D4C41)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF5D4037))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Kalimba (Thumb Piano) Keys
                Text(
                    text = "🎹 Kalimba (Mbira) • Pentatonic Tines",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E342E)
                )
                Spacer(modifier = Modifier.height(8.dp))

                val kalimbaNotes = listOf(
                    Triple(0, "C", 115.dp),
                    Triple(1, "D", 100.dp),
                    Triple(2, "E", 88.dp),
                    Triple(3, "G", 102.dp),
                    Triple(4, "A", 120.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    kalimbaNotes.forEach { (pitchIdx, noteName, tineHeight) ->
                        val tineAnim = remember { Animatable(1f) }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleY = tineAnim.value
                                }
                                .shadow(4.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFD7CCC8))
                                .border(1.5.dp, Color(0xFF8D6E63), RoundedCornerShape(12.dp))
                                .clickable {
                                    audioManager.playKalimbaChime(pitchIdx)
                                    coroutineScope.launch {
                                        tineAnim.animateTo(1.15f)
                                        tineAnim.animateTo(1f, spring(dampingRatio = 0.4f))
                                    }
                                }
                                .width(46.dp)
                                .height(tineHeight)
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🌾",
                                fontSize = 14.sp
                            )
                            Text(
                                text = noteName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E2723)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Engoma (Drums)
                Text(
                    text = "🪘 Engoma • Resonant Village Drums",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E342E)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Drum 1: Big Bass Hearth Drum
                    val drum1Scale = remember { Animatable(1f) }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = drum1Scale.value
                                scaleY = drum1Scale.value
                            }
                            .clickable {
                                audioManager.playVillageDrum()
                                coroutineScope.launch {
                                    drum1Scale.animateTo(0.88f)
                                    drum1Scale.animateTo(1f, spring(dampingRatio = 0.35f))
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFF8D6E63))
                                .border(3.dp, Color(0xFF4E342E), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🪘", fontSize = 34.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Engoma Ennene\n(Bass Drum)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Drum 2: Water Ripple / Slit Drum
                    val drum2Scale = remember { Animatable(1f) }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = drum2Scale.value
                                scaleY = drum2Scale.value
                            }
                            .clickable {
                                audioManager.playWaterDrop()
                                coroutineScope.launch {
                                    drum2Scale.animateTo(0.88f)
                                    drum2Scale.animateTo(1f, spring(dampingRatio = 0.35f))
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFFB0BEC5))
                                .border(3.dp, Color(0xFF546E7A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💧", fontSize = 30.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Amazzi g'Ennyanja\n(Water Chime)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
