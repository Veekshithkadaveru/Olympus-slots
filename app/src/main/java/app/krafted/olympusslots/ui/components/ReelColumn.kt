package app.krafted.olympusslots.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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

private val REEL_HEIGHT = 280.dp
private val SYMBOL_H = REEL_HEIGHT / 3f   // ~93.33 dp per slot

private fun randomStrip(targetGod: God? = null): List<God> {
    val others = God.entries.filter { it != targetGod }
    return if (targetGod != null)
        listOf(others.random(), others.random(), targetGod, others.random())
    else
        List(4) { God.entries.random() }
}

@Composable
fun ReelColumn(
    targetGod: God,
    spinPhase: SpinPhase,
    stopDelay: Long,
    isWinning: Boolean,
    modifier: Modifier = Modifier
) {
    // 4-symbol strip — index 2 is always the "center" (payline) slot.
    // Index 0 starts off-screen above the viewport during idle.
    var strip by remember { mutableStateOf(randomStrip(targetGod)) }

    // Vertical scroll progress: 0f → strip[0] off-screen, 1f → strip[0] fully at top
    val scrollAnim = remember { Animatable(0f) }

    // Brief scale-pop when the reel snaps into its final position
    val popAnim = remember { Animatable(1f) }

    // ── Winning animations ────────────────────────────────────────────────────

    val glowAlpha = if (isWinning) {
        rememberInfiniteTransition(label = "winGlow").animateFloat(
            initialValue = 0.20f, targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ), label = "glow"
        ).value
    } else 0f

    val sheenPos = if (isWinning) {
        rememberInfiniteTransition(label = "sheen").animateFloat(
            initialValue = -0.4f, targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "sheen"
        ).value
    } else 0f

    // ── Spin state machine ────────────────────────────────────────────────────

    val isActivelySpinning = spinPhase == SpinPhase.SPINNING

    LaunchedEffect(spinPhase) {
        when (spinPhase) {
            SpinPhase.SPINNING -> {
                popAnim.snapTo(1f)
                strip = List(4) { God.entries.random() }
                scrollAnim.snapTo(0f)
                // Fast continuous scroll: each symbol takes 85 ms to pass through
                while (true) {
                    scrollAnim.animateTo(1f, tween(85, easing = LinearEasing))
                    // Advance strip: push a new random symbol from the top
                    strip = listOf(God.entries.random()) + strip.dropLast(1)
                    scrollAnim.snapTo(0f)
                }
            }

            SpinPhase.RESOLVING -> {
                // Keep spinning until just before the stop deadline
                scrollAnim.snapTo(0f)
                val deadline = System.currentTimeMillis() + stopDelay - 200L
                while (System.currentTimeMillis() < deadline) {
                    scrollAnim.animateTo(1f, tween(85, easing = LinearEasing))
                    strip = listOf(God.entries.random()) + strip.dropLast(1)
                    scrollAnim.snapTo(0f)
                }

                // Land on final result: targetGod at center (index 2)
                strip = randomStrip(targetGod)
                scrollAnim.snapTo(0f)

                // Overshoot → spring back (reel "thumps" into place)
                scrollAnim.animateTo(0.30f, tween(150, easing = FastOutLinearInEasing))
                scrollAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )

                // Center symbol pop
                popAnim.snapTo(0.82f)
                popAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
            }

            SpinPhase.IDLE -> {
                scrollAnim.snapTo(0f)
                popAnim.snapTo(1f)
                strip = randomStrip(targetGod)
            }

            SpinPhase.RESULT -> { /* hold current display */ }
        }
    }

    // ── Visual style ──────────────────────────────────────────────────────────

    val accentColor = when (targetGod) {
        God.ZEUS     -> ZeusYellow
        God.POSEIDON -> PoseidonBlue
        God.HADES    -> HadesPurple
        God.ARTEMIS  -> ArtemisGreen
        God.APOLLO   -> ApolloOrange
        God.DEMETER  -> DemeterGreen
        God.HERMES   -> HermesGray
    }
    val borderColor = if (isWinning) accentColor else ReelBorder
    val shape = RoundedCornerShape(12.dp)

    // ── Layout ────────────────────────────────────────────────────────────────

    Box(
        modifier = modifier
            .width(105.dp)
            .height(REEL_HEIGHT)
            .shadow(
                elevation = if (isWinning) 18.dp else 8.dp,
                shape = shape,
                ambientColor = if (isWinning) borderColor.copy(alpha = 0.6f) else Color.Black,
                spotColor  = if (isWinning) borderColor else Color.Black
            )
            .clip(shape)
            .background(ReelBackground)
            .border(
                width = if (isWinning) 3.dp else 2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        borderColor.copy(alpha = 0.8f),
                        borderColor,
                        borderColor.copy(alpha = 0.8f)
                    )
                ),
                shape = shape
            )
    ) {

        // ── Scrolling symbol strip ─────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(SYMBOL_H * 4f)        // 4-symbol strip
                .graphicsLayer {
                    // Strip[0] starts 1 slot above the viewport (off-screen).
                    // scrollAnim=0 → translationY = -SYMBOL_H (strip[0] hidden above)
                    // scrollAnim=1 → translationY = 0         (strip[0] at top edge)
                    translationY = -SYMBOL_H.toPx() + scrollAnim.value * SYMBOL_H.toPx()
                },
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            strip.forEachIndexed { index, god ->
                val isCenterSlot = index == 2
                val isWinningSymbol = isWinning && isCenterSlot && spinPhase == SpinPhase.RESULT

                // Size / alpha / stretch differ between spinning and stopped
                val imgSize = when {
                    isActivelySpinning -> 68.dp
                    isCenterSlot       -> 80.dp
                    else               -> 54.dp
                }
                val alpha = when {
                    isActivelySpinning -> 0.90f
                    isCenterSlot       -> 1.00f
                    else               -> 0.36f
                }
                // Slight vertical stretch while spinning → motion-blur illusion
                val scaleYMult = if (isActivelySpinning) 1.12f else 1f
                val popScale   = if (isCenterSlot && !isActivelySpinning) popAnim.value else 1f
                val finalPopScale = if (isWinningSymbol) popScale * 1.15f else popScale

                Box(
                    modifier = Modifier
                        .height(SYMBOL_H)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val baseModifier = Modifier.size(imgSize)
                    val winningModifier = if (isWinningSymbol) {
                        Modifier.shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = accentColor,
                            spotColor = accentColor
                        ).border(
                            width = 2.dp,
                            color = accentColor,
                            shape = RoundedCornerShape(12.dp)
                        ).background(
                            color = accentColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else Modifier

                    Image(
                        painter = painterResource(id = god.drawableRes),
                        contentDescription = god.displayName,
                        contentScale = ContentScale.Fit,
                        modifier = baseModifier
                            .then(winningModifier)
                            .graphicsLayer {
                                this.alpha  = alpha
                                this.scaleX = finalPopScale
                                this.scaleY = scaleYMult * finalPopScale
                            }
                            .padding(if (isWinningSymbol) 4.dp else 0.dp)
                    )
                }
            }
        }

        // ── Top & bottom fog (hides symbols entering / leaving the viewport)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(ReelBackground, ReelBackground.copy(alpha = 0.65f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, ReelBackground.copy(alpha = 0.65f), ReelBackground)
                    )
                )
        )

        // ── Winning: radial inner glow ─────────────────────────────────────
        if (isWinning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = glowAlpha * 0.20f }
                    .background(
                        Brush.radialGradient(listOf(accentColor, Color.Transparent))
                    )
            )

            // Sheen sweep — bright band travelling from top to bottom
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sweepY    = sheenPos * size.height
                val halfBand  = 65.dp.toPx()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.22f),
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        startY = sweepY - halfBand,
                        endY   = sweepY + halfBand
                    )
                )
            }
        }
    }
}
