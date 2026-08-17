package com.example.ui.sanctuary

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

@Composable
fun SanctuaryCanvas(
    themeStyle: StorybookThemeStyle,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StorybookCalmAnimation")

    // Subtle, calming water shimmer movement (4.5-second duration)
    val waterShimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaterShimmer"
    )

    // Gentle leaf sway / breeze movement (3.5-second reverse loop)
    val leafSway by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LeafSway"
    )

    // Subtle drifting smoke / cloud movement
    val driftOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DriftOffset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (width <= 0f || height <= 0f) return@Canvas

        try {
            when (themeStyle) {
                StorybookThemeStyle.RIVERBANK_DAWN -> {
                    drawRiverbankDawn(width, height, waterShimmerPhase, leafSway)
                }
                StorybookThemeStyle.VILLAGE_HEARTH -> {
                    drawVillageHearth(width, height, driftOffset)
                }
                StorybookThemeStyle.WETLAND_MARSH -> {
                    drawWetlandMarsh(width, height, waterShimmerPhase, leafSway)
                }
                StorybookThemeStyle.BAOBAB_CANOPY -> {
                    drawBaobabCanopy(width, height, leafSway)
                }
                StorybookThemeStyle.LAKESIDE_COUNTING -> {
                    drawLakesideCounting(width, height, waterShimmerPhase)
                }
                StorybookThemeStyle.LAKESIDE_COLOURS -> {
                    drawLakesideColours(width, height, waterShimmerPhase)
                }
                StorybookThemeStyle.FISHERMAN_SONG -> {
                    drawFishermanSong(width, height, waterShimmerPhase, driftOffset)
                }
            }
        } catch (e: Exception) {
            Log.e("STORYBOOK_CANVAS", "Error rendering Storybook canvas", e)
        }
    }
}

/**
 * Spread 1: Riverbank Dawn Illustration Backdrop
 */
private fun DrawScope.drawRiverbankDawn(width: Float, height: Float, waterPhase: Float, sway: Float) {
    // 1. Soft Morning Sky
    val skyGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF3E0), Color(0xFFE0F7FA), Color(0xFFB2EBF2)),
        startY = 0f,
        endY = height * 0.55f
    )
    drawRect(brush = skyGradient, size = Size(width, height * 0.55f))

    // 2. Shoreline Grass Bank
    val bankPath = Path().apply {
        moveTo(0f, height * 0.45f)
        quadraticTo(width * 0.30f, height * 0.40f, width * 0.60f, height * 0.48f)
        quadraticTo(width * 0.85f, height * 0.52f, width, height * 0.42f)
        lineTo(width, height * 0.60f)
        lineTo(0f, height * 0.60f)
        close()
    }
    drawPath(path = bankPath, brush = Brush.verticalGradient(listOf(Color(0xFF81C784), Color(0xFF388E3C))))

    // 3. Calm River Water Surface
    val waterTop = height * 0.55f
    val waterGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1), Color(0xFF01579B)),
        startY = waterTop,
        endY = height
    )

    val waterPath = Path().apply {
        moveTo(0f, waterTop)
        val steps = 16
        for (i in 0..steps) {
            val x = (i / steps.toFloat()) * width
            val y = waterTop + (sin((i * 0.5f) + waterPhase) * 6f).toFloat()
            lineTo(x, y)
        }
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(path = waterPath, brush = waterGradient)

    // 4. Papyrus Reeds Silhouette
    for (i in 0..12) {
        val reedX = (i / 12f) * width * 0.45f + 10f
        val reedHeight = height * 0.12f + (sin(i.toDouble()) * 10f).toFloat()
        drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(reedX, waterTop),
            end = Offset(reedX + sway * 0.5f, waterTop - reedHeight),
            strokeWidth = 5f
        )
    }
}

/**
 * Spread 2: Village Hearth Illustration Backdrop
 */
private fun DrawScope.drawVillageHearth(width: Float, height: Float, drift: Float) {
    // 1. Warm Earthy Wall Background
    val wallGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5E6CA), Color(0xFFE6CFA8), Color(0xFFD4B886)),
        startY = 0f,
        endY = height
    )
    drawRect(brush = wallGradient, size = Size(width, height))

    // 2. Thatched Clay Hut Outline
    val hutCenterX = width * 0.72f
    val hutBaseY = height * 0.42f

    val roofPath = Path().apply {
        moveTo(hutCenterX - 110f, hutBaseY)
        lineTo(hutCenterX, hutBaseY - 90f)
        lineTo(hutCenterX + 110f, hutBaseY)
        close()
    }
    drawPath(path = roofPath, color = Color(0xFF8D6E63))

    drawRoundRect(
        color = Color(0xFFBCAAA4),
        topLeft = Offset(hutCenterX - 85f, hutBaseY),
        size = Size(170f, 100f),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // 3. Hearth Smoke Drift
    drawCircle(
        color = Color(0x33BDBDBD),
        radius = 16f,
        center = Offset(width * 0.48f + drift * 0.3f, height * 0.55f - drift * 0.5f)
    )
    drawCircle(
        color = Color(0x229E9E9E),
        radius = 24f,
        center = Offset(width * 0.50f + drift * 0.5f, height * 0.50f - drift * 0.8f)
    )

    // 4. Woven Grass Mat Floor
    val matPath = Path().apply {
        moveTo(width * 0.10f, height * 0.70f)
        lineTo(width * 0.90f, height * 0.70f)
        lineTo(width * 0.95f, height)
        lineTo(width * 0.05f, height)
        close()
    }
    drawPath(path = matPath, color = Color(0xFFE0C397))
}

/**
 * Spread 3: Wetland Marsh Illustration Backdrop
 */
private fun DrawScope.drawWetlandMarsh(width: Float, height: Float, waterPhase: Float, sway: Float) {
    // 1. Soft Marsh Sky
    val skyGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7)),
        startY = 0f,
        endY = height * 0.50f
    )
    drawRect(brush = skyGradient, size = Size(width, height * 0.50f))

    // 2. Marsh Water Surface
    val waterTop = height * 0.48f
    val waterGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4DB6AC), Color(0xFF00897B), Color(0xFF004D40)),
        startY = waterTop,
        endY = height
    )
    drawRect(brush = waterGradient, topLeft = Offset(0f, waterTop), size = Size(width, height - waterTop))

    // 3. Floating Water Lily Pads
    val lilyColors = Color(0xFF2E7D32)
    drawCircle(color = lilyColors, radius = width * 0.08f, center = Offset(width * 0.30f, height * 0.75f))
    drawCircle(color = lilyColors, radius = width * 0.06f, center = Offset(width * 0.70f, height * 0.80f))
}

/**
 * Spread 4: Baobab Canopy Illustration Backdrop
 */
private fun DrawScope.drawBaobabCanopy(width: Float, height: Float, sway: Float) {
    // 1. Warm Forest Canopy Sky
    val skyGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFFFD54F)),
        startY = 0f,
        endY = height
    )
    drawRect(brush = skyGradient, size = Size(width, height))

    // 2. Grand Baobab Tree Trunk
    val trunkPath = Path().apply {
        moveTo(width * 0.20f, height)
        quadraticTo(width * 0.28f, height * 0.55f, width * 0.24f, height * 0.20f)
        lineTo(width * 0.42f, height * 0.20f)
        quadraticTo(width * 0.38f, height * 0.55f, width * 0.46f, height)
        close()
    }
    drawPath(path = trunkPath, brush = Brush.horizontalGradient(listOf(Color(0xFF4E342E), Color(0xFF6D4C41), Color(0xFF3E2723))))

    // 3. Leaf Canopy
    drawCircle(color = Color(0xFF33691E), radius = width * 0.22f, center = Offset(width * 0.33f + sway * 0.4f, height * 0.18f))
    drawCircle(color = Color(0xFF558B2F), radius = width * 0.18f, center = Offset(width * 0.20f + sway * 0.2f, height * 0.24f))
}

/**
 * Spread 5: Lakeside Counting Illustration Backdrop
 */
private fun DrawScope.drawLakesideCounting(width: Float, height: Float, waterPhase: Float) {
    // 1. Calm Sandy Shore Background
    val sandGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFFFD54F)),
        startY = 0f,
        endY = height * 0.60f
    )
    drawRect(brush = sandGradient, size = Size(width, height * 0.60f))

    // 2. Lake Edge
    val waterTop = height * 0.60f
    val waterGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF29B6F6), Color(0xFF0288D1), Color(0xFF01579B)),
        startY = waterTop,
        endY = height
    )
    drawRect(brush = waterGradient, topLeft = Offset(0f, waterTop), size = Size(width, height - waterTop))
}

/**
 * Spread 6: Lakeside Colours Backdrop
 */
private fun DrawScope.drawLakesideColours(width: Float, height: Float, waterPhase: Float) {
    // 1. Sunset Rainbow / Lake Sky Gradient
    val rainbowGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFCC80), // Warm Sunrise Ochre
            Color(0xFFFFAB91), // Sunset Rose
            Color(0xFFCE93D8), // Twilight Violet
            Color(0xFF81D4FA)  // Shoreline Blue
        ),
        startY = 0f,
        endY = height
    )
    drawRect(brush = rainbowGradient, size = Size(width, height))

    // Gentle sun halo
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0x88FFE082), Color(0x00FFE082)),
            center = Offset(width * 0.5f, height * 0.25f),
            radius = width * 0.35f
        ),
        radius = width * 0.35f,
        center = Offset(width * 0.5f, height * 0.25f)
    )
}

/**
 * Spread 7: Fisherman Song & Story Backdrop
 */
private fun DrawScope.drawFishermanSong(width: Float, height: Float, waterPhase: Float, drift: Float) {
    // 1. Twilight Evening Sky
    val nightGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF3949AB), Color(0xFF004D40)),
        startY = 0f,
        endY = height
    )
    drawRect(brush = nightGradient, size = Size(width, height))

    // 2. Crescent Moon Glow
    drawCircle(
        color = Color(0xFFFFEE58),
        radius = width * 0.07f,
        center = Offset(width * 0.82f, height * 0.14f)
    )
    drawCircle(
        color = Color(0xFF1A237E),
        radius = width * 0.06f,
        center = Offset(width * 0.80f, height * 0.12f)
    )

    // 3. Gentle Lake Water with Moon Reflection
    val waterTop = height * 0.58f
    val lakeGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF004D40), Color(0xFF00695C), Color(0xFF004D40)),
        startY = waterTop,
        endY = height
    )
    drawRect(brush = lakeGradient, topLeft = Offset(0f, waterTop), size = Size(width, height - waterTop))
}
