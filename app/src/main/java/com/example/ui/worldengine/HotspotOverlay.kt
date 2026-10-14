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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VocabularyItem
import com.example.ui.theme.StarGold
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.WarmCreamBg

@Composable
fun HotspotOverlay(
    item: VocabularyItem,
    isDiscovered: Boolean,
    isCompleted: Boolean = false,
    onHotspotTap: (VocabularyItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RippleAnimation")

    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RipplePulse"
    )

    val itemTag = "hotspot_${item.lukenyeTerm.lowercase()}"

    Box(
        modifier = modifier
            .testTag(itemTag)
            .clickable {
                Log.d("WORLD_ENGINE_DEBUG", "Hotspot tapped: ${item.lukenyeTerm} (discovered=$isDiscovered, completed=$isCompleted)")
                onHotspotTap(item)
            },
        contentAlignment = Alignment.Center
    ) {
        // Water Ripple Pulsing Circle
        val rippleColor = when {
            isCompleted -> Color(0x66FFD700)
            isDiscovered -> Color(0x5500E5FF)
            else -> Color(0x3380D8FF)
        }
        val rippleBorder = when {
            isCompleted -> StarGold
            isDiscovered -> Color(0xFF00E5FF)
            else -> Color(0xAA80D8FF)
        }

        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(rippleScale)
                .clip(CircleShape)
                .background(rippleColor)
                .border(2.dp, rippleBorder, CircleShape)
        )

        // Hotspot Content Badge
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> StarGold
                            isDiscovered -> SurfaceWhite
                            else -> WarmCreamBg.copy(alpha = 0.95f)
                        }
                    )
                    .border(
                        width = if (isCompleted) 3.dp else 2.dp,
                        color = when {
                            isCompleted -> Color(0xFFE65100)
                            isDiscovered -> Color(0xFF00ACC1)
                            else -> Color(0xFF81D4FA)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDiscovered || isCompleted) {
                    Text(text = item.iconEmoji, fontSize = 28.sp)
                } else {
                    Text(text = "🌊", fontSize = 24.sp)
                }

                // Completion Star Overlay Badge
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE65100)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⭐", fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isCompleted) Color(0xFF2E7D32).copy(alpha = 0.85f)
                        else Color.Black.copy(alpha = 0.75f)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isDiscovered || isCompleted) item.lukenyeTerm else "???",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
            }
        }
    }
}
