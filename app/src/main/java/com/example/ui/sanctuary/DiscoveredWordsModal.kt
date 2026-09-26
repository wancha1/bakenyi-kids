package com.example.ui.sanctuary

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.audio.AuthenticAudioManager

/**
 * Discovered Words Journal Modal (Akatabo k'Ebigambo).
 * Shows words explored by the child, total collection count, badges, and allows replaying audio pronunciation.
 */
@Composable
fun DiscoveredWordsModal(
    allSpreads: List<StorybookSpread>,
    discoveredObjectIds: Set<String>,
    audioManager: AuthenticAudioManager,
    onDismiss: () -> Unit
) {
    val allObjects = allSpreads.flatMap { it.interactiveObjects }.distinctBy { it.id }
    val discoveredCount = allObjects.count { it.id in discoveredObjectIds }
    val totalCount = allObjects.size

    val explorerRank = when {
        discoveredCount >= 25 -> "👑 Omukulu w'Enfumo (Master Storyteller)"
        discoveredCount >= 12 -> "🛶 Omulunnyanja (Lake Voyager)"
        discoveredCount >= 5 -> "🌱 Omuyizi Omuto (Early Explorer)"
        else -> "🌟 Omutandisi (Curious Beginner)"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("discovered_words_journal_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F0)),
            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFC4A482))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📖", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Akatabo k'Ebigambo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E2723)
                            )
                            Text(
                                text = "My Discovered Words Journal",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEFE8D8))
                        .border(1.dp, Color(0xFFD7CCC8), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = explorerRank,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E2723)
                            )
                            Text(
                                text = "Tap any word to listen to its pronunciation",
                                fontSize = 10.sp,
                                color = Color(0xFF795548)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFD2B48C))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "⭐ $discoveredCount / $totalCount",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E2723)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Grid of Words
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(360.dp)
                ) {
                    items(allObjects) { obj ->
                        val isDiscovered = obj.id in discoveredObjectIds

                        Box(
                            modifier = Modifier
                                .shadow(2.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDiscovered) Color(0xFFFFFDF5) else Color(0xFFEDE7DD))
                                .border(
                                    1.dp,
                                    if (isDiscovered) Color(0xFF8D6E63) else Color(0xFFD7CCC8),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    audioManager.playObjectTapSoundAndSpeech(
                                        objectEmoji = obj.illustrationEmoji,
                                        lukenyeWord = obj.labelLukenye,
                                        englishWord = obj.labelEnglish,
                                        pronunciation = obj.pronunciation
                                    )
                                }
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = obj.illustrationEmoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = obj.labelLukenye,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDiscovered) Color(0xFF3E2723) else Color(0xFF8D6E63)
                                    )
                                    Text(
                                        text = obj.labelEnglish,
                                        fontSize = 10.sp,
                                        color = Color(0xFF6D4C41)
                                    )
                                    Text(
                                        text = "\"${obj.pronunciation}\"",
                                        fontSize = 9.sp,
                                        color = Color(0xFF8D6E63)
                                    )
                                }

                                if (isDiscovered) {
                                    Text(text = "✨", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
