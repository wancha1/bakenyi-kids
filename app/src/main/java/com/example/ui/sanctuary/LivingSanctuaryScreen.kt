package com.example.ui.sanctuary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LivingSanctuaryScreen(
    viewModel: BakenyeViewModel,
    modifier: Modifier = Modifier,
    activePageIndex: Int = -1,
    onNavigateToLibrary: () -> Unit = {},
    onPageChanged: (Int) -> Unit = {}
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
    val initialPageIndex = remember(sanctuaryStateEntity.activeBiome, activePageIndex) {
        if (activePageIndex in spreads.indices) {
            activePageIndex
        } else {
            val index = spreads.indexOfFirst { it.biome.name == sanctuaryStateEntity.activeBiome }
            if (index >= 0) index else 0
        }
    }

    // Page turn state with physical page turn rustle sound effect
    val pageTurnState = rememberPhysicalPageTurnState(
        pageCount = spreads.size,
        initialPage = initialPageIndex,
        onPageTurnSound = { audioManager.playPageTurnSound() }
    )

    // Sync external activePageIndex if changed
    LaunchedEffect(activePageIndex) {
        if (activePageIndex in spreads.indices && pageTurnState.currentPage != activePageIndex) {
            pageTurnState.snapToPage(activePageIndex)
        }
    }

    // Save spread to Room DB on page change
    LaunchedEffect(pageTurnState.currentPage) {
        if (pageTurnState.currentPage in spreads.indices) {
            val spread = spreads[pageTurnState.currentPage]
            viewModel.updateSanctuaryBiome(spread.biome)
            onPageChanged(pageTurnState.currentPage)
        }
    }

    var showParentOverlay by remember { mutableStateOf(false) }
    var showBookSelector by remember { mutableStateOf(false) }
    var showDiscoveredWords by remember { mutableStateOf(false) }
    var showMusicalPlaypad by remember { mutableStateOf(false) }
    var showPageContentModal by remember { mutableStateOf(false) }
    var discoveredObjectIds by remember { mutableStateOf(setOf<String>()) }

    var activeObject by remember { mutableStateOf<StorybookObject?>(null) }
    var activeRenderableContent by remember { mutableStateOf<RenderableLanguageContent?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            audioManager.release()
        }
    }

    val currentSpread = spreads.getOrElse(pageTurnState.currentPage) { spreads[0] }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEDE4D3)) // Warm textured picture-book linen desk
            .testTag("living_sanctuary_screen")
    ) {
        // Picture-Book Frame with Artisanal Leather & Gold Binding Seam
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .shadow(elevation = 14.dp, shape = RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFFFFDF8))
                .border(
                    3.dp,
                    Brush.verticalGradient(
                        listOf(Color(0xFFD4AF37), Color(0xFFC4A482), Color(0xFF8D6E63))
                    ),
                    RoundedCornerShape(22.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Redesigned Storybook Header Ribbon
                StorybookHeader(
                    currentSpread = currentSpread,
                    totalBooks = books.size,
                    currentPage = pageTurnState.currentPage,
                    totalPages = spreads.size,
                    discoveredCount = discoveredObjectIds.size,
                    onBookRibbonClicked = { showBookSelector = true },
                    onDiscoveredWordsClicked = { showDiscoveredWords = true },
                    onMusicPlaypadClicked = { showMusicalPlaypad = true },
                    onPageContentClicked = { showPageContentModal = true },
                    onParentGateUnlocked = { showParentOverlay = true }
                )

                // 2. Full-Screen Paper Page-Turn Container with Dynamic Adaptive Geometry
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

                // 3. Grounded Storybook Reading Deck (Narration & Built-In Page Navigation)
                StorybookReadingDeck(
                    spread = currentSpread,
                    audioManager = audioManager,
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
                    },
                    onViewAllPageContent = { showPageContentModal = true }
                )
            }
        }

        // 4. Tactile Reading Card Modal for Selected Vocabulary Object
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
                        .padding(bottom = 80.dp, start = 12.dp, end = 12.dp)
                )
            }
        }

        // 5. Illustrated Book Shelf Library Modal
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

        // 9. Full Page Content Detail Modal
        if (showPageContentModal) {
            PageContentDetailModal(
                spread = currentSpread,
                audioManager = audioManager,
                onDismiss = { showPageContentModal = false }
            )
        }
    }
}

/**
 * Redesigned Storybook Header Ribbon with Luxury Leather Trim,
 * Book Title Pill, Page Indicator, and Icon Action Cluster.
 */
@Composable
private fun StorybookHeader(
    currentSpread: StorybookSpread,
    totalBooks: Int,
    currentPage: Int,
    totalPages: Int,
    discoveredCount: Int,
    onBookRibbonClicked: () -> Unit,
    onDiscoveredWordsClicked: () -> Unit,
    onMusicPlaypadClicked: () -> Unit,
    onPageContentClicked: () -> Unit,
    onParentGateUnlocked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFFEFE6D6))
            .border(
                width = 1.dp,
                color = Color(0xFFDDD2BC)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Book Library Ribbon Button
        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFFFFDF8))
                .border(1.5.dp, Color(0xFFC4A482), RoundedCornerShape(14.dp))
                .clickable { onBookRibbonClicked() }
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .testTag("book_ribbon_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentSpread.bookIconEmoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentSpread.bookTitleLukenye,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C1810),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = " ▾",
                        fontSize = 11.sp,
                        color = Color(0xFF8D6E63)
                    )
                }
                Text(
                    text = "${currentSpread.bookTitleEnglish} • Page ${currentSpread.pageNumberInBook}/${currentSpread.totalPagesInBook}",
                    fontSize = 9.sp,
                    color = Color(0xFF6D4C41),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Center Page Indicator Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE3D6C2))
                .border(1.dp, Color(0xFFC4A482), RoundedCornerShape(12.dp))
                .padding(horizontal = 7.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${currentPage + 1}/$totalPages",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3E2723)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Right Action Cluster
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Full Page Content Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFDF8))
                    .border(1.dp, Color(0xFFC4A482), RoundedCornerShape(12.dp))
                    .clickable { onPageContentClicked() }
                    .padding(horizontal = 7.dp, vertical = 5.dp)
                    .testTag("page_content_header_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📄 Content",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            }

            // Word Discovery Journal Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFDF8))
                    .border(1.dp, Color(0xFFC4A482), RoundedCornerShape(12.dp))
                    .clickable { onDiscoveredWordsClicked() }
                    .padding(horizontal = 7.dp, vertical = 5.dp)
                    .testTag("words_journal_header_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⭐ $discoveredCount",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B6B00)
                )
            }

            // Instruments Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFFDF8))
                    .border(1.dp, Color(0xFFC4A482), CircleShape)
                    .clickable { onMusicPlaypadClicked() }
                    .testTag("instruments_header_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🪘", fontSize = 14.sp)
            }

            // Discreet Parent Clasp Gate
            EmbersGateButton(
                onGateUnlocked = onParentGateUnlocked
            )
        }
    }
}

/**
 * Adaptive Illustrated Storybook Spread Canvas with:
 * - Dynamic BoxWithConstraints coordinate distribution.
 * - Embossed picture-book vignette cards.
 * - Tap ripple audio wave animation.
 * - Grounded margin Kato mascot.
 */
@Composable
private fun StorybookSpreadView(
    spread: StorybookSpread,
    currentLanguageMode: LanguageMode,
    audioManager: AuthenticAudioManager,
    onObjectTapped: (StorybookObject, RenderableLanguageContent) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = maxWidth
        val canvasHeight = maxHeight

        // 1. Hand-Painted Illustrated Scene Backdrop (Static & Crisp)
        SanctuaryCanvas(
            themeStyle = spread.themeStyle,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Adaptively Positioned Storybook Illustrated Objects
        val usableWidth = canvasWidth - 110.dp
        val usableHeight = canvasHeight - 110.dp

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

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val xPos = (usableWidth * obj.xPercent.coerceIn(0.04f, 0.94f)) + 12.dp
            val yPos = (usableHeight * obj.yPercent.coerceIn(0.04f, 0.94f)) + 8.dp

            Box(
                modifier = Modifier
                    .offset(x = xPos, y = yPos)
                    .graphicsLayer {
                        scaleX = if (isPressed) 0.95f else 1.0f
                        scaleY = if (isPressed) 0.95f else 1.0f
                    }
                    .shadow(
                        elevation = if (isPressed) 2.dp else 5.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x333E2723),
                        spotColor = Color(0x553E2723)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFFDF7))
                    .border(
                        1.5.dp,
                        Brush.verticalGradient(
                            listOf(Color(0xFFD4AF37), Color(0xFFC4A482))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        audioManager.playObjectTapSoundAndSpeech(
                            objectEmoji = obj.illustrationEmoji,
                            lukenyeWord = renderable.primaryText,
                            englishWord = obj.labelEnglish,
                            pronunciation = obj.pronunciation,
                            noteSeed = index
                        )

                        onObjectTapped(obj, renderable)
                    }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .testTag("storybook_object_${obj.id}")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = obj.illustrationEmoji,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = renderable.primaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C1810),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = obj.labelEnglish,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6D4C41),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 4. Interactive Kato the Otter Mascot anchored cleanly in the margin
        val katoPagePrompt = remember(spread) {
            if (spread.katoPromptLukenye.isNotEmpty()) {
                spread.katoPromptLukenye to spread.katoPromptEnglish
            } else null
        }
        KatoCompanionOverlay(
            audioManager = audioManager,
            pagePrompt = katoPagePrompt,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 12.dp)
        )
    }
}

/**
 * Storybook Tactile Reading Card Modal for Selected Vocabulary Object.
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
            .shadow(elevation = 14.dp, shape = RoundedCornerShape(22.dp))
            .testTag("storybook_reading_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFC4A482))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = storybookObject.illustrationEmoji,
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = renderable.primaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C1810)
                )
                Text(
                    text = "Pronounce: \"${storybookObject.pronunciation}\"",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8D6E63)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "English: ${storybookObject.labelEnglish}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF5D4037)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Replay Audio Button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFE8D8))
                        .border(1.5.dp, Color(0xFFC4A482), CircleShape)
                        .clickable {
                            audioManager.playObjectTapSoundAndSpeech(
                                objectEmoji = storybookObject.illustrationEmoji,
                                lukenyeWord = renderable.primaryText,
                                englishWord = storybookObject.labelEnglish,
                                pronunciation = storybookObject.pronunciation
                            )
                        },
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
                        .background(Color(0xFFE0D7C6))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        color = Color(0xFF3E2723),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Redesigned Visual Book Shelf Library Modal with Categorized Shelves.
 */
@Composable
private fun BookSelectorModal(
    books: List<Storybook>,
    currentPageIndex: Int,
    onBookSelected: (Storybook) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("All (18)", "🎣 Fishing", "🏡 Home", "🌿 Nature", "👑 Wisdom")

    val filteredBooks = remember(selectedCategoryIndex, books) {
        when (selectedCategoryIndex) {
            1 -> books.filter { b ->
                val style = b.spreads.firstOrNull()?.themeStyle
                style == StorybookThemeStyle.FISHERMAN_SONG || style == StorybookThemeStyle.RIVERBANK_DAWN
            }
            2 -> books.filter { b ->
                b.spreads.firstOrNull()?.themeStyle == StorybookThemeStyle.VILLAGE_HEARTH
            }
            3 -> books.filter { b ->
                b.spreads.firstOrNull()?.themeStyle == StorybookThemeStyle.WETLAND_MARSH
            }
            4 -> books.takeLast(4)
            else -> books
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F0)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFC4A482))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📚", fontSize = 22.sp, modifier = Modifier.padding(end = 6.dp))
                        Column {
                            Text(
                                text = "Akatabo k'Engero (Library)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C1810)
                            )
                            Text(
                                text = "18 Picture Books • 36 Spreads",
                                fontSize = 11.sp,
                                color = Color(0xFF8D6E63)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF5D4037))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Pills
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = {}
                ) {
                    categories.forEachIndexed { idx, title ->
                        val isSelected = selectedCategoryIndex == idx
                        Tab(
                            selected = isSelected,
                            onClick = { selectedCategoryIndex = idx },
                            text = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFFD2B48C) else Color(0xFFEFE8D8))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF8D6E63) else Color(0xFFDDD2BC),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = Color(0xFF2C1810)
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Book Shelf Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(390.dp)
                ) {
                    items(filteredBooks) { book ->
                        val isCurrent = book.spreads.any { it.pageIndex == currentPageIndex }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isCurrent) Color(0xFFFFF2D6) else Color(0xFFFFFDF8))
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) Color(0xFFB8860B) else Color(0xFFDDD2BC),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onBookSelected(book) }
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = book.iconEmoji, fontSize = 26.sp)
                                    if (isCurrent) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFB8860B))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "READING",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = book.titleLukenye,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C1810),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = book.titleEnglish,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6D4C41),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "📖 ${book.spreads.size} Spreads",
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
