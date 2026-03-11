package app.krafted.olympusslots.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.olympusslots.R
import app.krafted.olympusslots.ui.components.CoinShowerEffect
import app.krafted.olympusslots.ui.components.LightningEffect
import app.krafted.olympusslots.ui.theme.OlympusCream
import app.krafted.olympusslots.ui.theme.OlympusGold
import app.krafted.olympusslots.ui.theme.OlympusGoldDark
import app.krafted.olympusslots.ui.theme.OlympusGoldLight
import app.krafted.olympusslots.ui.theme.OlympusPurpleDeep
import app.krafted.olympusslots.ui.theme.ZeusYellow
import kotlinx.coroutines.delay

@Composable
fun JackpotOverlay(
    coinsWon: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFlash by remember { mutableStateOf(true) }
    var showBackground by remember { mutableStateOf(false) }
    var showZeus by remember { mutableStateOf(false) }
    var showJackpotText by remember { mutableStateOf(false) }
    var showCoins by remember { mutableStateOf(false) }
    var showClaim by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showFlash = false
        showBackground = true
        delay(300)
        showZeus = true
        delay(400)
        showJackpotText = true
        delay(300)
        showCoins = true
        delay(3000)
        showClaim = true
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible = showBackground,
            enter = fadeIn(tween(600))
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_jackpot),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            0.6f to Color(0x40000000),
                            1f to Color(0xCC000000)
                        )
                    )
                )
        )

        if (showBackground) {
            LightningEffect()
        }

        if (showCoins) {
            CoinShowerEffect()
        }

        AnimatedVisibility(
            visible = showFlash,
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.8f))

            AnimatedVisibility(
                visible = showZeus,
                enter = scaleIn(
                    initialScale = 0.3f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(tween(400))
            ) {
                ZeusPortrait()
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showJackpotText,
                enter = scaleIn(
                    initialScale = 3f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(tween(300))
            ) {
                JackpotTitle()
            }

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = showCoins,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 }
            ) {
                CoinCounter(targetAmount = coinsWon)
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = showClaim,
                enter = fadeIn(tween(500)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            ) {
                ClaimButton(onClick = onDismiss)
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun ZeusPortrait() {
    val infiniteTransition = rememberInfiniteTransition(label = "zeusGlow")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "zeusRingRot"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zeusGlowPulse"
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .graphicsLayer { alpha = glowPulse * 0.4f }
                .blur(24.dp)
                .clip(CircleShape)
                .background(ZeusYellow)
        )

        Canvas(
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer { rotationZ = ringRotation }
        ) {
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        ZeusYellow,
                        Color.Transparent,
                        OlympusGold,
                        Color.Transparent,
                        ZeusYellow,
                        Color.Transparent,
                        OlympusGoldLight,
                        Color.Transparent
                    )
                ),
                radius = size.minDimension / 2,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Image(
            painter = painterResource(id = R.drawable.god_zeus),
            contentDescription = "Zeus - King of Gods",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    brush = Brush.verticalGradient(
                        listOf(OlympusGoldLight, OlympusGold, OlympusGoldDark)
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun JackpotTitle() {
    val infiniteTransition = rememberInfiniteTransition(label = "jackpotTitle")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "titleShimmer"
    )

    Box(contentAlignment = Alignment.Center) {

        Text(
            text = "JACKPOT!",
            style = TextStyle(
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = ZeusYellow.copy(alpha = 0.5f)
            ),
            modifier = Modifier.blur(16.dp)
        )
        Text(
            text = "JACKPOT!",
            style = TextStyle(
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                brush = Brush.verticalGradient(
                    listOf(Color.White, OlympusGoldLight, OlympusGold, OlympusGoldDark)
                ),
                shadow = Shadow(
                    color = ZeusYellow.copy(alpha = 0.8f),
                    offset = Offset(0f, 4f),
                    blurRadius = 16f
                )
            ),
            textAlign = TextAlign.Center
        )
    }

    Text(
        text = "Zeus - King of Gods",
        color = OlympusCream.copy(alpha = 0.8f),
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 3.sp
    )
}

@Composable
private fun CoinCounter(targetAmount: Int) {
    val animatedAmount by animateIntAsState(
        targetValue = targetAmount,
        animationSpec = tween(1500, easing = EaseOutCubic),
        label = "jackpotCoins"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "+%,d".format(animatedAmount),
            style = TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                brush = Brush.verticalGradient(
                    listOf(OlympusGoldLight, OlympusGold)
                ),
                shadow = Shadow(
                    color = OlympusGold.copy(alpha = 0.6f),
                    offset = Offset(0f, 3f),
                    blurRadius = 12f
                )
            )
        )
        Text(
            text = "COINS",
            color = OlympusCream.copy(alpha = 0.6f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp
        )
    }
}

@Composable
private fun ClaimButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "claimBtn")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "claimPulse"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "claimPress"
    )

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pulseScale * pressScale
                scaleY = pulseScale * pressScale
            }
            .width(200.dp)
            .height(56.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(OlympusGoldLight, OlympusGold, OlympusGoldDark)
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.5f), OlympusGold.copy(alpha = 0.3f))
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CLAIM",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = OlympusPurpleDeep,
                shadow = Shadow(
                    color = OlympusGold.copy(alpha = 0.3f),
                    offset = Offset(0f, 1f),
                    blurRadius = 2f
                )
            )
        )
    }
}
