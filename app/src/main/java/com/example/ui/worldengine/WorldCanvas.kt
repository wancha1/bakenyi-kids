package com.example.ui.worldengine

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
fun WorldCanvas(
    modifier: Modifier = Modifier,
    isWaterAnimating: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LakeWaterAnimation")
    
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val canoeBob by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CanoeBobbing"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Guard against invalid or unmeasured Canvas dimensions
        if (width <= 0f || height <= 0f) {
            Log.w("WORLD_ENGINE_DEBUG", "WorldCanvas skipped rendering due to non-positive size: $width x $height")
            return@Canvas
        }

        try {
            // 1. Sky & Atmosphere Gradient
            drawSkyBackground(width, height)

            // 2. Far Papyrus Reeds & Shoreline
            drawShoreline(width, height)

            // 3. Animated Lake Water
            drawLakeWater(width, height, if (isWaterAnimating) waveOffset else 0f)

            // 4. Handcrafted Wooden Canoe
            drawCanoe(width, height, canoeBob)

        } catch (e: Exception) {
            Log.e("WORLD_ENGINE_DEBUG", "Error rendering WorldCanvas frame", e)
            Log.e("BAKENYE_CRASH", "WorldCanvas rendering exception caught safely", e)
        }
    }
}

private fun DrawScope.drawSkyBackground(width: Float, height: Float) {
    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF3E0), // Morning Warm Cream
            Color(0xFFE0F7FA), // Soft Blue Lake Air
            Color(0xFFB2EBF2)  // Water Horizon
        ),
        startY = 0f,
        endY = height * 0.55f
    )
    drawRect(brush = skyGradient, size = Size(width, height * 0.55f))
}

private fun DrawScope.drawShoreline(width: Float, height: Float) {
    val shoreTop = height * 0.38f
    val shoreBottom = height * 0.50f

    // Papyrus shoreline green band
    val shoreGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF33691E), Color(0xFF558B2F), Color(0xFF689F38)),
        startY = shoreTop,
        endY = shoreBottom
    )
    drawRect(
        brush = shoreGradient,
        topLeft = Offset(0f, shoreTop),
        size = Size(width, shoreBottom - shoreTop)
    )

    // Reeds along the water line
    val reedColor = Color(0xFF2E7D32)
    val reedWidth = width * 0.015f
    for (i in 0..25) {
        val reedX = (i / 25f) * width
        val reedHeight = (height * 0.08f) + (sin(i.toDouble()) * 12f).toFloat()
        drawLine(
            color = reedColor,
            start = Offset(reedX, shoreBottom),
            end = Offset(reedX + (sin(i.toDouble()) * 8f).toFloat(), shoreBottom - reedHeight),
            strokeWidth = reedWidth
        )
    }
}

private fun DrawScope.drawLakeWater(width: Float, height: Float, wavePhase: Float) {
    val waterTop = height * 0.48f
    val waterGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF00ACC1), // Lake Kyoga Turquoise
            Color(0xFF00838F), // Deep Lake Water
            Color(0xFF006064)
        ),
        startY = waterTop,
        endY = height
    )

    val waterPath = Path().apply {
        moveTo(0f, waterTop)
        val steps = 20
        for (i in 0..steps) {
            val x = (i / steps.toFloat()) * width
            val y = waterTop + (sin((i * 0.6f) + wavePhase) * 12f).toFloat()
            lineTo(x, y)
        }
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }

    drawPath(path = waterPath, brush = waterGradient)

    // Sunlight reflections on water
    val shimmerColor = Color.White.copy(alpha = 0.25f)
    for (row in 1..4) {
        val rowY = waterTop + (row * height * 0.10f)
        val shift = sin((wavePhase + row).toDouble()).toFloat() * 20f
        drawRoundRect(
            color = shimmerColor,
            topLeft = Offset(width * 0.2f + shift, rowY),
            size = Size(width * 0.35f, height * 0.008f),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }
}

private fun DrawScope.drawCanoe(width: Float, height: Float, bobY: Float) {
    val canoeCenterX = width * 0.35f
    val canoeY = (height * 0.62f) + bobY
    val canoeWidth = width * 0.30f
    val canoeHeight = height * 0.05f

    val woodGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF4E342E), Color(0xFF6D4C41), Color(0xFF3E2723))
    )

    val canoePath = Path().apply {
        moveTo(canoeCenterX - (canoeWidth / 2f), canoeY)
        quadraticTo(
            canoeCenterX,
            canoeY + (canoeHeight * 1.5f),
            canoeCenterX + (canoeWidth / 2f),
            canoeY
        )
        quadraticTo(
            canoeCenterX,
            canoeY + (canoeHeight * 0.3f),
            canoeCenterX - (canoeWidth / 2f),
            canoeY
        )
        close()
    }

    drawPath(path = canoePath, brush = woodGradient)
}
