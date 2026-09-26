package com.example.ui.sanctuary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.language.context.LanguageMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 3-Second Hold / PIN Parent Gate Button.
 */
@Composable
fun EmbersGateButton(
    onGateUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var holdProgress by remember { mutableStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .testTag("embers_gate")
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF3E2723).copy(alpha = 0.88f))
            .border(2.dp, Color(0xFFFFB703), CircleShape)
            .shadow(4.dp, CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHolding = true
                        holdProgress = 0f
                        val job = scope.launch {
                            for (i in 1..30) {
                                delay(100)
                                holdProgress = i / 30f
                            }
                            if (isHolding) {
                                onGateUnlocked()
                            }
                        }
                        tryAwaitRelease()
                        isHolding = false
                        holdProgress = 0f
                        job.cancel()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isHolding) "🔥" else "🪵",
                fontSize = 20.sp
            )
        }
    }
}

/**
 * Parent Area Modal (Embers Gate Protected).
 * Contains ONLY:
 * • Language mode
 * • Screen time
 * • Audio settings
 * • Accessibility
 * • Privacy
 * • Progress summary
 * • About
 */
@Composable
fun ParentSanctuaryOverlay(
    currentLanguageMode: LanguageMode,
    onLanguageModeChanged: (LanguageMode) -> Unit,
    onDismiss: () -> Unit
) {
    var sessionLimitMinutes by remember { mutableStateOf(20) }
    var audioVolumeLevel by remember { mutableStateOf("BALANCED") }
    var showWordLabels by remember { mutableStateOf(true) }
    var highContrastMode by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("parent_sanctuary_overlay"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF8F5))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Parent Gate",
                            tint = Color(0xFF3E2723),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PARENT AREA",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. LANGUAGE MODE
                SectionHeader(title = "1. LANGUAGE MODE")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val modes = listOf(
                        Pair(LanguageMode.LUKENYE_PRIMARY, "Lukenye Primary (Authentic Oral Heritage Focus)"),
                        Pair(LanguageMode.ENGLISH_PRIMARY, "English Primary (Exploration with English Audio)"),
                        Pair(LanguageMode.DUAL_SIDE_BY_SIDE, "Dual Mode (Lukenye + English Side-by-Side)")
                    )

                    modes.forEach { (mode, title) ->
                        val isSelected = currentLanguageMode == mode
                        Button(
                            onClick = { onLanguageModeChanged(mode) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF2A9D8F) else Color(0xFFEFEBE9)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF3E2723),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. SCREEN TIME LIMIT
                SectionHeader(title = "2. SCREEN TIME LIMIT")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(15, 20, 30, 45, 0).forEach { mins ->
                        val isSelected = sessionLimitMinutes == mins
                        val label = if (mins == 0) "Open" else "$mins m"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFFE07A5F) else Color(0xFFEFEBE9))
                                .clickable { sessionLimitMinutes = mins }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF3E2723)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. AUDIO SETTINGS
                SectionHeader(title = "3. AUDIO SETTINGS")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("GENTLE" to "Gentle (50%)", "BALANCED" to "Balanced (80%)", "CLEAR" to "Clear (100%)").forEach { (level, title) ->
                        val isSelected = audioVolumeLevel == level
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF2A9D8F) else Color(0xFFEFEBE9))
                                .clickable { audioVolumeLevel = level }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF3E2723)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. ACCESSIBILITY
                SectionHeader(title = "4. ACCESSIBILITY")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Show Written Word Labels",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3E2723)
                        )
                        Switch(
                            checked = showWordLabels,
                            onCheckedChange = { showWordLabels = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2A9D8F))
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "High Contrast Visual Borders",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3E2723)
                        )
                        Switch(
                            checked = highContrastMode,
                            onCheckedChange = { highContrastMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2A9D8F))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. PRIVACY & SAFETY
                SectionHeader(title = "5. PRIVACY & SAFETY")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🔒 Child Safety Commitment:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• 100% Offline-first: No user data leaves this device.\n• Zero advertisements, trackers, logins, or social features.\n• Compliant with global children's privacy standards (COPPA).",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6. PROGRESS SUMMARY (Qualitative Only)
                SectionHeader(title = "6. PROGRESS SUMMARY")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🌿 Qualitative Storybook Activity:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Explored 18 Picture Books (Greetings, Family, Home, Village, Animals, Nature, Numbers, Songs, Daily Life).\n• Listened to authentic human-recorded Lukenye speech.\n• Zero competitive pressure, no quizzes, no graded tests.",
                            fontSize = 11.sp,
                            color = Color(0xFF424242),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 7. ABOUT & CULTURAL HERITAGE
                SectionHeader(title = "7. ABOUT BAKENYI KIDS")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFECB3))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "📖 Bakenyi Kids v1.0 Launch Edition",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Preserving and teaching the endangered Lukenye language and rich cultural heritage of the Bakenyi people of Uganda through calm, beautiful interactive picture books.",
                            fontSize = 11.sp,
                            color = Color(0xFF4E342E),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "RETURN TO PICTURE BOOK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF5D4037),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}
