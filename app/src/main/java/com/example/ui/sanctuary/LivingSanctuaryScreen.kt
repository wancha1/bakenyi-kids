package com.example.ui.sanctuary

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.language.enforcer.VerificationStateEnforcer
import com.example.language.model.LanguageContent
import com.example.language.model.RenderableLanguageContent
import com.example.ui.BakenyeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LivingSanctuaryScreen(
    viewModel: BakenyeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val audioManager = remember { AuthenticAudioManager.getInstance(context) }
    val languageContext = remember { AppLanguageContext(context) }
    val currentLanguageMode by languageContext.currentLanguageMode.collectAsState()

    // Persistent state in Room
    val sanctuaryStateEntity by viewModel.sanctuaryState.collectAsState()

    val spreads = remember { getStorybookSpreads() }

    // Map Room activeBiome to page index
    val initialPageIndex = remember(sanctuaryStateEntity.activeBiome) {
        val index = spreads.indexOfFirst { it.biome.name == sanctuaryStateEntity.activeBiome }
        if (index >= 0) index else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialPageIndex,
        pageCount = { spreads.size }
    )

    // Save spread to Room DB on page change
    LaunchedEffect(pagerState.currentPage) {
        val spread = spreads[pagerState.currentPage]
        viewModel.updateSanctuaryBiome(spread.biome)
    }

    var showParentOverlay by remember { mutableStateOf(false) }
    var activeObject by remember { mutableStateOf<StorybookObject?>(null) }
    var activeRenderableContent by remember { mutableStateOf<RenderableLanguageContent?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            audioManager.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6EE)) // Warm linen parchment background
            .testTag("living_sanctuary_screen")
    ) {
        // Picture-Book Frame Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(3.dp, Color(0xFFD2B48C), RoundedCornerShape(24.dp))
                .background(Color(0xFFFFFDF5))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Storybook Header
                StorybookHeader(
                    currentSpread = spreads[pagerState.currentPage],
                    totalPages = spreads.size,
                    onParentGateUnlocked = { showParentOverlay = true }
                )

                // 2. Full-Screen Horizontal Pager for Storybook Pages
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { pageIndex ->
                    val spread = spreads[pageIndex]
                    StorybookSpreadView(
                        spread = spread,
                        currentLanguageMode = currentLanguageMode,
                        audioManager = audioManager,
                        onObjectTapped = { obj, renderable ->
                            activeObject = obj
                            activeRenderableContent = renderable
                        }
                    )
                }

                // 3. Storybook Footer with Calming Page Swiping & Dots
                StorybookFooter(
                    pagerState = pagerState,
                    totalPages = spreads.size,
                    onPrevClicked = {
                        scope.launch {
                            if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                    onNextClicked = {
                        scope.launch {
                            if (pagerState.currentPage < spreads.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                )
            }
        }

        // 4. Storybook Object Reading Card (Audio-first interaction display)
        activeRenderableContent?.let { renderable ->
            activeObject?.let { obj ->
                StorybookReadingCard(
                    storybookObject = obj,
                    renderable = renderable,
                    audioManager = audioManager,
                    onDismiss = {
                        activeRenderableContent = null
                        activeObject = null
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp, start = 16.dp, end = 16.dp)
                )
            }
        }

        // 5. Parent Gate Overlay Modal
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
 * Storybook Header with Title, Page Badge, and Discreet Parent Clasp.
 */
@Composable
private fun StorybookHeader(
    currentSpread: StorybookSpread,
    totalPages: Int,
    onParentGateUnlocked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFE8D8))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currentSpread.titleLukenye,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723)
            )
            Text(
                text = "${currentSpread.titleEnglish} • ${currentSpread.conceptDescription}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D4C41)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Page Indicator Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD2B48C).copy(alpha = 0.50f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Page ${currentSpread.pageIndex + 1} of $totalPages 📖",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Discreet Parent Clasp Lock
            EmbersGateButton(
                onGateUnlocked = onParentGateUnlocked
            )
        }
    }
}

/**
 * Full-Screen Illustrated Storybook Spread Page.
 */
@Composable
private fun StorybookSpreadView(
    spread: StorybookSpread,
    currentLanguageMode: LanguageMode,
    audioManager: AuthenticAudioManager,
    onObjectTapped: (StorybookObject, RenderableLanguageContent) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StorybookObjectBreathing")

    // Gentle breathing scale animation for interactive targets (1.0f -> 1.04f over 2.5 seconds)
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ObjectBreathing"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Hand-Painted Illustrated Scene Backdrop
        SanctuaryCanvas(
            themeStyle = spread.themeStyle,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Interactive Objects positioned on canvas layout
        spread.interactiveObjects.forEach { obj ->
            val rawContent = remember(obj) {
                LanguageContent(
                    contentId = obj.id,
                    englishText = obj.labelEnglish,
                    lukenyeText = obj.labelLukenye,
                    englishMeaning = obj.labelEnglish,
                    lukenyeAudioAsset = obj.audioAssetPath,
                    pronunciation = obj.pronunciation,
                    verificationStatus = obj.verificationStatus
                )
            }

            val renderable = remember(rawContent, currentLanguageMode) {
                VerificationStateEnforcer.resolveRenderableContent(
                    content = rawContent,
                    preferLukenye = currentLanguageMode != LanguageMode.ENGLISH_PRIMARY,
                    isDevMode = false
                )
            }

            // Interactive Object Touch Target (Large touch target >= 72dp x 72dp)
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = (obj.xPercent * 300).dp,
                            y = (obj.yPercent * 400).dp
                        )
                        .scale(breathingScale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFFDF5).copy(alpha = 0.94f))
                        .border(2.dp, Color(0xFF8D6E63), RoundedCornerShape(20.dp))
                        .clickable {
                            if (renderable.hasAudio && renderable.audioAssetPath != null) {
                                audioManager.playPronunciation(renderable.audioAssetPath) {}
                            }
                            onObjectTapped(obj, renderable)
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("storybook_object_${obj.id}")
                ) {
                    Text(
                        text = obj.illustrationEmoji,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = renderable.primaryText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = obj.labelEnglish,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6D4C41),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Storybook Footer with Page Swiping Controls & Dots.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StorybookFooter(
    pagerState: PagerState,
    totalPages: Int,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFE8D8))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Previous Page Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (pagerState.currentPage > 0) Color(0xFFD2B48C) else Color(0xFFE0D8C8))
                .clickable(enabled = pagerState.currentPage > 0) { onPrevClicked() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("prev_page_button")
        ) {
            Text(
                text = "◄ Previous Page",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (pagerState.currentPage > 0) Color(0xFF3E2723) else Color(0xFF9E9E9E)
            )
        }

        // Page Leaf Dot Indicators
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (i in 0 until totalPages) {
                Box(
                    modifier = Modifier
                        .size(if (i == pagerState.currentPage) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (i == pagerState.currentPage) Color(0xFF8D6E63) else Color(0xFFC8BCA8))
                )
            }
        }

        // Next Page Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (pagerState.currentPage < totalPages - 1) Color(0xFFD2B48C) else Color(0xFFE0D8C8))
                .clickable(enabled = pagerState.currentPage < totalPages - 1) { onNextClicked() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("next_page_button")
        ) {
            Text(
                text = "Next Page ►",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (pagerState.currentPage < totalPages - 1) Color(0xFF3E2723) else Color(0xFF9E9E9E)
            )
        }
    }
}

/**
 * Storybook Reading Card for Early Readers when an Object is tapped.
 */
@Composable
private fun StorybookReadingCard(
    storybookObject: StorybookObject,
    renderable: RenderableLanguageContent,
    audioManager: AuthenticAudioManager,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("storybook_reading_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF8D6E63)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = storybookObject.illustrationEmoji,
                fontSize = 44.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = renderable.primaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Text(
                    text = "Pronunciation: \"${storybookObject.pronunciation}\"",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF8D6E63)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "English: ${storybookObject.labelEnglish}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4E342E)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Replay Audio Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFE8D8))
                        .clickable {
                            if (renderable.hasAudio && renderable.audioAssetPath != null) {
                                audioManager.playPronunciation(renderable.audioAssetPath) {}
                            }
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔊", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Dismiss Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD7CCC8))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✕", fontSize = 16.sp, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
