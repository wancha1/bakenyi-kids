package com.example.ui.sanctuary

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Hybrid 2.5D Storybook Canvas.
 * Hand-painted illustration aesthetics enhanced with multi-layer depth,
 * real-time procedural lighting, water caustics, swaying organic foliage,
 * and ambient atmospheric particles.
 */
@Composable
fun SanctuaryCanvas(
    themeStyle: StorybookThemeStyle,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Storybook25DCalmCycle")

    // 1. Slow, continuous time progression for atmospheric waves & sunlight (12-second cycle)
    val slowClock by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AtmosphericClock"
    )

    // 2. Water Shimmer & Wave harmonics (5.5-second cycle)
    val waterPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaterHarmonics"
    )

    // 3. Gentle wind sway for foliage and papyrus reeds (4-second reverse loop)
    val windSway by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WindSway"
    )

    // 4. Soft sunbeam / fire glow breathing pulse (3.5-second loop)
    val lightPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AmbientLightPulse"
    )

    // Stable random seed for floating dust motes / fireflies / embers
    val particleSeeds = remember {
        List(14) { index ->
            ParticleSeed(
                baseX = (index * 0.07f + 0.04f) % 0.95f,
                baseY = (index * 0.13f + 0.12f) % 0.85f,
                speed = 0.6f + (index % 5) * 0.2f,
                size = 2.5f + (index % 4) * 1.5f,
                phaseOffset = (index * 0.8f).toFloat()
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (width <= 0f || height <= 0f) return@Canvas

        try {
            // Layer 0: Hand-Painted Scenic Backdrop with 2.5D Depth Lighting
            when (themeStyle) {
                StorybookThemeStyle.RIVERBANK_DAWN -> {
                    drawRiverbankDawn25D(width, height, waterPhase, windSway, lightPulse, slowClock)
                }
                StorybookThemeStyle.VILLAGE_HEARTH -> {
                    drawVillageHearth25D(width, height, windSway, lightPulse, slowClock)
                }
                StorybookThemeStyle.WETLAND_MARSH -> {
                    drawWetlandMarsh25D(width, height, waterPhase, windSway, lightPulse)
                }
                StorybookThemeStyle.BAOBAB_CANOPY -> {
                    drawBaobabCanopy25D(width, height, windSway, lightPulse, slowClock)
                }
                StorybookThemeStyle.LAKESIDE_COUNTING -> {
                    drawLakesideCounting25D(width, height, waterPhase, windSway, lightPulse)
                }
                StorybookThemeStyle.LAKESIDE_COLOURS -> {
                    drawLakesideColours25D(width, height, waterPhase, lightPulse, slowClock)
                }
                StorybookThemeStyle.FISHERMAN_SONG -> {
                    drawFishermanSong25D(width, height, waterPhase, windSway, lightPulse, slowClock)
                }
            }

            // Layer 1: Ambient 2.5D Depth Dust Motes / Sunlight Sparkles / Evening Fireflies
            drawAtmosphericParticles(
                width = width,
                height = height,
                themeStyle = themeStyle,
                particles = particleSeeds,
                clock = slowClock,
                pulse = lightPulse
            )

            // Layer 2: Physical Picture-Book Center Spine Fold & Soft Vignette
            drawStorybookSpineFoldAndVignette(width, height)

        } catch (e: Exception) {
            Log.e("STORYBOOK_25D_CANVAS", "Error rendering 2.5D Storybook canvas", e)
        }
    }
}

private data class ParticleSeed(
    val baseX: Float,
    val baseY: Float,
    val speed: Float,
    val size: Float,
    val phaseOffset: Float
)

/**
 * 2.5D Riverbank Dawn: Layered distant misty hills, morning god rays,
 * realistic harmonic water ripples, and multi-depth swaying papyrus reeds.
 */
private fun DrawScope.drawRiverbankDawn25D(
    width: Float,
    height: Float,
    waterPhase: Float,
    windSway: Float,
    lightPulse: Float,
    clock: Float
) {
    // 1. Layer 0: Sky with Soft Warm Dawn Gradient & Volumetric Sun Rays
    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF8E7), // Warm Pearl Dawn
            Color(0xFFFFE0B2), // Golden Amber
            Color(0xFFE1F5FE), // Soft River Cyan
            Color(0xFFB3E5FC)  // Morning Lake Blue
        ),
        startY = 0f,
        endY = height * 0.58f
    )
    drawRect(brush = skyGradient, size = Size(width, height * 0.58f))

    // Volumetric Dawn Sun Rays from top-left (Lake Kyoga Sunrise)
    val sunCenter = Offset(width * 0.18f, height * 0.16f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFD54F).copy(alpha = 0.55f * lightPulse),
                Color(0xFFFFECB3).copy(alpha = 0.30f),
                Color.Transparent
            ),
            center = sunCenter,
            radius = width * 0.45f
        ),
        radius = width * 0.45f,
        center = sunCenter
    )

    // Soft morning sun disc
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFCA28)),
            center = sunCenter,
            radius = width * 0.08f
        ),
        radius = width * 0.08f,
        center = sunCenter
    )

    // 2. Layer 1: Distant Misty Ugandan Hills (Deep Parallax Layer)
    val distantHillsPath = Path().apply {
        moveTo(0f, height * 0.38f)
        cubicTo(
            width * 0.25f, height * 0.34f,
            width * 0.50f, height * 0.42f,
            width * 0.75f, height * 0.36f
        )
        cubicTo(
            width * 0.88f, height * 0.33f,
            width * 0.95f, height * 0.37f,
            width, height * 0.35f
        )
        lineTo(width, height * 0.52f)
        lineTo(0f, height * 0.52f)
        close()
    }
    drawPath(
        path = distantHillsPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFB0BEC5).copy(alpha = 0.45f), Color(0xFF90A4AE).copy(alpha = 0.65f)),
            startY = height * 0.32f,
            endY = height * 0.52f
        )
    )

    // 3. Layer 2: Midground Red-Earth Shore & Green Meadow Bank
    val bankPath = Path().apply {
        moveTo(0f, height * 0.46f)
        quadraticTo(width * 0.35f, height * 0.42f, width * 0.65f, height * 0.50f)
        quadraticTo(width * 0.85f, height * 0.54f, width, height * 0.45f)
        lineTo(width, height * 0.62f)
        lineTo(0f, height * 0.62f)
        close()
    }
    drawPath(
        path = bankPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF4CAF50), Color(0xFF388E3C), Color(0xFF795548)),
            startY = height * 0.42f,
            endY = height * 0.62f
        )
    )

    // 4. Layer 3: 2.5D Living River Water with Multi-Harmonic Wave Caustics
    val waterTop = height * 0.54f
    val waterGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF29B6F6), // Sunlit surface
            Color(0xFF0288D1), // Clear lake depth
            Color(0xFF01579B), // Deep water
            Color(0xFF003865)  // River bed
        ),
        startY = waterTop,
        endY = height
    )

    val waterPath = Path().apply {
        moveTo(0f, waterTop)
        val steps = 24
        for (i in 0..steps) {
            val progress = i / steps.toFloat()
            val x = progress * width
            // Multi-frequency sinusoidal wave for natural liquid movement
            val primaryWave = sin(progress * 4 * PI.toFloat() + waterPhase) * 5f
            val harmonicWave = sin(progress * 8 * PI.toFloat() - waterPhase * 1.5f) * 2.5f
            val y = waterTop + primaryWave + harmonicWave
            lineTo(x, y)
        }
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(path = waterPath, brush = waterGradient)

    // Specular sunlight highlights on water ripples (2.5D specular reflection)
    val specularPath = Path().apply {
        val count = 8
        for (i in 0 until count) {
            val rx = width * (0.15f + i * 0.10f) + sin(clock + i) * 12f
            val ry = waterTop + 14f + (i * 18f) + cos(waterPhase + i) * 3f
            moveTo(rx - 22f, ry)
            quadraticTo(rx, ry - 3f, rx + 22f, ry)
        }
    }
    drawPath(
        path = specularPath,
        color = Color(0xFFFFF9C4).copy(alpha = 0.55f * lightPulse),
        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
    )

    // 5. Layer 4: Foreground Swaying Papyrus Reeds (2.5D Multi-Plane Perspective)
    // Back row (darker, smaller sway)
    for (i in 0..10) {
        val reedX = (i / 10f) * width * 0.40f + 15f
        val reedHeight = height * 0.14f + sin(i * 1.4) * 14f
        val swayOffset = windSway * (8f + i * 1.2f)
        drawLine(
            color = Color(0xFF1B5E20),
            start = Offset(reedX, waterTop + 10f),
            end = Offset(reedX + swayOffset * 0.7f, (waterTop - reedHeight).toFloat()),
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )
        // Papyrus feather tuft top
        drawCircle(
            color = Color(0xFF2E7D32),
            radius = 6f,
            center = Offset(reedX + swayOffset * 0.7f, (waterTop - reedHeight).toFloat())
        )
    }

    // Front row (brighter green, responsive swaying)
    for (i in 0..8) {
        val reedX = (i / 8f) * width * 0.35f + 25f
        val reedHeight = height * 0.18f + cos(i * 1.1) * 18f
        val swayOffset = windSway * (12f + i * 1.5f)
        drawLine(
            color = Color(0xFF43A047),
            start = Offset(reedX, waterTop + 20f),
            end = Offset(reedX + swayOffset, (waterTop - reedHeight).toFloat()),
            strokeWidth = 4.5f,
            cap = StrokeCap.Round
        )
        // Detailed papyrus crown
        val crownCenter = Offset(reedX + swayOffset, (waterTop - reedHeight).toFloat())
        drawCircle(color = Color(0xFF66BB6A), radius = 8f, center = crownCenter)
    }
}

/**
 * 2.5D Village Hearth: Rich layered homestead, thatched roof textures,
 * soft hearth fire luminescence with dynamic warm shadows, and woven mat perspective.
 */
private fun DrawScope.drawVillageHearth25D(
    width: Float,
    height: Float,
    windSway: Float,
    lightPulse: Float,
    clock: Float
) {
    // 1. Layer 0: Warm Earthy Interior Wall & Daylight Soft Glow
    val wallGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFF3E0), // Warm ambient light
            Color(0xFFF5E6CA), // Sandstone clay
            Color(0xFFE0C397), // Ochre mud wall
            Color(0xFFBCAAA4)  // Outer perimeter
        ),
        center = Offset(width * 0.50f, height * 0.45f),
        radius = width * 0.75f
    )
    drawRect(brush = wallGradient, size = Size(width, height))

    // 2. Layer 1: Thatched Homestead Hut in Background with 2.5D Shadow
    val hutCenterX = width * 0.72f
    val hutBaseY = height * 0.38f

    // Soft drop shadow cast by hut onto earth
    drawRoundRect(
        color = Color(0x333E2723),
        topLeft = Offset(hutCenterX - 95f, hutBaseY + 60f),
        size = Size(190f, 40f),
        cornerRadius = CornerRadius(20f, 20f)
    )

    // Clay wall of hut
    drawRoundRect(
        brush = Brush.verticalGradient(
            listOf(Color(0xFFD7CCC8), Color(0xFFA1887F), Color(0xFF6D4C41)),
            startY = hutBaseY,
            endY = hutBaseY + 95f
        ),
        topLeft = Offset(hutCenterX - 80f, hutBaseY),
        size = Size(160f, 95f),
        cornerRadius = CornerRadius(14f, 14f)
    )

    // Thatched Straw Roof with layered depth lines
    val roofPath = Path().apply {
        moveTo(hutCenterX - 115f, hutBaseY + 5f)
        lineTo(hutCenterX, hutBaseY - 95f)
        lineTo(hutCenterX + 115f, hutBaseY + 5f)
        close()
    }
    drawPath(
        path = roofPath,
        brush = Brush.verticalGradient(
            listOf(Color(0xFF8D6E63), Color(0xFF6D4C41), Color(0xFF4E342E)),
            startY = hutBaseY - 95f,
            endY = hutBaseY + 5f
        )
    )

    // 3. Layer 2: Hearth Fire Warm Luminescence (2.5D Dynamic Lighting)
    val hearthCenter = Offset(width * 0.50f, height * 0.60f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF9800).copy(alpha = 0.50f * lightPulse),
                Color(0xFFFF5722).copy(alpha = 0.30f * lightPulse),
                Color(0xFFFFC107).copy(alpha = 0.12f),
                Color.Transparent
            ),
            center = hearthCenter,
            radius = width * 0.40f * lightPulse
        ),
        radius = width * 0.40f * lightPulse,
        center = hearthCenter
    )

    // Rising Hearth Smoke Particles (Soft translucent puffs)
    for (s in 0..4) {
        val smokeY = height * 0.52f - (s * 24f) - (clock * 10f % 40f)
        val smokeX = width * 0.50f + sin(clock + s) * 14f + windSway * 10f
        val smokeRadius = 16f + (s * 8f)
        drawCircle(
            color = Color(0xFFBDBDBD).copy(alpha = (0.28f - s * 0.05f).coerceAtLeast(0.05f)),
            radius = smokeRadius,
            center = Offset(smokeX, smokeY)
        )
    }

    // 4. Layer 3: Handwoven Grass Mat with 2.5D Linear Perspective Grid
    val matPath = Path().apply {
        moveTo(width * 0.08f, height * 0.68f)
        lineTo(width * 0.92f, height * 0.68f)
        lineTo(width * 0.98f, height)
        lineTo(width * 0.02f, height)
        close()
    }
    drawPath(
        path = matPath,
        brush = Brush.verticalGradient(
            listOf(Color(0xFFEFEBE9), Color(0xFFD7CCC8), Color(0xFFBCAAA4)),
            startY = height * 0.68f,
            endY = height
        )
    )

    // Woven mat texture weave lines
    for (m in 1..7) {
        val progress = m / 8f
        val y = height * 0.68f + progress * (height * 0.32f)
        val leftX = width * (0.08f - progress * 0.06f)
        val rightX = width * (0.92f + progress * 0.06f)
        drawLine(
            color = Color(0xFFA1887F).copy(alpha = 0.45f),
            start = Offset(leftX, y),
            end = Offset(rightX, y),
            strokeWidth = 1.8f
        )
    }
}

/**
 * 2.5D Wetland Marsh: Layered wetland greenery, floating water lily pads,
 * soft water surface with ripples, and papyrus canopy.
 */
private fun DrawScope.drawWetlandMarsh25D(
    width: Float,
    height: Float,
    waterPhase: Float,
    windSway: Float,
    lightPulse: Float
) {
    // 1. Marsh Sky
    val skyGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7)),
        startY = 0f,
        endY = height * 0.48f
    )
    drawRect(brush = skyGradient, size = Size(width, height * 0.48f))

    // 2. Wetland Water Body with 2.5D Surface
    val waterTop = height * 0.46f
    val waterGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4DB6AC), Color(0xFF00897B), Color(0xFF00695C), Color(0xFF004D40)),
        startY = waterTop,
        endY = height
    )
    drawRect(brush = waterGradient, topLeft = Offset(0f, waterTop), size = Size(width, height - waterTop))

    // 3. Floating 3D-Illuminated Water Lily Pads
    val lilyPads = listOf(
        Triple(0.24f, 0.72f, width * 0.10f),
        Triple(0.68f, 0.68f, width * 0.08f),
        Triple(0.44f, 0.82f, width * 0.12f),
        Triple(0.82f, 0.78f, width * 0.07f)
    )

    lilyPads.forEach { (xPercent, yPercent, radius) ->
        val center = Offset(
            width * xPercent + sin(waterPhase + xPercent * 10f) * 4f,
            height * yPercent + cos(waterPhase + yPercent * 10f) * 2f
        )
        // Lily Pad Drop Shadow in water
        drawOval(
            color = Color(0x5500332C),
            topLeft = Offset(center.x - radius, center.y - radius * 0.35f + 6f),
            size = Size(radius * 2, radius * 0.70f)
        )
        // Lily Pad Disc
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32), Color(0xFF1B5E20)),
                center = center,
                radius = radius
            ),
            topLeft = Offset(center.x - radius, center.y - radius * 0.35f),
            size = Size(radius * 2, radius * 0.70f)
        )
        // Water Lily Flower Bud (Soft Pink & White)
        drawCircle(
            color = Color(0xFFF8BBD0),
            radius = radius * 0.22f,
            center = Offset(center.x + radius * 0.2f, center.y - radius * 0.1f)
        )
    }

    // 4. Swaying Background Wetland Reeds
    for (i in 0..12) {
        val x = (i / 12f) * width
        val rHeight = height * 0.16f + sin(i.toDouble()) * 20f
        drawLine(
            color = Color(0xFF1B5E20).copy(alpha = 0.75f),
            start = Offset(x, waterTop),
            end = Offset(x + windSway * 10f, (waterTop - rHeight).toFloat()),
            strokeWidth = 4f
        )
    }
}

/**
 * 2.5D Baobab Canopy: Monumental trunk with woodgrain texturing,
 * volumetric canopy shade, and sun dappling rays.
 */
private fun DrawScope.drawBaobabCanopy25D(
    width: Float,
    height: Float,
    windSway: Float,
    lightPulse: Float,
    clock: Float
) {
    // 1. Warm Savanna Sky
    val skyGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFFFD54F), Color(0xFFFFB74D)),
        startY = 0f,
        endY = height
    )
    drawRect(brush = skyGradient, size = Size(width, height))

    // 2. Grand Ancient Baobab Trunk (Multi-tone textured bark)
    val trunkPath = Path().apply {
        moveTo(width * 0.18f, height)
        cubicTo(
            width * 0.24f, height * 0.65f,
            width * 0.20f, height * 0.35f,
            width * 0.15f, height * 0.12f
        )
        lineTo(width * 0.45f, height * 0.12f)
        cubicTo(
            width * 0.40f, height * 0.35f,
            width * 0.44f, height * 0.65f,
            width * 0.52f, height
        )
        close()
    }
    drawPath(
        path = trunkPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF3E2723), // Shadow edge
                Color(0xFF5D4037),
                Color(0xFF8D6E63), // Mid bark
                Color(0xFF6D4C41),
                Color(0xFF3E2723)  // Back edge
            ),
            startX = width * 0.15f,
            endX = width * 0.52f
        )
    )

    // 3. Volumetric Leaf Canopy Clusters with Organic Sway
    val canopyClusters = listOf(
        Triple(0.28f, 0.14f, width * 0.26f),
        Triple(0.16f, 0.20f, width * 0.20f),
        Triple(0.42f, 0.18f, width * 0.22f),
        Triple(0.32f, 0.26f, width * 0.18f)
    )

    canopyClusters.forEachIndexed { idx, (cx, cy, r) ->
        val sway = windSway * (6f + idx * 2.5f)
        val center = Offset(width * cx + sway, height * cy)
        // Shadow depth layer
        drawCircle(
            color = Color(0xFF1B5E20).copy(alpha = 0.55f),
            radius = r * 1.05f,
            center = Offset(center.x, center.y + 10f)
        )
        // Main lush foliage
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF689F38), Color(0xFF33691E), Color(0xFF1B5E20)),
                center = center,
                radius = r
            ),
            radius = r,
            center = center
        )
    }

    // 4. Sun Dapple Rays penetrating through canopy leaves
    val dapplePath = Path().apply {
        moveTo(width * 0.30f, height * 0.22f)
        lineTo(width * 0.05f, height * 0.85f)
        lineTo(width * 0.25f, height * 0.85f)
        close()
    }
    drawPath(
        path = dapplePath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFF9C4).copy(alpha = (0.33f * lightPulse).coerceIn(0f, 1f)), Color.Transparent),
            startY = height * 0.22f,
            endY = height * 0.85f
        )
    )
}

/**
 * 2.5D Lakeside Counting: Golden sandy beach, gently lapping wave crests,
 * smooth shore pebbles and warm sun.
 */
private fun DrawScope.drawLakesideCounting25D(
    width: Float,
    height: Float,
    waterPhase: Float,
    windSway: Float,
    lightPulse: Float
) {
    // 1. Shore Sand Background
    val sandGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFFFE082), Color(0xFFFFD54F)),
        startY = 0f,
        endY = height * 0.62f
    )
    drawRect(brush = sandGradient, size = Size(width, height * 0.62f))

    // 2. Lake Edge with 2.5D Lapping Wave Foam
    val waterTop = height * 0.58f
    val waterGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF29B6F6), Color(0xFF0288D1), Color(0xFF01579B)),
        startY = waterTop,
        endY = height
    )
    drawRect(brush = waterGradient, topLeft = Offset(0f, waterTop), size = Size(width, height - waterTop))

    // Wave foam line
    val foamPath = Path().apply {
        moveTo(0f, waterTop)
        val steps = 20
        for (i in 0..steps) {
            val px = (i / steps.toFloat()) * width
            val py = waterTop + sin(i * 0.6f + waterPhase) * 4f
            lineTo(px, py)
        }
        lineTo(width, waterTop + 14f)
        lineTo(0f, waterTop + 14f)
        close()
    }
    drawPath(path = foamPath, color = Color(0x88FFFFFF))
}

/**
 * 2.5D Lakeside Colours: Vibrant sunset sky gradient, multi-band sun corona,
 * shimmering violet lake reflections.
 */
private fun DrawScope.drawLakesideColours25D(
    width: Float,
    height: Float,
    waterPhase: Float,
    lightPulse: Float,
    clock: Float
) {
    // 1. Multi-band Sunset Sky
    val rainbowGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFB74D), // Golden Ochre
            Color(0xFFFF8A65), // Sunset Coral
            Color(0xFFBA68C8), // Twilight Violet
            Color(0xFF4FC3F7), // Clear River Blue
            Color(0xFF0288D1)  // Deep Lake Blue
        ),
        startY = 0f,
        endY = height
    )
    drawRect(brush = rainbowGradient, size = Size(width, height))

    // 2. Soft Glowing Sun Corona
    val sunCenter = Offset(width * 0.50f, height * 0.28f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFEE58).copy(alpha = 0.65f * lightPulse),
                Color(0xFFFFB74D).copy(alpha = 0.35f),
                Color.Transparent
            ),
            center = sunCenter,
            radius = width * 0.38f
        ),
        radius = width * 0.38f,
        center = sunCenter
    )
}

/**
 * 2.5D Fisherman Song: Deep indigo twilight sky, glowing crescent moon,
 * silver water reflections, and calming rhythmic atmosphere.
 */
private fun DrawScope.drawFishermanSong25D(
    width: Float,
    height: Float,
    waterPhase: Float,
    windSway: Float,
    lightPulse: Float,
    clock: Float
) {
    // 1. Indigo Night Sky
    val nightGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D1B2A), // Deep Night Sky
            Color(0xFF1B263B), // Midnight Blue
            Color(0xFF283593), // Royal Indigo
            Color(0xFF004D40)  // Lakeside Pine
        ),
        startY = 0f,
        endY = height
    )
    drawRect(brush = nightGradient, size = Size(width, height))

    // 2. Glowing Golden Crescent Moon (2.5D Moon Glow)
    val moonCenter = Offset(width * 0.82f, height * 0.16f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF59D).copy(alpha = 0.45f * lightPulse), Color.Transparent),
            center = moonCenter,
            radius = width * 0.20f
        ),
        radius = width * 0.20f,
        center = moonCenter
    )
    // Moon body
    drawCircle(color = Color(0xFFFFF59D), radius = width * 0.07f, center = moonCenter)
    // Shadow bite to create elegant crescent
    drawCircle(color = Color(0xFF142036), radius = width * 0.062f, center = Offset(moonCenter.x - 12f, moonCenter.y - 8f))

    // 3. Shimmering Lake Water with Silver Moonlight Path
    val waterTop = height * 0.56f
    val waterGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF003830), Color(0xFF00241E), Color(0xFF001511)),
        startY = waterTop,
        endY = height
    )
    drawRect(brush = waterGradient, topLeft = Offset(0f, waterTop), size = Size(width, height - waterTop))

    // Silver Moonlight reflection path down the water
    val moonReflectionPath = Path().apply {
        val steps = 8
        for (i in 0..steps) {
            val y = waterTop + (i / steps.toFloat()) * (height - waterTop)
            val waveOffset = sin(waterPhase + i) * 6f
            val rx = moonCenter.x + waveOffset
            val rWidth = 16f + (i * 8f)
            moveTo(rx - rWidth, y)
            lineTo(rx + rWidth, y)
        }
    }
    drawPath(
        path = moonReflectionPath,
        color = Color(0xFFFFF9C4).copy(alpha = 0.35f * lightPulse),
        style = Stroke(width = 2.2f, cap = StrokeCap.Round)
    )
}

/**
 * Atmospheric 2.5D Particles: Floating golden sunlight dust motes during day,
 * warm embers in hearth scenes, and gentle glowing fireflies at dusk.
 */
private fun DrawScope.drawAtmosphericParticles(
    width: Float,
    height: Float,
    themeStyle: StorybookThemeStyle,
    particles: List<ParticleSeed>,
    clock: Float,
    pulse: Float
) {
    val isNight = themeStyle == StorybookThemeStyle.FISHERMAN_SONG
    val isHearth = themeStyle == StorybookThemeStyle.VILLAGE_HEARTH

    particles.forEach { seed ->
        val posX = (seed.baseX * width + sin(clock * seed.speed + seed.phaseOffset) * 22f) % width
        val posY = (seed.baseY * height - (clock * 12f * seed.speed) % (height * 0.8f))
        val currentY = if (posY < 0f) posY + height else posY

        val particleColor = when {
            isNight -> Color(0xFFFFF59D).copy(alpha = (0.45f * sin(clock * 2f + seed.phaseOffset) + 0.45f).coerceIn(0.1f, 0.85f))
            isHearth -> Color(0xFFFFAB40).copy(alpha = (0.50f * sin(clock * 3f + seed.phaseOffset) + 0.40f).coerceIn(0.1f, 0.80f))
            else -> Color(0xFFFFF9C4).copy(alpha = 0.35f * pulse)
        }

        drawCircle(
            color = particleColor,
            radius = seed.size,
            center = Offset(posX, currentY)
        )
    }
}

/**
 * Storybook Spine Seam & Page Gutter Shadow:
 * Evokes the physical spine fold of an open two-page picture book.
 */
private fun DrawScope.drawStorybookSpineFoldAndVignette(width: Float, height: Float) {
    val spineCenterX = width * 0.50f
    val spineWidth = width * 0.08f

    // Subtle center crease gradient (spine shadow)
    val spineBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0x183E2723),
            Color(0x283E2723),
            Color(0x183E2723),
            Color.Transparent
        ),
        startX = spineCenterX - spineWidth,
        endX = spineCenterX + spineWidth
    )
    drawRect(
        brush = spineBrush,
        topLeft = Offset(spineCenterX - spineWidth, 0f),
        size = Size(spineWidth * 2, height)
    )

    // Soft outer parchment border vignette
    val borderInset = 8f
    drawRoundRect(
        color = Color(0x123E2723),
        topLeft = Offset(borderInset, borderInset),
        size = Size(width - borderInset * 2, height - borderInset * 2),
        cornerRadius = CornerRadius(20f, 20f),
        style = Stroke(width = 3f)
    )
}
