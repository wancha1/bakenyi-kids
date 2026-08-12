package com.example.ui.sanctuary

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AuthenticAudioManager
import com.example.language.context.AppLanguageContext
import com.example.language.context.LanguageMode
import com.example.language.model.VerificationStatus
import com.example.ui.BakenyeViewModel

@Composable
fun LivingSanctuaryScreen(
    viewModel: BakenyeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioManager = remember { AuthenticAudioManager.getInstance(context) }
    val languageContext = remember { AppLanguageContext(context) }
    val currentLanguageMode by languageContext.currentLanguageMode.collectAsState()

    var activeBiome by remember { mutableStateOf(SanctuaryBiome.RIVER_WETLANDS) }
    var timeOfDay by remember { mutableStateOf(DiurnalTimeOfDay.MORNING_DAWN) }
    var showParentOverlay by remember { mutableStateOf(false) }
    var activeSpeechBubble by remember { mutableStateOf<String?>(null) }

    // Pre-configured Living Ecosystem Inhabitants
    val inhabitants = remember {
        listOf(
            SanctuaryInhabitant(
                id = "KIGO_OTTER",
                name = "Kigo the Otter",
                nativeTitle = "Eryato (Canoe)",
                emojiIcon = "🦦",
                biome = SanctuaryBiome.RIVER_WETLANDS,
                naturalBehaviorDesc = "Kigo rolls river stones and greets children near the canoe landing.",
                nativeAudioPhrase = "Oli otya! Weebale okujja ekyato!",
                englishMeaning = "Hello! Welcome to the canoe landing!",
                pronunciation = "Oh-lee oh-tyah! Wee-bah-leh oh-koo-jjah eh-kyah-toh!",
                verificationStatus = VerificationStatus.VERIFIED
            ),
            SanctuaryInhabitant(
                id = "NALUBA_KINGFISHER",
                name = "Naluba the Kingfisher",
                nativeTitle = "Ensomba (Fish)",
                emojiIcon = "🐦",
                biome = SanctuaryBiome.RIVER_WETLANDS,
                naturalBehaviorDesc = "Perches on papyrus reeds and dips into water to catch fish.",
                nativeAudioPhrase = "Laba ensomba mu nnyanja!",
                englishMeaning = "Look at the fish in the lake!",
                pronunciation = "Lah-bah en-sohm-bah moo n-nyah-njah!",
                verificationStatus = VerificationStatus.VERIFIED
            ),
            SanctuaryInhabitant(
                id = "JJAJJA_GRANDMOTHER",
                name = "JjaJja Grandmother",
                nativeTitle = "Obwosi (Story)",
                emojiIcon = "👵🏽",
                biome = SanctuaryBiome.VILLAGE_LANDING,
                naturalBehaviorDesc = "Sweeps the clay hearth and shares ancient oral stories.",
                nativeAudioPhrase = "Tula wano omwana wange, nkuwe ebyomuwendo.",
                englishMeaning = "Sit here my child, let me share ancient wisdom.",
                pronunciation = "Too-lah wah-noh oh-mwah-nah wahn-geh...",
                verificationStatus = VerificationStatus.VERIFIED
            ),
            SanctuaryInhabitant(
                id = "SSOZI_CRANE",
                name = "Ssozi Crested Crane",
                nativeTitle = "Engwali (Crested Crane)",
                emojiIcon = "🦩",
                biome = SanctuaryBiome.HILLS_AND_MEADOWS,
                naturalBehaviorDesc = "Performs gentle dancing steps to greet the morning sun.",
                nativeAudioPhrase = "Jjangu tuzine mu musiri!",
                englishMeaning = "Come let us dance in the meadow!",
                pronunciation = "Jjan-goo too-zee-neh moo moo-see-ree!",
                verificationStatus = VerificationStatus.VERIFIED
            ),
            SanctuaryInhabitant(
                id = "LUMU_MONKEY",
                name = "Lumu Vervet Monkey",
                nativeTitle = "Ebibala (Fruit)",
                emojiIcon = "🐒",
                biome = SanctuaryBiome.BAOBAB_FOREST,
                naturalBehaviorDesc = "Swings in the baobab canopy dropping wild figs into water.",
                nativeAudioPhrase = "Liba ebibala ebimyufu!",
                englishMeaning = "Look at the ripe sweet fruit!",
                pronunciation = "Lee-bah eh-bee-bah-lah eh-bee-myoo-foo!",
                verificationStatus = VerificationStatus.VERIFIED
            )
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            audioManager.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("living_sanctuary_screen")
    ) {
        // 1. Edge-to-Edge Interactive Sanctuary Canvas
        SanctuaryCanvas(
            timeOfDay = timeOfDay,
            activeBiome = activeBiome,
            modifier = Modifier.fillMaxSize(),
            onWaterTap = {
                audioManager.playSuccessSound()
            }
        )

        // 2. Inhabitants Anchor Placement for Active Biome
        val visibleInhabitants = inhabitants.filter { it.biome == activeBiome }
        visibleInhabitants.forEachIndexed { index, inhabitant ->
            val xPos = when (index) {
                0 -> (-60).dp
                1 -> 70.dp
                else -> 0.dp
            }
            val yPos = when (index) {
                0 -> 40.dp
                1 -> (-30).dp
                else -> 80.dp
            }

            InhabitantAnchor(
                inhabitant = inhabitant,
                onTap = { selected ->
                    activeSpeechBubble = selected.nativeAudioPhrase
                    audioManager.playPronunciation("bakenye_sample") {
                        // Audio completed
                    }
                },
                modifier = Modifier.align(Alignment.Center),
                xOffset = xPos,
                yOffset = yPos
            )
        }

        // 3. Top Natural Biome Switcher (Embedded into nature)
        LazyRow(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 48.dp, end = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(SanctuaryBiome.values()) { biome ->
                val isSelected = activeBiome == biome
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) Color(0xFF2A9D8F) else Color(0xFF3E2723).copy(alpha = 0.75f)
                        )
                        .border(
                            1.5.dp,
                            if (isSelected) Color(0xFFE9C46A) else Color(0xFF8D6E63),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { activeBiome = biome }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = biome.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFF8E1)
                    )
                }
            }
        }

        // 4. Time of Day Diurnal Cycle Switcher (Sun/Moon Ember)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF3E2723).copy(alpha = 0.80f))
                .clickable { timeOfDay = timeOfDay.next() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (timeOfDay) {
                        DiurnalTimeOfDay.MORNING_DAWN -> "🌅 Dawn"
                        DiurnalTimeOfDay.MIDDAY_SUN -> "☀️ Midday"
                        DiurnalTimeOfDay.EVENING_GOLD -> "🌇 Evening"
                        DiurnalTimeOfDay.TWILIGHT_DUSK -> "🌙 Twilight"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFF8E1)
                )
            }
        }

        // 5. Active Inhabitant Native Speech Bubble (Spoken Heritage Response)
        activeSpeechBubble?.let { speech ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 24.dp, end = 24.dp)
                    .clickable { activeSpeechBubble = null },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD2B48C))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🗣️ \"$speech\"",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "(Tap anywhere to close)",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // 6. Discreet Embers Gate (Top-Right Parent Area Lock)
        EmbersGateButton(
            onGateUnlocked = { showParentOverlay = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
        )

        // 7. Parent Sanctuary Settings Overlay Modal
        if (showParentOverlay) {
            ParentSanctuaryOverlay(
                currentLanguageMode = currentLanguageMode,
                onLanguageModeChanged = { newMode ->
                    languageContext.setLanguageMode(newMode)
                },
                onDismiss = { showParentOverlay = false }
            )
        }
    }
}
