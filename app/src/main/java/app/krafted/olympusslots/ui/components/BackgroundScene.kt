package app.krafted.olympusslots.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import app.krafted.olympusslots.viewmodel.Background

@Composable
fun BackgroundScene(
    background: Background,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val bgAlpha by animateFloatAsState(
        targetValue = if (background == Background.NEUTRAL) 0.7f else 1f,
        animationSpec = tween(800),
        label = "bgAlpha"
    )

    val pulseScale = if (background == Background.JACKPOT || background == Background.BIG_WIN) {
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

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = background.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(bgAlpha)
                .scale(pulseScale)
        )
        content()
    }
}
