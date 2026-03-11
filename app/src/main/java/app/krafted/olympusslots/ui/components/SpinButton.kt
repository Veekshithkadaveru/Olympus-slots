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

    // Pulsing glow when enabled and idle
    val infiniteTransition = rememberInfiniteTransition(label = "spinPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = when {
            !isEnabled -> 0.95f
            isPressed -> 0.92f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val shape = RoundedCornerShape(16.dp)
    val gradientColors = if (isEnabled) {
        if (isFree) {
            listOf(PoseidonBlue, Color(0xFF0097A7))
        } else {
            listOf(OlympusGold, OlympusGoldDark)
        }
    } else {
        listOf(Color(0xFF555555), Color(0xFF333333))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isEnabled) 12.dp else 4.dp,
                shape = shape,
                ambientColor = if (isEnabled) OlympusGold.copy(alpha = 0.3f) else Color.Black,
                spotColor = if (isEnabled) OlympusGold else Color.Black
            )
            .clip(shape)
            .background(Brush.horizontalGradient(gradientColors))
            .then(
                if (isEnabled) {
                    Modifier.border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                OlympusGoldLight.copy(alpha = pulseAlpha),
                                OlympusGold.copy(alpha = pulseAlpha * 0.7f),
                                OlympusGoldLight.copy(alpha = pulseAlpha)
                            )
                        ),
                        shape = shape
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isFree) "FREE SPIN" else "SPIN",
                color = if (isEnabled) OlympusPurpleDeep else Color.Gray,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                letterSpacing = 3.sp
            )
            if (!isFree && isEnabled) {
                Text(
                    text = "$spinCost coins",
                    color = OlympusPurpleDeep.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
