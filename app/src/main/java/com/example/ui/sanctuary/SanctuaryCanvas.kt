package com.example.ui.sanctuary

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.cos
import kotlin.math.sin

data class WaterRipple(
    val x: Float,
    val y: Float,
    var radius: Float = 10f,
    var alpha: Float = 0.8f,
    val maxRadius: Float = 120f
)

data class FireflyParticle(
    val initialXRatio: Float,
    val initialYRatio: Float,
    val speed: Float,
    val phase: Float
)

@Composable
fun SanctuaryCanvas(
    timeOfDay: DiurnalTimeOfDay,
    activeBiome: SanctuaryBiome,
    modifier: Modifier = Modifier,
    onWaterTap: (Offset) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SanctuaryEcosystemAnimation")

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val reedSway by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ReedSway"
    )

    val fireflyPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FireflyPulse"
    )

    val ripples = remember { mutableStateListOf<WaterRipple>() }

    val fireflies = remember {
        List(18) { index ->
            FireflyParticle(
                initialXRatio = (index * 0.055f + 0.08f) % 0.95f,
                initialYRatio = 0.40f + (index * 0.035f % 0.45f),
                speed = 0.5f + (index % 3) * 0.3f,
                phase = index * 0.5f
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    ripples.add(WaterRipple(offset.x, offset.y))
                    if (ripples.size > 12) {
                        ripples.removeAt(0)
                    }
                    onWaterTap(offset)
                }
            }
    ) {
        val width = size.width
        val height = size.height

        if (width <= 0f || height <= 0f) return@Canvas

        try {
            // 1. Sky & Atmospheric Lighting
            drawAtmosphereSky(width, height, timeOfDay)

            // 2. Horizon Biome Background Elements
            when (activeBiome) {
                SanctuaryBiome.HILLS_AND_MEADOWS -> drawHillsAndMeadows(width, height, reedSway)
                SanctuaryBiome.BAOBAB_FOREST -> drawBaobabGrove(width, height, reedSway)
                SanctuaryBiome.VILLAGE_LANDING -> drawVillageCanoeLanding(width, height, wavePhase)
                SanctuaryBiome.RIVER_WETLANDS -> drawWetlandsAndRiver(width, height, wavePhase, reedSway)
            }

            // 3. Dynamic Water Surface
            drawWaterSurface(width, height, wavePhase, timeOfDay)

            // 4. Interactive Water Displacement Ripples
            val iterator = ripples.iterator()
            while (iterator.hasNext()) {
                val ripple = iterator.next()
                ripple.radius += 3.5f
                ripple.alpha -= 0.022f

                if (ripple.alpha <= 0f || ripple.radius >= ripple.maxRadius) {
                    iterator.remove()
                } else {
                    drawCircle(
                        color = Color.White.copy(alpha = ripple.alpha.coerceIn(0f, 1f)),
                        radius = ripple.radius,
                        center = Offset(ripple.x, ripple.y),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )
                }
            }

            // 5. Fireflies at Evening/Twilight
            if (timeOfDay == DiurnalTimeOfDay.EVENING_GOLD || timeOfDay == DiurnalTimeOfDay.TWILIGHT_DUSK) {
                fireflies.forEach { firefly ->
                    val x = (firefly.initialXRatio * width) + (sin(wavePhase * firefly.speed + firefly.phase) * 25f)
                    val y = (firefly.initialYRatio * height) + (cos(wavePhase * firefly.speed + firefly.phase) * 15f)
                    val alpha = (fireflyPulse * sin(wavePhase + firefly.phase)).coerceIn(0.1f, 0.9f)

                    drawCircle(
                        color = Color(0xFFFFB703).copy(alpha = alpha),
                        radius = 6f,
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = Color(0xFFFFF3C4).copy(alpha = alpha * 0.5f),
                        radius = 12f,
                        center = Offset(x, y)
                    )
                }
            }

        } catch (e: Exception) {
            Log.e("SANCTUARY_CANVAS", "Error rendering SanctuaryCanvas frame", e)
        }
    }
}

private fun DrawScope.drawAtmosphereSky(width: Float, height: Float, timeOfDay: DiurnalTimeOfDay) {
    val skyColors = when (timeOfDay) {
        DiurnalTimeOfDay.MORNING_DAWN -> listOf(Color(0xFFFFF3E0), Color(0xFFE0F7FA), Color(0xFFB2EBF2))
        DiurnalTimeOfDay.MIDDAY_SUN -> listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD), Color(0xFF38BDF8))
        DiurnalTimeOfDay.EVENING_GOLD -> listOf(Color(0xFFFFE0B2), Color(0xFFFFB74D), Color(0xFFF57C00))
        DiurnalTimeOfDay.TWILIGHT_DUSK -> listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF1E293B))
    }

    val skyGradient = Brush.verticalGradient(
        colors = skyColors,
        startY = 0f,
        endY = height * 0.60f
    )
    drawRect(brush = skyGradient, size = Size(width, height * 0.60f))
}

private fun DrawScope.drawHillsAndMeadows(width: Float, height: Float, sway: Float) {
    val hillPath = Path().apply {
        moveTo(0f, height * 0.40f)
        quadraticTo(width * 0.25f, height * 0.30f, width * 0.50f, height * 0.38f)
        quadraticTo(width * 0.75f, height * 0.45f, width, height * 0.35f)
        lineTo(width, height * 0.65f)
        lineTo(0f, height * 0.65f)
        close()
    }
    drawPath(path = hillPath, brush = Brush.verticalGradient(listOf(Color(0xFF689F38), Color(0xFF33691E))))
}

private fun DrawScope.drawBaobabGrove(width: Float, height: Float, sway: Float) {
    // Baobab Trunk
    val trunkPath = Path().apply {
        moveTo(width * 0.15f, height * 0.60f)
        quadraticTo(width * 0.25f, height * 0.35f, width * 0.22f, height * 0.15f)
        lineTo(width * 0.35f, height * 0.15f)
        quadraticTo(width * 0.32f, height * 0.35f, width * 0.42f, height * 0.60f)
        close()
    }
    drawPath(path = trunkPath, brush = Brush.horizontalGradient(listOf(Color(0xFF4E342E), Color(0xFF6D4C41), Color(0xFF3E2723))))

    // Baobab Canopy Leaves
    drawCircle(color = Color(0xFF2E7D32), radius = width * 0.18f, center = Offset(width * 0.28f + sway * 0.3f, height * 0.18f))
    drawCircle(color = Color(0xFF388E3C), radius = width * 0.14f, center = Offset(width * 0.18f + sway * 0.2f, height * 0.22f))
}

private fun DrawScope.drawVillageCanoeLanding(width: Float, height: Float, wavePhase: Float) {
    // Homestead Clay Hut Outline
    val hutCenterX = width * 0.78f
    val hutBaseY = height * 0.48f

    // Roof
    val roofPath = Path().apply {
        moveTo(hutCenterX - 80f, hutBaseY)
        lineTo(hutCenterX, hutBaseY - 70f)
        lineTo(hutCenterX + 80f, hutBaseY)
        close()
    }
    drawPath(path = roofPath, color = Color(0xFF8D6E63))

    // Wall
    drawRoundRect(
        color = Color(0xFFD7CCC8),
        topLeft = Offset(hutCenterX - 65f, hutBaseY),
        size = Size(130f, 60f),
        cornerRadius = CornerRadius(8f, 8f)
    )
}

private fun DrawScope.drawWetlandsAndRiver(width: Float, height: Float, wavePhase: Float, sway: Float) {
    // Papyrus Reeds along river edge
    val reedColor = Color(0xFF2E7D32)
    val reedWidth = width * 0.012f
    for (i in 0..22) {
        val reedX = (i / 22f) * width
        val reedHeight = (height * 0.12f) + (sin(i.toDouble()) * 14f).toFloat()
        val swayX = (sin((i + sway * 0.1f).toDouble()) * 10f).toFloat()

        drawLine(
            color = reedColor,
            start = Offset(reedX, height * 0.52f),
            end = Offset(reedX + swayX, height * 0.52f - reedHeight),
            strokeWidth = reedWidth
        )
    }
}

private fun DrawScope.drawWaterSurface(width: Float, height: Float, wavePhase: Float, timeOfDay: DiurnalTimeOfDay) {
    val waterTop = height * 0.52f
    val waterColors = when (timeOfDay) {
        DiurnalTimeOfDay.MORNING_DAWN -> listOf(Color(0xFF00ACC1), Color(0xFF00838F), Color(0xFF006064))
        DiurnalTimeOfDay.MIDDAY_SUN -> listOf(Color(0xFF0284C7), Color(0xFF0369A1), Color(0xFF075985))
        DiurnalTimeOfDay.EVENING_GOLD -> listOf(Color(0xFFD97706), Color(0xFFB45309), Color(0xFF78350F))
        DiurnalTimeOfDay.TWILIGHT_DUSK -> listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0284C7))
    }

    val waterGradient = Brush.verticalGradient(colors = waterColors, startY = waterTop, endY = height)

    val waterPath = Path().apply {
        moveTo(0f, waterTop)
        val steps = 24
        for (i in 0..steps) {
            val x = (i / steps.toFloat()) * width
            val y = waterTop + (sin((i * 0.5f) + wavePhase) * 10f).toFloat()
            lineTo(x, y)
        }
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }

    drawPath(path = waterPath, brush = waterGradient)
}
