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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.language.context.LanguageMode

/**
 * Standard Modern Settings & Heritage Screen ("Enteekateeka z'Ekitabo").
 * Offers standard Material 3 configuration for Language Immersion,
 * Parent Controls, Audio options, and Cultural Integrity.
 */
@Composable
fun StorybookSettingsScreen(
    currentLanguageMode: LanguageMode,
    onLanguageModeChanged: (LanguageMode) -> Unit,
    onOpenParentOverlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
            .testTag("storybook_settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Language Immersion Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD2BC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🌐 Language Immersion Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C1810)
                    )
                    Text(
                        text = "Choose how stories, phrases, and vocabulary are displayed",
                        fontSize = 12.sp,
                        color = Color(0xFF8D6E63)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val modes = listOf(
                        Triple(
                            LanguageMode.DUAL_SIDE_BY_SIDE,
                            "Bilingual Equal (Recommended)",
                            "Shows Lukenye alongside English with full pronunciation guides"
                        ),
                        Triple(
                            LanguageMode.LUKENYE_PRIMARY,
                            "Lukenye Immersion",
                            "Full Lukenye immersion text with optional English subtitles"
                        ),
                        Triple(
                            LanguageMode.ENGLISH_PRIMARY,
                            "English Primary",
                            "English text with Lukenye vocabulary callouts"
                        )
                    )

                    modes.forEach { (mode, title, desc) ->
                        val isSelected = currentLanguageMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onLanguageModeChanged(mode) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onLanguageModeChanged(mode) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF8D6E63)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C1810)
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6D4C41)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Parent Gate & Security Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenParentOverlay() }
                    .testTag("settings_parent_gate_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD2BC)),
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
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFE8D8))
                            .border(1.dp, Color(0xFFC4A482), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔒", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Parent Sanctuary Gate",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C1810)
                        )
                        Text(
                            text = "Manage safety PIN, screen time limits & language controls",
                            fontSize = 11.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFE8D8))
                            .border(1.dp, Color(0xFFC4A482), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Open ▾",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }
                }
            }
        }

        // 3. Cultural Heritage Verification Badge Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD4AF37)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🛡️", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = "Verified Lukenye Oral Heritage",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C1810)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "100% Authentic Oral Record from Bakenye Elders of Lake Kyoga, Buyende, Iganga, and Pallisa. Fully certified offline database without AI hallucinations or synthetic replacement.",
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFE8D8), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Database Version: 1.0.4 • 18 Storybooks • 36 Spreads • 108 Verified Words",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6D4C41)
                        )
                    }
                }
            }
        }
    }
}
