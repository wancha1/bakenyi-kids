package com.example.ui.sanctuary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AuthenticAudioManager

/**
 * Redesigned Storybook Reading Deck ("Ekitabo ky'Emboozi").
 * Grounded at the bottom of the picture book, uniting story narration,
 * live audio read-aloud, elder proverbs, and tactile page-turn navigation controls.
 */
@Composable
fun StorybookReadingDeck(
    spread: StorybookSpread,
    audioManager: AuthenticAudioManager,
    currentPage: Int,
    totalPages: Int,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onViewAllPageContent: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isNarrating by remember { mutableStateOf(false) }
    var showProverb by remember { mutableStateOf(false) }

    val wave1 = 14f
    val wave2 = 18f
    val wave3 = 10f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFFFFFDF8))
            .border(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(Color(0xFFD4AF37), Color(0xFFC4A482), Color(0xFF8D6E63))
                ),
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("storybook_narrative_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Tactile Previous Page Tab
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (currentPage > 0) Color(0xFFEFE8D8) else Color(0xFFF5F0E6))
                    .border(
                        1.5.dp,
                        if (currentPage > 0) Color(0xFFC4A482) else Color(0xFFE8E0D2),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(enabled = currentPage > 0) { onPrevClicked() }
                    .testTag("prev_page_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◀",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentPage > 0) Color(0xFF3E2723) else Color(0xFFBCAAA4)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 2. Center Reading Narrative Core
            Column(modifier = Modifier.weight(1f)) {
                // Header row inside reading deck: Title & Content shortcut
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "📖 ${spread.titleLukenye}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2C1810)
                        )
                        Text(
                            text = " • ${spread.titleEnglish}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6D4C41),
                            modifier = Modifier.padding(start = 3.dp)
                        )
                    }

                    // "All Page Content" Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFE8D8))
                            .border(1.dp, Color(0xFFC4A482), RoundedCornerShape(10.dp))
                            .clickable { onViewAllPageContent() }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("view_all_page_content_button")
                    ) {
                        Text(
                            text = "📄 Content",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Primary Story sentences
                Text(
                    text = spread.narrativeLukenye,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C1810),
                    lineHeight = 18.sp
                )
                Text(
                    text = spread.narrativeEnglish,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF5D4037),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Interactive Action Pills: "Soma Nange" and "Enfumo"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Audio Read Aloud Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isNarrating) Color(0xFFD2B48C) else Color(0xFFEFE8D8))
                            .border(1.5.dp, Color(0xFFC4A482), RoundedCornerShape(12.dp))
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
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("read_aloud_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isNarrating) {
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
                                Text(
                                    text = "🔊",
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                            Text(
                                text = if (isNarrating) "Stop" else "Soma Nange",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E2723)
                            )
                        }
                    }

                    // Proverb Pill Toggle
                    if (spread.proverbLukenye.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (showProverb) Color(0xFFD2B48C).copy(alpha = 0.5f) else Color(0xFFEFE8D8))
                                .border(1.dp, Color(0xFFC4A482), RoundedCornerShape(12.dp))
                                .clickable { showProverb = !showProverb }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (showProverb) "🌾 Enfumo ▲" else "🌾 Enfumo ▼",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF3E2723)
                            )
                        }
                    }
                }

                // Expandable Proverb View
                AnimatedVisibility(
                    visible = showProverb && spread.proverbLukenye.isNotEmpty(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .background(Color(0xFFFAF6EE), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFDDD2BC), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "\"${spread.proverbLukenye}\"",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                        Text(
                            text = "Wisdom: \"${spread.proverbEnglish}\"",
                            fontSize = 10.sp,
                            color = Color(0xFF5D4037)
                        )
                        if (spread.culturalFact.isNotEmpty()) {
                            Text(
                                text = spread.culturalFact,
                                fontSize = 9.sp,
                                color = Color(0xFF8D6E63),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 3. Tactile Next Page Tab
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (currentPage < totalPages - 1) Color(0xFFEFE8D8) else Color(0xFFF5F0E6))
                    .border(
                        1.5.dp,
                        if (currentPage < totalPages - 1) Color(0xFFC4A482) else Color(0xFFE8E0D2),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(enabled = currentPage < totalPages - 1) { onNextClicked() }
                    .testTag("next_page_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentPage < totalPages - 1) Color(0xFF3E2723) else Color(0xFFBCAAA4)
                )
            }
        }
    }
}

/**
 * Backward compatibility alias for StorybookNarrativeBar.
 */
@Composable
fun StorybookNarrativeBar(
    spread: StorybookSpread,
    audioManager: AuthenticAudioManager,
    modifier: Modifier = Modifier,
    onViewAllPageContent: () -> Unit = {}
) {
    StorybookReadingDeck(
        spread = spread,
        audioManager = audioManager,
        currentPage = spread.pageIndex,
        totalPages = 36,
        onPrevClicked = {},
        onNextClicked = {},
        onViewAllPageContent = onViewAllPageContent,
        modifier = modifier
    )
}
