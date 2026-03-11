package app.krafted.olympusslots.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import app.krafted.olympusslots.ui.theme.OlympusGold
import app.krafted.olympusslots.ui.theme.OlympusGoldDark
import app.krafted.olympusslots.ui.theme.OlympusGoldLight
import app.krafted.olympusslots.ui.theme.ZeusYellow
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun LightningEffect(modifier: Modifier = Modifier) {

    var boltSeed by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(250)
            boltSeed++
        }
    }

    val flashAlpha by animateFloatAsState(
        targetValue = if (boltSeed % 3 == 0) 0.15f else 0f,
        animationSpec = tween(100),
        label = "lightningFlash"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val rng = Random(boltSeed)

        if (flashAlpha > 0f) {
            drawRect(Color.White.copy(alpha = flashAlpha))
        }

        val boltCount = 2 + rng.nextInt(2)
        repeat(boltCount) {
            val startX = w * (0.1f + rng.nextFloat() * 0.8f)
            val segments = 8 + rng.nextInt(6)
            val path = Path()
            path.moveTo(startX, 0f)

            var currentX = startX
            var currentY = 0f
            val segmentHeight = h * 0.6f / segments

            for (s in 0 until segments) {
                currentX += (rng.nextFloat() - 0.5f) * 80f
                currentY += segmentHeight
                path.lineTo(currentX, currentY)
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.8f),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
            drawPath(
                path = path,
                color = ZeusYellow.copy(alpha = 0.3f),
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )

            if (rng.nextFloat() > 0.4f) {
                val branchPath = Path()
                val branchStartY = segmentHeight * (2 + rng.nextInt(segments / 2))
                val branchStartX = startX + (rng.nextFloat() - 0.5f) * 60f
                branchPath.moveTo(branchStartX, branchStartY)
                var bx = branchStartX
                var by = branchStartY
                repeat(4) {
                    bx += (rng.nextFloat() - 0.3f) * 50f
                    by += segmentHeight * 0.7f
                    branchPath.lineTo(bx, by)
                }
                drawPath(
                    path = branchPath,
                    color = Color.White.copy(alpha = 0.5f),
                    style = Stroke(width = 1.5f, cap = StrokeCap.Round)
                )
                drawPath(
                    path = branchPath,
                    color = ZeusYellow.copy(alpha = 0.2f),
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun CoinShowerEffect(modifier: Modifier = Modifier) {
    data class CoinParticle(
        val x: Float,
        val startY: Float,
        val speed: Float,
        val size: Float,
        val wobbleSpeed: Float,
        val wobbleAmount: Float
    )

    val particles = remember {
        List(35) {
            CoinParticle(
                x = Random.nextFloat(),
                startY = -Random.nextFloat() * 0.5f,
                speed = 0.4f + Random.nextFloat() * 0.6f,
                size = 3f + Random.nextFloat() * 5f,
                wobbleSpeed = 1f + Random.nextFloat() * 3f,
                wobbleAmount = 10f + Random.nextFloat() * 20f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "coinShower")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coinTime"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val y = ((p.startY + time * p.speed * 1.5f) % 1.3f) * size.height
            val wobble = sin(time * p.wobbleSpeed * 2 * PI.toFloat()) * p.wobbleAmount
            val x = p.x * size.width + wobble

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(OlympusGoldLight, OlympusGold, OlympusGoldDark),
                    center = Offset(x, y)
                ),
                radius = p.size.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = p.size.dp.toPx() * 0.4f,
                center = Offset(x - p.size * 0.3f, y - p.size * 0.3f)
            )
        }
    }
}
