package app.krafted.olympusslots.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import app.krafted.olympusslots.viewmodel.Background

@Composable
fun BackgroundScene(
    background: Background,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(
            targetState = background,
            animationSpec = tween(800),
            label = "bgCrossfade"
        ) { activeBg ->
            val pulseScale = if (activeBg == Background.JACKPOT || activeBg == Background.BIG_WIN) {
                val infiniteTransition = rememberInfiniteTransition(label = "bgPulse")
                infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.03f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseScale"
                ).value
            } else 1f

            // Distinct color tint per state so transitions are clearly visible
            val colorFilter = when (activeBg) {
                Background.NEUTRAL -> ColorFilter.tint(
                    Color(0xFF1A0E3E).copy(alpha = 0.45f), BlendMode.Darken
                )
                Background.MINOR_WIN -> ColorFilter.tint(
                    Color(0xFFB8860B).copy(alpha = 0.18f), BlendMode.Screen
                )
                Background.STANDARD_WIN -> ColorFilter.tint(
                    Color(0xFFFFD700).copy(alpha = 0.15f), BlendMode.Screen
                )
                Background.BIG_WIN -> ColorFilter.tint(
                    Color(0xFF0077B6).copy(alpha = 0.25f), BlendMode.Screen
                )
                Background.JACKPOT -> ColorFilter.tint(
                    Color(0xFFFFE066).copy(alpha = 0.22f), BlendMode.Screen
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = activeBg.drawableRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter = colorFilter,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                )

                // Extra gradient overlay to further differentiate each state
                when (activeBg) {
                    Background.NEUTRAL -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF0D0D2B).copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color(0xFF0D0D2B).copy(alpha = 0.6f)
                                    )
                                )
                            )
                        }
                    }
                    Background.JACKPOT -> {
                        val infiniteTransition = rememberInfiniteTransition(label = "jackpotGlow")
                        val glowAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.08f,
                            targetValue = 0.22f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, easing = EaseInOutSine),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "jackpotGlowAlpha"
                        )
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(Color(0xFFFFD700).copy(alpha = glowAlpha))
                            drawRect(
                                brush = Brush.radialGradient(
                                    listOf(
                                        Color(0xFFFFFFCC).copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                        }
                    }
                    Background.BIG_WIN -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF004E92).copy(alpha = 0.35f),
                                        Color.Transparent,
                                        Color(0xFF1B1464).copy(alpha = 0.3f)
                                    )
                                )
                            )
                        }
                    }
                    Background.STANDARD_WIN -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFFFD700).copy(alpha = 0.12f),
                                        Color.Transparent,
                                        Color(0xFFB8860B).copy(alpha = 0.1f)
                                    )
                                )
                            )
                        }
                    }
                    Background.MINOR_WIN -> {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xFFCD7F32).copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
        content()
    }
}
