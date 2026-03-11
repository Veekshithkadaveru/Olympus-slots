package app.krafted.olympusslots.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import app.krafted.olympusslots.game.God
import app.krafted.olympusslots.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun DailyBonusScreen(
    coinBalance: Int,
    dailyBonusAvailable: Boolean,
    lastBonusClaim: Long,
    onClaim: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val blessingGod = remember { God.entries.random() }
    var claimed by remember { mutableStateOf(false) }
    var showCoinAnimation by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_jackpot),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0xE01A0E3E),
                            0.3f to Color(0x801A0E3E),
                            0.5f to Color(0x401A0E3E),
                            0.7f to Color(0x801A0E3E),
                            1f to Color(0xF01A0E3E)
                        )
                    )
                )
        )

        GoldenGlow(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.Center)
                .offset(y = (-60).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            BackButton(onClick = onNavigateBack)

            Spacer(modifier = Modifier.height(24.dp))

            BlessingTitle()

            Spacer(modifier = Modifier.height(32.dp))

            GodPortrait(god = blessingGod)

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedVisibility(visible = showCoinAnimation) {
                BonusCoinCounter(targetAmount = 100)
            }

            Spacer(modifier = Modifier.weight(1f))

            if (dailyBonusAvailable && !claimed) {
                ClaimBonusButton(
                    onClick = {
                        onClaim()
                        claimed = true
                        showCoinAnimation = true
                    }
                )
            } else {
                if (claimed) {
                    Text(
                        text = "Blessing received!",
                        color = OlympusGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                } else {
                    CooldownTimer(lastBonusClaim = lastBonusClaim)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun GoldenGlow(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = modifier
            .graphicsLayer { alpha = glowAlpha }
            .background(
                Brush.radialGradient(colors = listOf(OlympusGold, Color.Transparent))
            )
    )
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "backScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(shape)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1E1250), Color(0xFF130D35))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(OlympusGoldDark.copy(alpha = 0.5f), OlympusGoldDark.copy(alpha = 0.2f))
                    ),
                    shape = shape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\u2190  OLYMPUS",
                color = OlympusCream.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun BlessingTitle() {
    val infiniteTransition = rememberInfiniteTransition(label = "blessingTitle")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleGlow"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "DAILY BLESSING",
                style = TextStyle(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    color = OlympusGold.copy(alpha = glowAlpha * 0.4f)
                ),
                modifier = Modifier.blur(12.dp)
            )
            Text(
                text = "DAILY BLESSING",
                style = TextStyle(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    brush = Brush.verticalGradient(
                        listOf(OlympusGoldLight, OlympusGold, OlympusGoldDark)
                    ),
                    shadow = Shadow(
                        color = OlympusGold.copy(alpha = 0.5f),
                        offset = Offset(0f, 4f),
                        blurRadius = 12f
                    )
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "The gods bestow their favour",
            color = OlympusCream.copy(alpha = 0.7f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 3.sp
        )
    }
}

@Composable
private fun GodPortrait(god: God) {
    val infiniteTransition = rememberInfiniteTransition(label = "godGlow")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "godRingRot"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "godGlowPulse"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer { alpha = glowPulse * 0.35f }
                    .blur(24.dp)
                    .clip(CircleShape)
                    .background(OlympusGold)
            )

            Canvas(
                modifier = Modifier
                    .size(170.dp)
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
                            Color.Transparent,
                            OlympusGoldDark,
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 4.dp.toPx())
                )
            }

            Image(
                painter = painterResource(id = god.drawableRes),
                contentDescription = god.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
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

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = god.displayName,
            color = OlympusCream.copy(alpha = 0.9f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )
        Text(
            text = god.domain,
            color = OlympusGold.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun BonusCoinCounter(targetAmount: Int) {
    val animatedAmount by animateIntAsState(
        targetValue = targetAmount,
        animationSpec = tween(1500, easing = EaseOutCubic),
        label = "bonusCoins"
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
private fun ClaimBonusButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "claimBonus")
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
            .width(220.dp)
            .height(58.dp)
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

@Composable
private fun CooldownTimer(lastBonusClaim: Long) {
    val cooldownMs = 86_400_000L
    var remainingMs by remember { mutableStateOf(cooldownMs - (System.currentTimeMillis() - lastBonusClaim)) }

    LaunchedEffect(lastBonusClaim) {
        while (remainingMs > 0) {
            remainingMs = cooldownMs - (System.currentTimeMillis() - lastBonusClaim)
            delay(1000)
        }
    }

    val totalSeconds = (remainingMs.coerceAtLeast(0) / 1000).toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val timeText = "%02d:%02d:%02d".format(hours, minutes, seconds)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Return in",
            color = OlympusCream.copy(alpha = 0.5f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        val shape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .clip(shape)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1E1250), Color(0xFF251560), Color(0xFF1E1250))
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            OlympusGoldDark.copy(alpha = 0.3f),
                            OlympusGold.copy(alpha = 0.5f),
                            OlympusGoldDark.copy(alpha = 0.3f)
                        )
                    ),
                    shape = shape
                )
                .padding(horizontal = 32.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timeText,
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    brush = Brush.verticalGradient(
                        listOf(OlympusGoldLight, OlympusGold)
                    ),
                    shadow = Shadow(
                        color = OlympusGold.copy(alpha = 0.4f),
                        blurRadius = 8f
                    )
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
