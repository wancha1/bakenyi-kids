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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Standard Modern Library Screen ("Akatabo k'Engero").
 * Modeled after modern reading apps (Google Play Books, Epic!, Apple Books).
 * Allows users to browse, search, and launch any of the 18 picture books.
 */
@Composable
fun StorybookLibraryScreen(
    currentSpreadIndex: Int,
    onOpenSpread: (Int) -> Unit,
    onOpenAdventure: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val books = remember { getLaunchStorybooks() }
    val spreads = remember { getAllStorybookSpreads() }
    val currentSpread = spreads.getOrElse(currentSpreadIndex) { spreads[0] }
    val currentBook = books.find { b -> b.spreads.any { it.pageIndex == currentSpreadIndex } } ?: books[0]

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("All Books (18)", "🎣 Lake & Fishing", "🏡 Village & Hearth", "🌿 Marsh & Nature", "👑 Wisdom & Kings")

    val filteredBooks = remember(searchQuery, selectedCategoryIndex, books) {
        books.filter { book ->
            val matchesCategory = when (selectedCategoryIndex) {
                1 -> book.spreads.firstOrNull()?.themeStyle == StorybookThemeStyle.FISHERMAN_SONG ||
                        book.spreads.firstOrNull()?.themeStyle == StorybookThemeStyle.RIVERBANK_DAWN
                2 -> book.spreads.firstOrNull()?.themeStyle == StorybookThemeStyle.VILLAGE_HEARTH
                3 -> book.spreads.firstOrNull()?.themeStyle == StorybookThemeStyle.WETLAND_MARSH
                4 -> book.id.contains("wisdom") || book.id.contains("proverb") || book.spreads.any { it.proverbLukenye.isNotEmpty() }
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    book.titleLukenye.contains(searchQuery, ignoreCase = true) ||
                    book.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                    book.shortDescription.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
            .testTag("storybook_library_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_field"),
                placeholder = {
                    Text("Search books by Lukenye or English title...", fontSize = 14.sp, color = Color(0xFF8D6E63))
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

        // 1b. Explore Lake Kyoga Adventure Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAdventure() }
                    .testTag("explore_adventure_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛶", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Explore Lake Kyoga (Adventure)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Interactive lakeside discovery with Kato the Otter",
                            fontSize = 11.sp,
                            color = Color(0xFFC8E6C9)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFD54F))
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "PLAY ▶",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2C1810)
                        )
                    }
                }
            }
        }

        // 2. Currently Reading Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenSpread(currentSpreadIndex) }
                    .testTag("continue_reading_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1810)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Book Icon Badge
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF8D6E63), Color(0xFF4E342E))
                                )
                            )
                            .border(1.5.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = currentBook.iconEmoji, fontSize = 34.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFD4AF37))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CONTINUE READING",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2C1810)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentBook.titleLukenye,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentBook.titleEnglish,
                            fontSize = 12.sp,
                            color = Color(0xFFD7CCC8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Spread ${currentSpread.pageNumberInBook} of ${currentSpread.totalPagesInBook} • ${currentBook.spreads.size} Pages",
                            fontSize = 11.sp,
                            color = Color(0xFFA1887F),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Play / Resume Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD4AF37)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Read",
                            tint = Color(0xFF2C1810)
                        )
                    }
                }
            }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Stories (${filteredBooks.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C1810)
                )
                Text(
                    text = "Bakenye Heritage Series",
                    fontSize = 12.sp,
                    color = Color(0xFF8D6E63)
                )
            }
        }

        // 5. Books List
        items(filteredBooks) { book ->
            val firstSpreadIndex = book.spreads.firstOrNull()?.pageIndex ?: 0
            val isCurrent = book.id == currentBook.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenSpread(firstSpreadIndex) }
                    .testTag("book_card_${book.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isCurrent) 2.dp else 1.dp,
                    color = if (isCurrent) Color(0xFFD4AF37) else Color(0xFFDDD2BC)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Book Cover
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF5EFEB))
                            .border(1.dp, Color(0xFFD7CCC8), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = book.iconEmoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = book.titleLukenye,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C1810),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFFF2D6))
                                        .border(1.dp, Color(0xFFD4AF37), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "CURRENT",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8B6B00)
                                    )
                                }
                            }
                        }

                        Text(
                            text = book.titleEnglish,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6D4C41),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = book.shortDescription,
                            fontSize = 11.sp,
                            color = Color(0xFF8D6E63),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📖 ${book.spreads.size} Spreads",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5D4037)
                            )
                            val vocabCount = book.spreads.flatMap { it.interactiveObjects }.distinctBy { it.id }.size
                            Text(
                                text = "⭐ $vocabCount Vocabulary Words",
                                fontSize = 10.sp,
                                color = Color(0xFF8D6E63)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Open Action Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFE8D8))
                            .border(1.dp, Color(0xFFC4A482), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Read ▶",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }
                }
            }
        }
    }
}
