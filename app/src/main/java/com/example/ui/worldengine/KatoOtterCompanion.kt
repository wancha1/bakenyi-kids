package com.example.ui.worldengine

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationWorldState
import com.example.ui.theme.OliveGreenPrimary
import com.example.ui.theme.StarGold
import com.example.ui.theme.SurfaceWhite

@Composable
fun KatoOtterCompanion(
    worldState: LocationWorldState,
    speechText: String,
    isPlayingAudio: Boolean,
    onKatoTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "KatoAnimations")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KatoPulse"
    )

    val currentScale = when {
        isPlayingAudio -> pulseScale
        worldState is LocationWorldState.Reward -> 1.15f
        else -> 1.0f
    }

    Box(
        modifier = modifier
            .testTag("kato_otter_companion")
            .clickable {
                Log.d("WORLD_ENGINE_DEBUG", "Kato otter tapped. Current state: $worldState")
                onKatoTap()
            }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Speech Bubble
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, StarGold, RoundedCornerShape(18.dp))
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isPlayingAudio) "🔊 " else "💬 ",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = speechText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OliveGreenPrimary
                    )
                }
            }

            // Kato Otter Avatar Icon Frame
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(currentScale)
                    .clip(CircleShape)
                    .background(StarGold)
                    .border(3.dp, SurfaceWhite, CircleShape)
                    .shadow(6.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (worldState) {
                        is LocationWorldState.Reward -> "🎉"
                        is LocationWorldState.Challenge -> "🧠"
                        else -> "🦦"
                    },
                    fontSize = 40.sp
                )
            }
        }
    }
}
