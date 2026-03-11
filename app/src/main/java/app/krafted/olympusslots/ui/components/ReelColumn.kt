package app.krafted.olympusslots.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.krafted.olympusslots.game.God
import app.krafted.olympusslots.ui.theme.*
import app.krafted.olympusslots.viewmodel.SpinPhase
import kotlinx.coroutines.delay

@Composable
fun ReelColumn(
    targetGod: God,
    spinPhase: SpinPhase,
    stopDelay: Long,
    isWinning: Boolean,
    modifier: Modifier = Modifier
) {
    // Track which god symbols to display during spinning
    var displayedGods by remember { mutableStateOf(listOf(God.entries.random(), targetGod, God.entries.random())) }
    var isStopped by remember { mutableStateOf(true) }

    // Bounce animation on stop
    val bounceOffset = remember { Animatable(0f) }

    // Winning glow animation
    val glowAlpha = if (isWinning) {
        val infiniteTransition = rememberInfiniteTransition(label = "winGlow")
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        ).value
    } else 0f

    // Spinning symbol cycling
    LaunchedEffect(spinPhase) {
        when (spinPhase) {
            SpinPhase.SPINNING -> {
                isStopped = false
                // Rapidly cycle through random symbols
                while (true) {
                    displayedGods = listOf(
                        God.entries.random(),
                        God.entries.random(),
                        God.entries.random()
                    )
                    delay(80)
                }
            }
            SpinPhase.RESOLVING -> {
                delay(stopDelay)
                // Set final symbols with target in center
                displayedGods = listOf(
                    God.entries.filter { it != targetGod }.random(),
                    targetGod,
                    God.entries.filter { it != targetGod }.random()
                )
                // Bounce effect on stop
                bounceOffset.snapTo(-12f)
                bounceOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                isStopped = true
            }
            SpinPhase.IDLE -> {
                isStopped = true
                displayedGods = listOf(
                    God.entries.filter { it != targetGod }.random(),
                    targetGod,
                    God.entries.filter { it != targetGod }.random()
                )
            }
            SpinPhase.RESULT -> { /* keep current display */ }
        }
    }

    val shape = RoundedCornerShape(12.dp)
    val winBorderColor = if (isWinning) {
        when (targetGod) {
            God.ZEUS -> ZeusYellow
            God.POSEIDON -> PoseidonBlue
            God.HADES -> HadesPurple
            God.ARTEMIS -> ArtemisGreen
            God.APOLLO -> ApolloOrange
            God.DEMETER -> DemeterGreen
            God.HERMES -> HermesGray
        }
    } else ReelBorder

    Box(
        modifier = modifier
            .width(105.dp)
            .height(280.dp)
            .shadow(
                elevation = if (isWinning) 16.dp else 8.dp,
                shape = shape,
                ambientColor = if (isWinning) winBorderColor.copy(alpha = 0.6f) else Color.Black,
                spotColor = if (isWinning) winBorderColor else Color.Black
            )
            .clip(shape)
            .background(ReelBackground)
            .border(
                width = if (isWinning) 3.dp else 2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        winBorderColor.copy(alpha = 0.8f),
                        winBorderColor,
                        winBorderColor.copy(alpha = 0.8f)
                    )
                ),
                shape = shape
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = bounceOffset.value.dp.toPx() },
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            displayedGods.forEachIndexed { index, god ->
                val isCenter = index == 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = god.drawableRes),
                        contentDescription = god.displayName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(if (isCenter) 80.dp else 56.dp)
                            .graphicsLayer {
                                alpha = if (isCenter) 1f else 0.4f
                                scaleX = if (isCenter) 1f else 0.75f
                                scaleY = if (isCenter) 1f else 0.75f
                            }
                    )
                }
            }
        }

        // Top and bottom fade gradients
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ReelBackground,
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ReelBackground
                        )
                    )
                )
        )

        if (isWinning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = glowAlpha * 0.15f }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(winBorderColor, Color.Transparent)
                        )
                    )
            )
        }
    }
}
