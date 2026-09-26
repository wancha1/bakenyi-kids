package com.example.ui.sanctuary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AuthenticAudioManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Interactive, animated Kato the Otter companion.
 * Features:
 * - Soothing idle bobbing and breathing animation.
 * - Blinking eyes and tail wag.
 * - Playful spring hop & spin on tap.
 * - Gentle chirp sound effect via AuthenticAudioManager.
 * - Friendly Lukenye audio encouragement and floating speech bubble.
 */
@Composable
fun KatoCompanionOverlay(
    audioManager: AuthenticAudioManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val infiniteTransition = rememberInfiniteTransition(label = "KatoBreathingCycle")

    // Idle vertical bobbing (calm 3.2s cycle)
    val idleBobPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "KatoIdleBob"
    )

    // Gentle tail wag tilt
    val tailWag by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KatoTailWag"
    )

    // Hop and spin bounce animation on tap
    val bounceScale = remember { Animatable(1f) }
    val bounceRotation = remember { Animatable(0f) }

    var speechBubbleText by remember { mutableStateOf<Pair<String, String>?>(null) }
    var phraseIndex by remember { mutableIntStateOf(0) }

    val katoPhrases = remember {
        listOf(
            "Oli otya! Nze Kato ogonze!" to "Hello! I am Kato the gentle river otter!",
            "Webale nnyo okusoma!" to "Thank you so much for reading with me!",
            "Laba amazzi ag'ennyanja Kyoga!" to "Look at the sparkling waters of Lake Kyoga!",
            "Kyenno kyenyini! Webale!" to "Exactly right! Wonderful discovery!",
            "Katono katono ke kawola!" to "Step by step, a small drop fills the gourd!",
            "Amagezi gali mu bakulu!" to "Great wisdom flows from the elders!"
        )
    }

    // Dismiss speech bubble after 4.5 seconds
    LaunchedEffect(speechBubbleText) {
        if (speechBubbleText != null) {
            delay(4500L)
            speechBubbleText = null
        }
    }

    Box(
        modifier = modifier.testTag("kato_companion_overlay"),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Speech Bubble
        AnimatedVisibility(
            visible = speechBubbleText != null,
            enter = fadeIn(tween(250)) + scaleIn(spring()),
            exit = fadeOut(tween(250)) + scaleOut(tween(200)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-60).dp, y = (-20).dp)
        ) {
            speechBubbleText?.let { (lukenye, english) ->
                Box(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFFFDF5))
                        .border(1.5.dp, Color(0xFFC4A482), RoundedCornerShape(18.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = lukenye,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                        Text(
                            text = english,
                            fontSize = 10.sp,
                            color = Color(0xFF6D4C41)
                        )
                    }
                }
            }
        }

        // Animated Kato Badge / Mascot
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = sin(idleBobPhase) * 4.dp.toPx()
                    rotationZ = tailWag + bounceRotation.value
                    scaleX = bounceScale.value
                    scaleY = bounceScale.value
                }
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFFEFE8D8))
                .border(2.5.dp, Color(0xFF8D6E63), CircleShape)
                .clickable {
                    coroutineScope.launch {
                        // Play sound & speech
                        audioManager.playKatoCompanionChirp()
                        val currentPair = katoPhrases[phraseIndex % katoPhrases.size]
                        phraseIndex++
                        speechBubbleText = currentPair

                        audioManager.speakWord(
                            lukenyeWord = currentPair.first,
                            englishWord = currentPair.second,
                            pronunciation = ""
                        )

                        // Tactile spring bounce
                        bounceScale.animateTo(1.22f, tween(150, easing = FastOutSlowInEasing))
                        bounceRotation.animateTo(12f, tween(120))
                        bounceRotation.animateTo(-10f, tween(120))
                        bounceRotation.animateTo(0f, tween(120))
                        bounceScale.animateTo(1f, spring(dampingRatio = 0.5f))
                    }
                }
                .padding(8.dp)
        ) {
            Text(
                text = "🦦",
                fontSize = 32.sp
            )
        }
    }
}
