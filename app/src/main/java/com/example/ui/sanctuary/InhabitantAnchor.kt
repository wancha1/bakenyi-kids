package com.example.ui.sanctuary

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InhabitantAnchor(
    inhabitant: SanctuaryInhabitant,
    onTap: (SanctuaryInhabitant) -> Unit,
    modifier: Modifier = Modifier,
    xOffset: Dp = 0.dp,
    yOffset: Dp = 0.dp
) {
    var isTapped by remember { mutableStateOf(false) }

    val scaleState by animateFloatAsState(
        targetValue = if (isTapped) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isTapped = false },
        label = "InhabitantBounce"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .offset(x = xOffset, y = yOffset)
            .testTag("inhabitant_anchor_${inhabitant.id}")
    ) {
        // Organic Tactile Anchor Hit Target (Minimum 64dp)
        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(scaleState)
                .clip(CircleShape)
                .background(Color(0xFFFFF8E1).copy(alpha = 0.92f))
                .border(3.dp, Color(0xFFD2B48C), CircleShape)
                .shadow(6.dp, CircleShape)
                .clickable {
                    isTapped = true
                    onTap(inhabitant)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = inhabitant.emojiIcon,
                fontSize = 40.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Natural Object Label Tag (Nature-inspired Ochre / Bark cloth)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF3E2723).copy(alpha = 0.85f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = inhabitant.nativeTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFECB3)
                )
                if (inhabitant.nativeAudioPhrase.isNotBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "🔊", fontSize = 10.sp)
                }
            }
        }
    }
}
