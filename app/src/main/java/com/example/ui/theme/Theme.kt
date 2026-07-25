package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BakenyeColorScheme = lightColorScheme(
    primary = OliveGreenPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = LightGoldBg,
    onPrimaryContainer = OliveGreenDark,
    secondary = GoldAccent,
    onSecondary = SurfaceWhite,
    secondaryContainer = LightGoldBg,
    onSecondaryContainer = GoldText,
    tertiary = StreakGreenText,
    onTertiary = SurfaceWhite,
    background = WarmCreamBg,
    onBackground = TextPrimaryDark,
    surface = SurfaceWhite,
    onSurface = TextPrimaryDark,
    surfaceVariant = WarmCreamBg,
    onSurfaceVariant = TextSecondaryMuted,
    outline = BorderSubtle
)

@Composable
fun BakenyeKidsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BakenyeColorScheme,
        typography = Typography,
        content = content
    )
}

