package app.krafted.olympusslots.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.olympusslots.game.God
import app.krafted.olympusslots.game.GodPower
import app.krafted.olympusslots.ui.theme.*
import kotlinx.coroutines.delay

private fun God.accentColor(): Color = when (this) {
    God.ZEUS -> ZeusYellow
    God.POSEIDON -> PoseidonBlue
    God.HADES -> HadesPurple
    God.ARTEMIS -> ArtemisGreen
    God.APOLLO -> ApolloOrange
    God.DEMETER -> DemeterGreen
    God.HERMES -> HermesGray
}

@Composable
fun WinOverlayThreeOfAKind(
    god: God,
    payout: Int,
    godPower: GodPower?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showContent by remember { mutableStateOf(false) }
    var showPortrait by remember { mutableStateOf(false) }
    var showPayout by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
        delay(200)
        showPortrait = true
        delay(300)
        showPayout = true
        delay(600)
        showButton = true
        delay(2400)
        onDismiss()
    }

    val accent = god.accentColor()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xDD000000))
            )
        }

        Column(
            modifier = Modifier.systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showPortrait,
                enter = scaleIn(
                    initialScale = 0.3f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(tween(300))
            ) {
                GodPortrait(god = god, accent = accent)
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showPortrait,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = god.displayName.uppercase(),
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            brush = Brush.verticalGradient(
                                listOf(Color.White, accent, accent.copy(alpha = 0.7f))
                            ),
                            shadow = Shadow(
                                color = accent.copy(alpha = 0.6f),
                                offset = Offset(0f, 3f),
                                blurRadius = 12f
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = god.domain,
                        color = OlympusCream.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = showPayout,
                enter = fadeIn(tween(400)) + scaleIn(
                    initialScale = 0.5f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            ) {
                WinCoinCounter(targetAmount = payout, accent = accent)
            }

            val powerText = when (godPower) {
                is GodPower.FreeSpins -> "+${godPower.count} Free Spins!"
                is GodPower.AllWilds -> "Next spin FREE!"
                is GodPower.DoubleNextSpin -> "Next spin x2!"
                is GodPower.DailyBonusSpin -> "Bonus Spin unlocked!"
                is GodPower.Reshuffle -> "Free reshuffle!"
                else -> null
            }

            if (powerText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedVisibility(
                    visible = showPayout,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(300)) { it / 2 }
                ) {
                    val powerShape = RoundedCornerShape(12.dp)
                    Text(
                        text = "  $powerText  ",
                        color = accent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .clip(powerShape)
                            .background(accent.copy(alpha = 0.1f))
                            .border(1.dp, accent.copy(alpha = 0.4f), powerShape)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(400)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            ) {
                SpinAgainButton(accent = accent, onClick = onDismiss)
            }
        }
    }
}

@Composable
fun WinOverlayTwoOfAKind(
    payout: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        showContent = true
        delay(1450)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
            )
        }

        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(
                initialScale = 0.5f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(tween(200))
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "minorWinGlow")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "minorGlow"
            )

            Text(
                text = "+$payout",
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    brush = Brush.verticalGradient(
                        listOf(OlympusGoldLight, OlympusGold)
                    ),
                    shadow = Shadow(
                        color = OlympusGold.copy(alpha = 0.8f),
                        offset = Offset(0f, 4f),
                        blurRadius = 20f
                    )
                ),
                modifier = Modifier.graphicsLayer { alpha = glowAlpha }
            )
        }
    }
}

@Composable
private fun GodPortrait(god: God, accent: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "godGlow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "godGlowPulse"
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer { alpha = glowPulse * 0.4f }
                .blur(20.dp)
                .clip(CircleShape)
                .background(accent)
        )

        Image(
            painter = painterResource(id = god.drawableRes),
            contentDescription = "${god.displayName} - ${god.domain}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    brush = Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.9f), OlympusGold, accent.copy(alpha = 0.7f))
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun WinCoinCounter(targetAmount: Int, accent: Color) {
    val animatedAmount by animateIntAsState(
        targetValue = targetAmount,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "winCoins"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "+$animatedAmount",
            style = TextStyle(
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                brush = Brush.verticalGradient(
                    listOf(OlympusGoldLight, OlympusGold)
                ),
                shadow = Shadow(
                    color = accent.copy(alpha = 0.5f),
                    offset = Offset(0f, 3f),
                    blurRadius = 12f
                )
            )
        )
        Text(
            text = "COINS",
            color = OlympusCream.copy(alpha = 0.5f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 6.sp
        )
    }
}

@Composable
private fun SpinAgainButton(accent: Color, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "spinAgainBtn")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spinAgainPulse"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "spinAgainPress"
    )

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pulseScale * pressScale
                scaleY = pulseScale * pressScale
            }
            .width(180.dp)
            .height(50.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(OlympusGoldLight, OlympusGold, OlympusGoldDark)
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.5f), accent.copy(alpha = 0.3f))
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
            text = "SPIN AGAIN",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
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
