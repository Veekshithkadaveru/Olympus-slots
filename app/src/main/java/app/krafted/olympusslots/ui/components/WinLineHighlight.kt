package app.krafted.olympusslots.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import app.krafted.olympusslots.ui.theme.OlympusGold
import app.krafted.olympusslots.ui.theme.OlympusGoldLight

@Composable
fun WinLineHighlight(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = OlympusGold
) {
    if (!isVisible) return

    val infiniteTransition = rememberInfiniteTransition(label = "winLine")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lineAlpha"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val width = size.width
        val centerY = size.height / 2

        // Base glow line
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    color.copy(alpha = alpha),
                    color.copy(alpha = alpha),
                    Color.Transparent
                ),
                startX = 0f,
                endX = width
            ),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Shimmer highlight
        val shimmerX = shimmerOffset * width
        val shimmerWidth = width * 0.2f
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    OlympusGoldLight.copy(alpha = alpha * 0.8f),
                    Color.Transparent
                ),
                startX = shimmerX - shimmerWidth / 2,
                endX = shimmerX + shimmerWidth / 2
            ),
            start = Offset(shimmerX - shimmerWidth / 2, centerY),
            end = Offset(shimmerX + shimmerWidth / 2, centerY),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
