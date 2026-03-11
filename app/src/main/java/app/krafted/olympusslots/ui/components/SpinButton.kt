package app.krafted.olympusslots.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.olympusslots.ui.theme.*

@Composable
fun SpinButton(
    isFree: Boolean,
    isEnabled: Boolean,
    spinCost: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "epicBtn")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isEnabled) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "epicPulse"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isEnabled) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRot"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isEnabled) 1f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "epicGlow"
    )

    val pressScale by animateFloatAsState(
        targetValue = when {
            !isEnabled -> 1f
            isPressed -> 0.88f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )

    val buttonSize = 130.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(buttonSize + 40.dp)
        ) {
            // Outermost soft glow
            Box(
                modifier = Modifier
                    .size(buttonSize + 30.dp)
                    .graphicsLayer { alpha = glowAlpha * 0.25f }
                    .blur(20.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (isFree) PoseidonBlue else OlympusGold)
            )

            if (isEnabled) {
                // Rotating outer ring with sweep gradient
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(buttonSize + 16.dp)
                        .graphicsLayer { rotationZ = ringRotation }
                ) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                OlympusGoldLight,
                                Color.Transparent,
                                OlympusGold,
                                Color.Transparent,
                                OlympusGoldLight,
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension / 2,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                    )
                }
            }

            // Static middle ring
            Box(
                modifier = Modifier
                    .size(buttonSize + 8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .border(
                        width = 3.dp,
                        brush = Brush.verticalGradient(
                            if (isEnabled) listOf(
                                OlympusGoldLight.copy(alpha = 0.9f),
                                OlympusGoldDark.copy(alpha = 0.6f),
                                OlympusBronze.copy(alpha = 0.8f)
                            ) else listOf(Color.Gray, Color.DarkGray)
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )

            val innerGradient = if (isEnabled) {
                if (isFree) arrayOf(
                    0f to Color(0xFFE0F7FA),
                    0.3f to PoseidonBlue,
                    0.7f to Color(0xFF006064),
                    1f to Color(0xFF004D40)
                ) else arrayOf(
                    0f to Color(0xFFFFF5CC),
                    0.3f to OlympusGold,
                    0.7f to OlympusGoldDark,
                    1f to Color(0xFF8B6914)
                )
            } else {
                arrayOf(
                    0f to Color(0xFF888888),
                    1f to Color(0xFF333333)
                )
            }

            // Main circular button
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .graphicsLayer {
                        scaleX = pulseScale * pressScale
                        scaleY = pulseScale * pressScale
                    }
                    .shadow(
                        elevation = if (isEnabled) 20.dp else 4.dp,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        ambientColor = if (isEnabled) OlympusGold.copy(alpha = 0.4f) else Color.Black,
                        spotColor = if (isEnabled) OlympusGold else Color.Black
                    )
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Brush.radialGradient(colorStops = innerGradient))
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f)
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = isEnabled,
                        onClick = onClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner highlight crescent at top
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(18.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 10.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isFree) "FREE" else "SPIN",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            color = if (isEnabled) OlympusPurpleDeep else Color.DarkGray,
                            shadow = if (isEnabled) androidx.compose.ui.graphics.Shadow(
                                color = OlympusGold.copy(alpha = 0.5f),
                                offset = androidx.compose.ui.geometry.Offset(0f, 2f),
                                blurRadius = 4f
                            ) else null
                        )
                    )
                    if (!isFree && isEnabled) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(1.5.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, OlympusPurpleDeep.copy(alpha = 0.5f), Color.Transparent)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$spinCost COINS",
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = OlympusPurpleDeep.copy(alpha = 0.7f)
                            )
                        )
                    } else if (isFree && isEnabled) {
                        Text(
                            text = "SPIN",
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 3.sp,
                                color = OlympusPurpleDeep.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }
        }
    }
}
