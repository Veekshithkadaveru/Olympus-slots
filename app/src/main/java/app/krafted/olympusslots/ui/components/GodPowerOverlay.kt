package app.krafted.olympusslots.ui.components

import androidx.compose.animation.core.EaseInOutSine
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import app.krafted.olympusslots.game.God
import app.krafted.olympusslots.ui.theme.ApolloOrange
import app.krafted.olympusslots.ui.theme.ArtemisGreen
import app.krafted.olympusslots.ui.theme.DemeterGreen
import app.krafted.olympusslots.ui.theme.HadesPurple
import app.krafted.olympusslots.ui.theme.HermesGray
import app.krafted.olympusslots.ui.theme.OlympusGold
import app.krafted.olympusslots.ui.theme.OlympusGoldDark
import app.krafted.olympusslots.ui.theme.OlympusGoldLight
import app.krafted.olympusslots.ui.theme.PoseidonBlue
import app.krafted.olympusslots.ui.theme.ZeusYellow
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// ═══════════════════════════════════════════════════════════════════════════════
// ZEUS ── Lightning bolts + coin shower (also used directly by JackpotScreen)
// ═══════════════════════════════════════════════════════════════════════════════

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

// ═══════════════════════════════════════════════════════════════════════════════
// POSEIDON ── Tidal sine waves sweeping the screen from top-down
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun PoseidonWaveEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "poseidonWave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Draw 4 layered waves; bottom waves are more opaque (deeper water)
        repeat(4) { waveIndex ->
            val waveAlpha = 0.16f + waveIndex * 0.08f
            val amplitude = 20f + waveIndex * 7f
            val frequency = 1.8f + waveIndex * 0.4f
            val yBase = h * (0.28f + waveIndex * 0.16f)
            val phaseShift = waveIndex * (PI / 2).toFloat()

            val path = Path()
            val steps = 120
            val stepW = w / steps

            for (i in 0..steps) {
                val x = i * stepW
                val y = yBase + amplitude * sin(
                    phase + phaseShift + i.toFloat() / steps * frequency * 2 * PI.toFloat()
                )
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.lineTo(w, h)
            path.lineTo(0f, h)
            path.close()

            drawPath(path, color = PoseidonBlue.copy(alpha = waveAlpha))
            drawPath(
                path, color = Color.White.copy(alpha = waveAlpha * 0.25f),
                style = Stroke(width = 2f)
            )
        }
    }
}

@Composable
fun HadesSmokeEffect(modifier: Modifier = Modifier) {
    data class SmokeParticle(
        val x: Float,
        val startPhase: Float,
        val speed: Float,
        val size: Float,
        val wobble: Float
    )

    val particles = remember {
        List(25) {
            SmokeParticle(
                x = Random.nextFloat(),
                startPhase = Random.nextFloat(),
                speed = 0.28f + Random.nextFloat() * 0.45f,
                size = 18f + Random.nextFloat() * 32f,
                wobble = 20f + Random.nextFloat() * 40f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hadesSmoke")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "smokeTime"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                listOf(
                    Color.Transparent,
                    HadesPurple.copy(alpha = 0.10f),
                    HadesPurple.copy(alpha = 0.25f)
                )
            )
        )

        particles.forEach { p ->
            val progress = (p.startPhase + time * p.speed) % 1f
            val y = h * (1f - progress) // rises bottom → top
            val wobbleX = sin(time * 2.5f + p.startPhase * 7f) * p.wobble
            val x = p.x * w + wobbleX
            val alpha = when {
                progress < 0.2f -> progress / 0.2f * 0.5f
                progress > 0.7f -> (1f - (progress - 0.7f) / 0.3f) * 0.5f
                else -> 0.5f
            }
            val radius = p.size.dp.toPx() * (0.6f + progress * 0.7f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        HadesPurple.copy(alpha = alpha),
                        Color(0xFF1A0030).copy(alpha = alpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(x, y),
                    radius = radius
                ),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun ApolloRaysEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "apolloRays")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rayRotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rayPulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = sqrt(cx * cx + cy * cy) * 1.2f

        rotate(degrees = rotation, pivot = Offset(cx, cy)) {
            val rayCount = 16
            repeat(rayCount) { i ->
                val isLong = i % 2 == 0
                val angle = i * (360f / rayCount) * (PI.toFloat() / 180f)
                val rayLen = maxRadius * (if (isLong) 1f else 0.55f)
                val rayWidth = (if (isLong) 28f else 14f).dp.toPx()
                val endX = cx + cos(angle) * rayLen
                val endY = cy + sin(angle) * rayLen

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ApolloOrange.copy(alpha = 0.55f * pulse),
                            OlympusGold.copy(alpha = 0.35f * pulse),
                            Color.Transparent
                        ),
                        start = Offset(cx, cy),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(cx, cy),
                    end = Offset(endX, endY),
                    strokeWidth = rayWidth
                )
            }
        }

        // Central radial glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    OlympusGoldLight.copy(alpha = 0.65f * pulse),
                    ApolloOrange.copy(alpha = 0.35f * pulse),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = 70.dp.toPx()
            ),
            radius = 70.dp.toPx(),
            center = Offset(cx, cy)
        )
    }
}

@Composable
fun HermesDashEffect(modifier: Modifier = Modifier) {
    data class Streak(
        val yFrac: Float,
        val lengthFrac: Float,
        val speed: Float,
        val phase: Float,
        val thickness: Float
    )

    val streaks = remember {
        List(12) {
            Streak(
                yFrac = Random.nextFloat(),
                lengthFrac = 0.12f + Random.nextFloat() * 0.22f,
                speed = 0.7f + Random.nextFloat() * 0.9f,
                phase = Random.nextFloat(),
                thickness = 1.5f + Random.nextFloat() * 2.5f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hermesDash")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashTime"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        streaks.forEach { s ->
            val progress = (s.phase + time * s.speed) % 1f
            val headX = w * (progress + s.lengthFrac)
            val tailX = w * progress
            val y = s.yFrac * h
            val alpha = when {
                progress < 0.08f -> progress / 0.08f
                progress > 0.88f -> 1f - (progress - 0.88f) / 0.12f
                else -> 1f
            }

            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        HermesGray.copy(alpha = 0.5f * alpha),
                        OlympusGoldLight.copy(alpha = 0.9f * alpha),
                        Color.Transparent
                    ),
                    startX = tailX,
                    endX = headX
                ),
                start = Offset(tailX, y),
                end = Offset(headX, y),
                strokeWidth = s.thickness.dp.toPx()
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DEMETER ── Leaf-petal shower drifting down from above
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun DemeterPetalEffect(modifier: Modifier = Modifier) {
    data class Petal(
        val x: Float,
        val startPhase: Float,
        val speed: Float,
        val size: Float,
        val wobble: Float,
        val spinSpeed: Float
    )

    val petals = remember {
        List(30) {
            Petal(
                x = Random.nextFloat(),
                startPhase = Random.nextFloat(),
                speed = 0.22f + Random.nextFloat() * 0.30f,
                size = 4f + Random.nextFloat() * 6f,
                wobble = 14f + Random.nextFloat() * 22f,
                spinSpeed = 0.5f + Random.nextFloat() * 1.8f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "demeterPetals")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "petalTime"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        petals.forEach { p ->
            val progress = (p.startPhase + time * p.speed) % 1.2f
            if (progress > 1.1f) return@forEach

            val y = progress * h
            val wobbleX = sin(time * p.spinSpeed * 4f + p.startPhase * 5f) * p.wobble
            val x = p.x * w + wobbleX
            val alpha = when {
                progress < 0.1f -> progress / 0.1f * 0.8f
                progress > 0.8f -> ((1f - (progress - 0.8f) / 0.3f).coerceAtLeast(0f)) * 0.8f
                else -> 0.8f
            }
            val spinDeg = time * p.spinSpeed * 360f
            val sz = p.size.dp.toPx()

            withTransform({
                rotate(degrees = spinDeg, pivot = Offset(x, y))
            }) {
                // Leaf: two overlapping ovals for a teardrop silhouette
                drawOval(
                    color = DemeterGreen.copy(alpha = alpha),
                    topLeft = Offset(x - sz, y - sz * 1.8f),
                    size = Size(sz * 2f, sz * 3.6f)
                )
                drawOval(
                    color = ArtemisGreen.copy(alpha = alpha * 0.55f),
                    topLeft = Offset(x - sz * 0.35f, y - sz * 1.8f),
                    size = Size(sz * 1.35f, sz * 3.6f)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ARTEMIS ── Silver moon-arrows shooting across the screen
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ArtemisArrowEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "artemisArrows")

    // Two staggered arrows (different initial offsets → different entry times)
    val progress1 by infiniteTransition.animateFloat(
        initialValue = -0.25f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arrow1"
    )
    val progress2 by infiniteTransition.animateFloat(
        initialValue = -0.75f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arrow2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawArrowStreak(w, h, progress1, h * 0.33f)
        drawArrowStreak(w, h, progress2, h * 0.67f)
    }
}

private fun DrawScope.drawArrowStreak(w: Float, h: Float, progress: Float, arrowY: Float) {
    val x = progress * w
    val trailLen = w * 0.20f
    val headLen = 20.dp.toPx()
    val headHalf = 9.dp.toPx()

    // Motion trail
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                ArtemisGreen.copy(alpha = 0.25f),
                Color(0xFFDDDDDD).copy(alpha = 0.8f),
                Color.Transparent
            ),
            startX = x - trailLen,
            endX = x
        ),
        start = Offset(x - trailLen, arrowY),
        end = Offset(x, arrowY),
        strokeWidth = 2.5.dp.toPx()
    )

    // Arrow head (only when on-screen)
    if (progress in -0.05f..1.05f) {
        val arrowHead = Path().apply {
            moveTo(x, arrowY)
            lineTo(x - headLen, arrowY - headHalf)
            lineTo(x - headLen * 0.55f, arrowY)
            lineTo(x - headLen, arrowY + headHalf)
            close()
        }
        drawPath(arrowHead, color = Color(0xFFE0E0E0).copy(alpha = 0.95f))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DISPATCHER ── Routes to the correct effect based on which god won
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun GodPowerOverlay(god: God, modifier: Modifier = Modifier) {
    when (god) {
        God.ZEUS -> {
            LightningEffect(modifier)
            CoinShowerEffect(modifier)
        }
        God.POSEIDON -> PoseidonWaveEffect(modifier)
        God.HADES    -> HadesSmokeEffect(modifier)
        God.APOLLO   -> ApolloRaysEffect(modifier)
        God.HERMES   -> HermesDashEffect(modifier)
        God.DEMETER  -> DemeterPetalEffect(modifier)
        God.ARTEMIS  -> ArtemisArrowEffect(modifier)
    }
}
