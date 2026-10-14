package com.example.ui.sanctuary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.AuthenticAudioManager

/**
 * Full Page Content Detail Modal ("Ebirimu Byonna eby'Omuko Guno").
 * Displays ALL content for the active storybook page:
 * 1. Book & Spread Title with Page Number
 * 2. Full Story Narrative with "Soma Nange" Audio Read Aloud
 * 3. Conversational Dialogue & Spoken Phrases with Pronunciation & Audio
 * 4. All Page Vocabulary Items with Emojis & Audio
 * 5. Traditional Elder Proverb (Enfumo) & Wisdom Lesson
 * 6. Bakenyi Cultural Heritage & Lore
 * 7. Kato the Otter's Page Learning Challenge with Voice Audio
 * 8. Traditional Song / Lakeside Chant (if present)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageContentDetailModal(
    spread: StorybookSpread,
    audioManager: AuthenticAudioManager,
    onDismiss: () -> Unit
) {
    var currentlyPlayingText by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = {
            audioManager.stopSpeaking()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .shadow(16.dp, RoundedCornerShape(26.dp))
                .clip(RoundedCornerShape(26.dp))
                .border(3.dp, Color(0xFFC4A482), RoundedCornerShape(26.dp))
                .testTag("page_content_detail_modal"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // ==========================================
                // 1. HEADER: BOOK & PAGE TITLE
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = spread.bookIconEmoji,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Column {
                            Text(
                                text = "${spread.bookTitleLukenye} • ${spread.bookTitleEnglish}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8D6E63)
                            )
                            Text(
                                text = spread.titleLukenye,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3E2723)
                            )
                            Text(
                                text = "${spread.titleEnglish} (Page ${spread.pageNumberInBook} of ${spread.totalPagesInBook})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }

                    // Close Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFE8D8))
                            .clickable {
                                audioManager.stopSpeaking()
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE2D6C5), thickness = 1.5.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // 2. STORY NARRATIVE ("Soma Nange")
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F4EB)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD2B48C))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📜 Ekitontome ky'Omuko (Story Narrative)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )

                            // Read Aloud Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFD2B48C))
                                    .clickable {
                                        if (currentlyPlayingText == "story") {
                                            audioManager.stopSpeaking()
                                            currentlyPlayingText = null
                                        } else {
                                            currentlyPlayingText = "story"
                                            audioManager.speakStorySentence(
                                                sentenceLukenye = spread.narrativeLukenye,
                                                sentenceEnglish = spread.narrativeEnglish,
                                                onComplete = { currentlyPlayingText = null }
                                            )
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("modal_read_narrative_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (currentlyPlayingText == "story") "⏹️ Stop" else "🔊 Soma Nange",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3E2723)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${spread.narrativeLukenye}\"",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723),
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = spread.narrativeEnglish,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF6D4C41),
                            lineHeight = 18.sp
                        )
                    }
                }

                // ==========================================
                // 3. CONVERSATIONAL DIALOGUE & SPOKEN PHRASES
                // ==========================================
                if (spread.phrases.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "💬 Emboozi n'Ebigambo by'Okubaza (Spoken Phrases)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        spread.phrases.forEachIndexed { index, phrase ->
                            val isPlaying = currentlyPlayingText == "phrase_$index"
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFAF4)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDECDBB))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = phrase.speakerEmoji,
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = phrase.lukenye,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3E2723)
                                        )
                                        if (phrase.pronunciation.isNotEmpty()) {
                                            Text(
                                                text = "Say: [${phrase.pronunciation}]",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF8D6E63)
                                            )
                                        }
                                        Text(
                                            text = phrase.english,
                                            fontSize = 11.sp,
                                            color = Color(0xFF5D4037)
                                        )
                                    }

                                    // Listen Button for Phrase
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isPlaying) Color(0xFFD2B48C) else Color(0xFFEFE8D8))
                                            .clickable {
                                                if (isPlaying) {
                                                    audioManager.stopSpeaking()
                                                    currentlyPlayingText = null
                                                } else {
                                                    currentlyPlayingText = "phrase_$index"
                                                    audioManager.speakPhrase(
                                                        phraseLukenye = phrase.lukenye,
                                                        phraseEnglish = phrase.english,
                                                        onComplete = { currentlyPlayingText = null }
                                                    )
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isPlaying) "⏹️" else "🔊",
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. ALL PAGE VOCABULARY ITEMS
                // ==========================================
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "🔤 Ebigambo Byonna eby'Omuko Guno (${spread.interactiveObjects.size} Words)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    spread.interactiveObjects.forEachIndexed { index, obj ->
                        val isPlaying = currentlyPlayingText == "obj_$index"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFD7CCC8))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = obj.illustrationEmoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier.padding(end = 10.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = obj.labelLukenye,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3E2723)
                                    )
                                    Text(
                                        text = "Pronunciation: \"${obj.pronunciation}\"",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF8D6E63)
                                    )
                                    Text(
                                        text = "Meaning: ${obj.labelEnglish}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF5D4037)
                                    )
                                }

                                // Pronounce Object Audio
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlaying) Color(0xFFD2B48C) else Color(0xFFEFE8D8))
                                        .clickable {
                                            currentlyPlayingText = "obj_$index"
                                            audioManager.playObjectTapSoundAndSpeech(
                                                objectEmoji = obj.illustrationEmoji,
                                                lukenyeWord = obj.labelLukenye,
                                                englishWord = obj.labelEnglish,
                                                pronunciation = obj.pronunciation,
                                                noteSeed = index
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🔊", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 5. KATO'S LEARNING CHALLENGE
                // ==========================================
                if (spread.katoPromptLukenye.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB74D))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🦦",
                                fontSize = 32.sp,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Kato agamba (Kato's Challenge):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "\"${spread.katoPromptLukenye}\"",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723)
                                )
                                Text(
                                    text = spread.katoPromptEnglish,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6D4C41)
                                )
                            }

                            // Kato Voice Playback
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFCC80))
                                    .clickable {
                                        audioManager.playKatoPagePrompt(
                                            lukenyePrompt = spread.katoPromptLukenye,
                                            englishPrompt = spread.katoPromptEnglish
                                        )
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🐾 Ask Kato",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 6. PROVERB & ELDER WISDOM (Enfumo)
                // ==========================================
                if (spread.proverbLukenye.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFAED581))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🌱 Enfumo y'Abakulu (Traditional Elder Wisdom):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF33691E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"${spread.proverbLukenye}\"",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "Wisdom: \"${spread.proverbEnglish}\"",
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFF2E7D32)
                            )
                            if (spread.culturalFact.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🏛️ Heritage Lore: ${spread.culturalFact}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF37474F)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 7. TRADITIONAL SONG / CHANT (If present)
                // ==========================================
                if (spread.culturalSong.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFB39DDB))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎵 Oluyimba lw'Ekyalo (Lakeside Chant)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A148C)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFD1C4E9))
                                        .clickable {
                                            audioManager.playVillageDrum()
                                            audioManager.playKalimbaChime(3)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "🪘 Play Beat",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4A148C)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = spread.culturalSong,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFF311B92)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF8D6E63))
                        .clickable {
                            audioManager.stopSpeaking()
                            onDismiss()
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Weebale nnyo (Back to Story)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
