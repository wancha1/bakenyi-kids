package com.example.ui.sanctuary

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AuthenticAudioManager

/**
 * Standard Modern Vocabulary Screen ("Ebigambo n'Enviiri").
 * Modeled after Duolingo / Babbel / Lingokids vocabulary explorers.
 * Allows children and elders to discover, hear, practice, and review all Lukenye words.
 */
@Composable
fun StorybookVocabularyScreen(
    audioManager: AuthenticAudioManager,
    discoveredObjectIds: Set<String>,
    modifier: Modifier = Modifier
) {
    val spreads = remember { getAllStorybookSpreads() }
    val allObjects = remember(spreads) {
        spreads.flatMap { it.interactiveObjects }.distinctBy { it.id }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("All Words (${allObjects.size})", "🐟 Lake & Waters", "🌾 Village & Home", "🐸 Nature & Wildlife", "⭐ Discovered (${discoveredObjectIds.size})")

    val filteredObjects = remember(searchQuery, selectedCategoryIndex, allObjects, discoveredObjectIds) {
        allObjects.filter { obj ->
            val matchesCategory = when (selectedCategoryIndex) {
                1 -> obj.id.contains("water") || obj.id.contains("canoe") || obj.id.contains("fish") || obj.id.contains("otter")
                2 -> obj.id.contains("hearth") || obj.id.contains("pot") || obj.id.contains("fire") || obj.id.contains("granary") || obj.id.contains("hut")
                3 -> obj.id.contains("frog") || obj.id.contains("crane") || obj.id.contains("lily") || obj.id.contains("reed") || obj.id.contains("tree")
                4 -> obj.id in discoveredObjectIds
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    obj.labelLukenye.contains(searchQuery, ignoreCase = true) ||
                    obj.labelEnglish.contains(searchQuery, ignoreCase = true) ||
                    obj.pronunciation.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
            .testTag("storybook_vocabulary_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDDD2BC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF2D6))
                            .border(1.5.dp, Color(0xFFD4AF37), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⭐", fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lukenye Vocabulary Bank",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C1810)
                        )
                        Text(
                            text = "${discoveredObjectIds.size} of ${allObjects.size} Words Explored",
                            fontSize = 12.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }
                }
            }
        }

        // 2. Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vocabulary_search_field"),
                placeholder = {
                    Text("Search words in Lukenye or English...", fontSize = 14.sp, color = Color(0xFF8D6E63))
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF6D4C41))
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF8D6E63),
                    unfocusedBorderColor = Color(0xFFDDD2BC)
                )
            )
        }

        // 3. Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.size) { idx ->
                    val isSelected = selectedCategoryIndex == idx
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF3E2723) else Color.White)
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF3E2723) else Color(0xFFDDD2BC),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategoryIndex = idx }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = categories[idx],
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF3E2723)
                        )
                    }
                }
            }
        }

        // 4. Section Title
        item {
            Text(
                text = "Words (${filteredObjects.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C1810)
            )
        }

        // 5. Vocabulary Cards List
        items(filteredObjects) { obj ->
            val isDiscovered = obj.id in discoveredObjectIds

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        audioManager.playObjectTapSoundAndSpeech(
                            objectEmoji = obj.illustrationEmoji,
                            lukenyeWord = obj.labelLukenye,
                            englishWord = obj.labelEnglish,
                            pronunciation = obj.pronunciation
                        )
                    }
                    .testTag("vocabulary_card_${obj.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDiscovered) Color(0xFFD4AF37) else Color(0xFFDDD2BC)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Illustration
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDiscovered) Color(0xFFFFFDF5) else Color(0xFFF5EFEB))
                            .border(
                                1.dp,
                                if (isDiscovered) Color(0xFFD4AF37) else Color(0xFFE0D7C6),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = obj.illustrationEmoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = obj.labelLukenye,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C1810)
                            )
                            if (isDiscovered) {
                                Text(
                                    text = " ⭐",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Text(
                            text = "Pronounce: \"${obj.pronunciation}\"",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8D6E63)
                        )

                        Text(
                            text = "English: ${obj.labelEnglish}",
                            fontSize = 12.sp,
                            color = Color(0xFF5D4037)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Audio Play Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFE8D8))
                            .border(1.dp, Color(0xFFC4A482), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Listen",
                            tint = Color(0xFF3E2723),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
