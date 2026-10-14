package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AuthenticAudioManager
import com.example.language.context.AppLanguageContext
import com.example.language.context.LanguageMode
import com.example.ui.BakenyeViewModel
import com.example.ui.sanctuary.EmbersGateButton
import com.example.ui.sanctuary.LivingSanctuaryScreen
import com.example.ui.sanctuary.ParentSanctuaryOverlay
import com.example.ui.sanctuary.StorybookCultureScreen
import com.example.ui.sanctuary.StorybookLibraryScreen
import com.example.ui.sanctuary.StorybookSettingsScreen
import com.example.ui.sanctuary.StorybookVocabularyScreen
import com.example.ui.worldengine.WorldEngineScreen

/**
 * Standard app destinations matching modern educational and reading apps
 * (such as Google Play Books, Duolingo, Epic!, Apple Books).
 */
enum class AppDestination(
    val route: String,
    val titleLukenye: String,
    val titleEnglish: String,
    val icon: ImageVector,
    val tag: String
) {
    ADVENTURE("adventure", "Omuzannyo", "Adventure", Icons.Default.Explore, "nav_adventure"),
    READER("reader", "Soma", "Read", Icons.Default.AutoStories, "nav_reader"),
    LIBRARY("library", "Ebitabo", "Library", Icons.Default.LibraryBooks, "nav_library"),
    VOCABULARY("vocabulary", "Amagambo", "Words", Icons.Default.Translate, "nav_vocabulary"),
    CULTURE("culture", "Obuwangwa", "Culture", Icons.Default.MusicNote, "nav_culture"),
    SETTINGS("settings", "Enteekateeka", "Settings", Icons.Default.Settings, "nav_settings")
}

@Composable
fun MainAppScreen(
    viewModel: BakenyeViewModel,
    initialShowWorldEngine: Boolean = true
) {
    val context = LocalContext.current
    val audioManager = remember { AuthenticAudioManager.getInstance(context) }
    val languageContext = remember { AppLanguageContext(context) }
    val currentLanguageMode by languageContext.currentLanguageMode.collectAsState()

    var currentDestination by remember {
        mutableStateOf(if (initialShowWorldEngine) AppDestination.ADVENTURE else AppDestination.READER)
    }
    var currentActivePageIndex by remember { mutableIntStateOf(0) }
    var discoveredWordsCount by remember { mutableIntStateOf(12) }
    var showParentOverlay by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_app_scaffold"),
        topBar = {
            MainAppTopBar(
                currentDestination = currentDestination,
                currentLanguageMode = currentLanguageMode,
                discoveredWordsCount = discoveredWordsCount,
                onWordBadgeClicked = { currentDestination = AppDestination.VOCABULARY },
                onLanguageChipClicked = { currentDestination = AppDestination.SETTINGS },
                onParentGateUnlocked = { showParentOverlay = true }
            )
        },
        bottomBar = {
            MainAppBottomNavigationBar(
                currentDestination = currentDestination,
                onNavigate = { destination ->
                    currentDestination = destination
                }
            )
        },
        containerColor = Color(0xFFF9F6F0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestination.ADVENTURE -> {
                    WorldEngineScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            currentDestination = AppDestination.READER
                        }
                    )
                }

                AppDestination.READER -> {
                    LivingSanctuaryScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize(),
                        activePageIndex = currentActivePageIndex,
                        onNavigateToLibrary = { currentDestination = AppDestination.LIBRARY },
                        onPageChanged = { newPage ->
                            currentActivePageIndex = newPage
                        }
                    )
                }

                AppDestination.LIBRARY -> {
                    StorybookLibraryScreen(
                        currentSpreadIndex = currentActivePageIndex,
                        onOpenSpread = { targetIndex ->
                            currentActivePageIndex = targetIndex
                            currentDestination = AppDestination.READER
                        },
                        onOpenAdventure = {
                            currentDestination = AppDestination.ADVENTURE
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                AppDestination.VOCABULARY -> {
                    StorybookVocabularyScreen(
                        audioManager = audioManager,
                        discoveredObjectIds = setOf("canoe_1", "lake_fish_1", "water_lily_1", "reed_bundle_1", "hearth_pot_1", "fire_1"),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                AppDestination.CULTURE -> {
                    StorybookCultureScreen(
                        audioManager = audioManager,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                AppDestination.SETTINGS -> {
                    StorybookSettingsScreen(
                        currentLanguageMode = currentLanguageMode,
                        onLanguageModeChanged = { newMode ->
                            languageContext.setLanguageMode(newMode)
                        },
                        onOpenParentOverlay = { showParentOverlay = true },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Parent Sanctuary Overlay Modal
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

/**
 * Standard Modern Top App Bar.
 */
@Composable
private fun MainAppTopBar(
    currentDestination: AppDestination,
    currentLanguageMode: LanguageMode,
    discoveredWordsCount: Int,
    onWordBadgeClicked: () -> Unit,
    onLanguageChipClicked: () -> Unit,
    onParentGateUnlocked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFFFFFDF8))
            .border(width = 1.dp, color = Color(0xFFDDD2BC))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App Title & Tagline
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Bakenye",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2C1810)
                )
                Text(
                    text = " • ${currentDestination.titleLukenye}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8D6E63)
                )
            }
            Text(
                text = "Lukenye Heritage Stories",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D4C41)
            )
        }

        // Action Cluster
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Language Mode Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFE8D8))
                    .border(1.dp, Color(0xFFC4A482), RoundedCornerShape(12.dp))
                    .clickable { onLanguageChipClicked() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("topbar_language_chip"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (currentLanguageMode) {
                        LanguageMode.LUKENYE_PRIMARY -> "🇺🇬 Lukenye"
                        LanguageMode.ENGLISH_PRIMARY -> "🇬🇧 English"
                        else -> "🇺🇬 Bilingual"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            }

            // Word Explorer Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF2D6))
                    .border(1.dp, Color(0xFFD4AF37), RoundedCornerShape(12.dp))
                    .clickable { onWordBadgeClicked() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("topbar_words_badge"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⭐ $discoveredWordsCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B6B00)
                )
            }

            // Parent Gate Lock Clasp
            EmbersGateButton(
                onGateUnlocked = onParentGateUnlocked
            )
        }
    }
}

/**
 * Standard Modern Bottom Navigation Bar.
 * Clear touch targets (>= 48dp), standard Material 3 icons and labels.
 */
@Composable
private fun MainAppBottomNavigationBar(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .shadow(8.dp)
            .testTag("main_app_bottom_bar"),
        containerColor = Color(0xFFFFFDF8),
        tonalElevation = 6.dp
    ) {
        AppDestination.values().forEach { destination ->
            val isSelected = currentDestination == destination

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.titleEnglish,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = destination.titleLukenye,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2C1810),
                    selectedTextColor = Color(0xFF2C1810),
                    indicatorColor = Color(0xFFEFE8D8),
                    unselectedIconColor = Color(0xFF8D6E63),
                    unselectedTextColor = Color(0xFF8D6E63)
                ),
                modifier = Modifier.testTag(destination.tag)
            )
        }
    }
}
