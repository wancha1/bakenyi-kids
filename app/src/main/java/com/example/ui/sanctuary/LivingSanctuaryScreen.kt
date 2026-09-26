package com.example.ui.sanctuary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AuthenticAudioManager
import com.example.language.context.AppLanguageContext
import com.example.language.context.LanguageMode
import com.example.language.enforcer.VerificationStateEnforcer
import com.example.language.model.LanguageContent
import com.example.language.model.RenderableLanguageContent
import com.example.ui.BakenyeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

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

    val books = remember { getLaunchStorybooks() }
    val spreads = remember { getAllStorybookSpreads() }

    // Map Room activeBiome to page index
    val initialPageIndex = remember(sanctuaryStateEntity.activeBiome) {
        val index = spreads.indexOfFirst { it.biome.name == sanctuaryStateEntity.activeBiome }
        if (index >= 0) index else 0
    }

    // Page turn state with physical page turn rustle sound effect
    val pageTurnState = rememberPhysicalPageTurnState(
        pageCount = spreads.size,
        initialPage = initialPageIndex,
        onPageTurnSound = { audioManager.playPageTurnSound() }
    )

    // Save spread to Room DB on page change
    LaunchedEffect(pageTurnState.currentPage) {
        if (pageTurnState.currentPage in spreads.indices) {
            val spread = spreads[pageTurnState.currentPage]
            viewModel.updateSanctuaryBiome(spread.biome)
        }
    }

    var showParentOverlay by remember { mutableStateOf(false) }
    var showBookSelector by remember { mutableStateOf(false) }
    var showDiscoveredWords by remember { mutableStateOf(false) }
    var showMusicalPlaypad by remember { mutableStateOf(false) }
    var discoveredObjectIds by remember { mutableStateOf(setOf<String>()) }

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
            .background(Color(0xFFEDE6D6)) // Warm linen picture-book desk canvas
            .testTag("living_sanctuary_screen")
    ) {
        // Physical Picture-Book Hardcover Frame with 2.5D Stacked Page Edges
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(26.dp))
                .clip(RoundedCornerShape(26.dp))
                .border(3.5.dp, Color(0xFFC4A482), RoundedCornerShape(26.dp))
                .background(Color(0xFFFFFDF8))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                val currentSpread = spreads.getOrElse(pageTurnState.currentPage) { spreads[0] }

                // 1. Storybook Header with Bookmark Ribbon, Word Journal, Instruments & Discreet Parent Clasp
                StorybookHeader(
                    currentSpread = currentSpread,
                    totalBooks = books.size,
                    totalPages = spreads.size,
                    discoveredCount = discoveredObjectIds.size,
                    onBookRibbonClicked = { showBookSelector = true },
                    onDiscoveredWordsClicked = { showDiscoveredWords = true },
                    onMusicPlaypadClicked = { showMusicalPlaypad = true },
                    onParentGateUnlocked = { showParentOverlay = true }
                )

                // 2. Full-Screen Gentle Physical Paper Page-Turn Container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    PhysicalPaperPageTurnContainer(
                        state = pageTurnState,
                        modifier = Modifier.fillMaxSize()
                    ) { pageIndex ->
                        val spread = spreads[pageIndex]
                        StorybookSpreadView(
                            spread = spread,
                            currentLanguageMode = currentLanguageMode,
                            audioManager = audioManager,
                            onObjectTapped = { obj, renderable ->
                                discoveredObjectIds = discoveredObjectIds + obj.id
                                activeObject = obj
                                activeRenderableContent = renderable
                            }
                        )
                    }
                }

                // 3. Storybook Footer with Page Controls & Spread Indicator
                StorybookFooter(
                    currentPage = pageTurnState.currentPage,
                    totalPages = spreads.size,
                    onPrevClicked = {
                        scope.launch {
                            pageTurnState.turnToPrevious()
                        }
                    },
                    onNextClicked = {
                        scope.launch {
                            pageTurnState.turnToNext()
                        }
                    }
                )
            }
        }

        // 4. Storybook Tactile Reading Card (Audio-first interactive pronunciation)
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
                        .padding(bottom = 56.dp, start = 14.dp, end = 14.dp)
                )
            }
        }

        // 5. Calm Book Shelf / Bookmark Selector Modal
        if (showBookSelector) {
            BookSelectorModal(
                books = books,
                currentPageIndex = pageTurnState.currentPage,
                onBookSelected = { selectedBook ->
                    showBookSelector = false
                    val targetPage = selectedBook.spreads.firstOrNull()?.pageIndex ?: 0
                    scope.launch {
                        pageTurnState.snapToPage(targetPage)
                    }
                },
                onDismiss = { showBookSelector = false }
            )
        }

        // 6. Word Discovery Journal Modal
        if (showDiscoveredWords) {
            DiscoveredWordsModal(
                allSpreads = spreads,
                discoveredObjectIds = discoveredObjectIds,
                audioManager = audioManager,
                onDismiss = { showDiscoveredWords = false }
            )
        }

        // 7. Interactive Musical Playpad Modal (Engoma & Kalimba)
        if (showMusicalPlaypad) {
            MusicalPlaypadModal(
                audioManager = audioManager,
                onDismiss = { showMusicalPlaypad = false }
            )
        }

        // 8. Parent Gate Overlay Modal
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
 * Storybook Header with Book Title, Bookmark Ribbon, Discovered Words counter,
 * Musical Playpad button, and Discreet Parent Clasp.
 */
@Composable
private fun StorybookHeader(
    currentSpread: StorybookSpread,
    totalBooks: Int,
    totalPages: Int,
    discoveredCount: Int,
    onBookRibbonClicked: () -> Unit,
    onDiscoveredWordsClicked: () -> Unit,
    onMusicPlaypadClicked: () -> Unit,
    onParentGateUnlocked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFE8D8))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Book Selector Ribbon
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onBookRibbonClicked() }
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentSpread.bookIconEmoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Column {
                Text(
                    text = "${currentSpread.bookTitleLukenye} • ${currentSpread.bookTitleEnglish}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Text(
                    text = "${currentSpread.titleLukenye} (Page ${currentSpread.pageNumberInBook}/${currentSpread.totalPagesInBook})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D4C41)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Words Discovery Journal Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD2B48C).copy(alpha = 0.65f))
                    .clickable { onDiscoveredWordsClicked() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("words_journal_header_button")
            ) {
                Text(
                    text = "⭐ $discoveredCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            }

            // Musical Instruments Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD2B48C).copy(alpha = 0.65f))
                    .clickable { onMusicPlaypadClicked() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("instruments_header_button")
            ) {
                Text(
                    text = "🎵",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Discreet Parent Clasp Lock
            EmbersGateButton(
                onGateUnlocked = onParentGateUnlocked
            )
        }
    }
}

/**
 * Full-Screen Illustrated Storybook Spread Page with:
 * - 2.5D Real-time hand-painted lighting
 * - "Soma Nange" Narrative reading bar
 * - Interactive objects with organic breathing, tilt and sound effects
 * - Tap ripple and sparkle particle animations
 * - Interactive Kato the Otter companion
 */
@Composable
private fun StorybookSpreadView(
    spread: StorybookSpread,
    currentLanguageMode: LanguageMode,
    audioManager: AuthenticAudioManager,
    onObjectTapped: (StorybookObject, RenderableLanguageContent) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Storybook25DObjectBreathing")

    // Slow, soothing breathing scale (1.0f -> 1.03f over 3 seconds)
    val breathingPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ),
        label = "BreathingCycle"
    )

    // Animated tap ripple wave state
    var tapRipplePos by remember { mutableStateOf<Offset?>(null) }
    val rippleAnim = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Hand-Painted Illustrated Scene Backdrop with 2.5D Real-Time Lighting
        SanctuaryCanvas(
            themeStyle = spread.themeStyle,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Animated Tap Ripple Wave on Canvas
        tapRipplePos?.let { pos ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = rippleAnim.value * 90.dp.toPx()
                val alpha = (1f - rippleAnim.value).coerceIn(0f, 1f)
                drawCircle(
                    color = Color(0xFFFFD54F).copy(alpha = alpha * 0.7f),
                    radius = radius,
                    center = pos,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFFFF9C4).copy(alpha = alpha * 0.4f),
                    radius = radius * 0.55f,
                    center = pos,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // 3. Narrative Story Reading Bar ("Soma Nange" / Read With Me)
        StorybookNarrativeBar(
            spread = spread,
            audioManager = audioManager,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // 4. Interactive 2.5D Objects positioned with depth tilt & dynamic shadow
        spread.interactiveObjects.forEachIndexed { index, obj ->
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

            val objectPhase = breathingPhase + (index * 0.9f)
            val idleSwayY = sin(objectPhase) * 3f
            val idleTiltX = cos(objectPhase) * 2f
            val idleTiltY = sin(objectPhase) * 2.5f

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = (obj.xPercent * 290).dp,
                            y = (obj.yPercent * 390).dp + idleSwayY.dp + 32.dp
                        )
                        .graphicsLayer {
                            // 2.5D Dynamic Tilt & Press feedback
                            rotationX = if (isPressed) 0f else idleTiltX
                            rotationY = if (isPressed) 0f else idleTiltY
                            scaleX = if (isPressed) 0.96f else 1.0f + (sin(objectPhase) * 0.02f)
                            scaleY = if (isPressed) 0.96f else 1.0f + (sin(objectPhase) * 0.02f)
                            cameraDistance = 16f * 2.5f
                        }
                        .shadow(
                            elevation = if (isPressed) 2.dp else 6.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = Color(0x443E2723),
                            spotColor = Color(0x663E2723)
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFFFDF5).copy(alpha = 0.96f))
                        .border(2.dp, Color(0xFF8D6E63), RoundedCornerShape(18.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            // Trigger sound effect and spoken audio
                            audioManager.playObjectTapSoundAndSpeech(
                                objectEmoji = obj.illustrationEmoji,
                                lukenyeWord = renderable.primaryText,
                                englishWord = obj.labelEnglish,
                                pronunciation = obj.pronunciation,
                                noteSeed = index
                            )

                            // Trigger expanding ripple wave
                            coroutineScope.launch {
                                tapRipplePos = Offset(obj.xPercent * 600f + 100f, obj.yPercent * 800f + 150f)
                                rippleAnim.snapTo(0f)
                                rippleAnim.animateTo(1f, tween(450, easing = LinearEasing))
                                tapRipplePos = null
                            }

                            onObjectTapped(obj, renderable)
                        }
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                        .testTag("storybook_object_${obj.id}")
                ) {
                    Text(
                        text = obj.illustrationEmoji,
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = renderable.primaryText,
                        fontSize = 12.sp,
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

        // 5. Interactive Kato the Otter Companion Mascot in corner
        KatoCompanionOverlay(
            audioManager = audioManager,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 12.dp)
        )
    }
}

/**
 * Storybook Footer with Page Controls & Spread Indicator.
 */
@Composable
private fun StorybookFooter(
    currentPage: Int,
    totalPages: Int,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFE8D8))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Previous Page Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (currentPage > 0) Color(0xFFD2B48C) else Color(0xFFE0D8C8))
                .clickable(enabled = currentPage > 0) { onPrevClicked() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("prev_page_button")
        ) {
            Text(
                text = "◄ Previous",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentPage > 0) Color(0xFF3E2723) else Color(0xFF9E9E9E)
            )
        }

        // Page Indicator
        Text(
            text = "📖 Spread ${currentPage + 1} of $totalPages",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037)
        )

        // Next Page Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (currentPage < totalPages - 1) Color(0xFFD2B48C) else Color(0xFFE0D8C8))
                .clickable(enabled = currentPage < totalPages - 1) { onNextClicked() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("next_page_button")
        ) {
            Text(
                text = "Next ►",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (currentPage < totalPages - 1) Color(0xFF3E2723) else Color(0xFF9E9E9E)
            )
        }
    }
}

/**
 * Storybook Tactile Reading Card for Early Readers when an Object is tapped.
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
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(22.dp))
            .testTag("storybook_reading_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
        border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFF8D6E63))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = storybookObject.illustrationEmoji,
                fontSize = 38.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = renderable.primaryText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Text(
                    text = "Pronunciation: \"${storybookObject.pronunciation}\"",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF8D6E63)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "English: ${storybookObject.labelEnglish}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4E342E)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Replay Audio Button with Kalimba & Speech
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFE8D8))
                        .clickable {
                            audioManager.playObjectTapSoundAndSpeech(
                                objectEmoji = storybookObject.illustrationEmoji,
                                lukenyeWord = renderable.primaryText,
                                englishWord = storybookObject.labelEnglish,
                                pronunciation = storybookObject.pronunciation
                            )
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔊", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Dismiss Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD7CCC8))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✕", fontSize = 14.sp, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Calm Book Shelf / Bookmark Selector Modal for picking any of the 18 Launch Books.
 */
@Composable
private fun BookSelectorModal(
    books: List<Storybook>,
    currentPageIndex: Int,
    onBookSelected: (Storybook) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF8F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📚 18 Picture Books (36 Spreads)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(380.dp)
                ) {
                    items(books) { book ->
                        val isCurrent = book.spreads.any { it.pageIndex == currentPageIndex }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isCurrent) Color(0xFFD2B48C) else Color(0xFFEFE8D8))
                                .border(
                                    1.dp,
                                    if (isCurrent) Color(0xFF8D6E63) else Color(0xFFD7CCC8),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onBookSelected(book) }
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = book.iconEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = book.titleLukenye,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3E2723)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = book.titleEnglish,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6D4C41)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${book.spreads.size} Spreads",
                                    fontSize = 10.sp,
                                    color = Color(0xFF8D6E63),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
