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

@Composable
fun HotspotOverlay(
    item: VocabularyItem,
    isDiscovered: Boolean,
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

    Box(
        modifier = modifier
            .testTag("hotspot_ensomba")
            .clickable {
                Log.d("WORLD_ENGINE_DEBUG", "Hotspot tapped: ${item.lugandaTerm}")
                onHotspotTap(item)
            },
        contentAlignment = Alignment.Center
    ) {
        // Water Ripple Pulsing Circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(rippleScale)
                .clip(CircleShape)
                .background(Color(0x5500E5FF))
                .border(2.dp, Color(0xFF00E5FF), CircleShape)
        )

        // Hotspot Content Badge
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isDiscovered) StarGold else SurfaceWhite)
                    .border(3.dp, Color(0xFF00ACC1), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.lugandaTerm,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
            }
        }
    }
}
