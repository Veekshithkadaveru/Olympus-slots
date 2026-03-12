package app.krafted.olympusslots.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.olympusslots.game.*
import app.krafted.olympusslots.ui.components.*
import app.krafted.olympusslots.ui.theme.*
import app.krafted.olympusslots.viewmodel.SlotUiState
import app.krafted.olympusslots.viewmodel.SpinPhase

@Composable
fun SlotScreen(
    uiState: SlotUiState,
    onSpin: () -> Unit,
    onResetToIdle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWin = uiState.winResult != null && uiState.winResult !is WinResult.NoMatch
    val winningGod = when (val result = uiState.winResult) {
        is WinResult.ThreeOfAKind -> result.god
        is WinResult.TwoOfAKind -> result.god
        else -> null
    }
    val isThreeOfAKind = uiState.winResult is WinResult.ThreeOfAKind
    val isZeusJackpot = uiState.winResult is WinResult.ThreeOfAKind &&
            (uiState.winResult as WinResult.ThreeOfAKind).god == God.ZEUS

    var showJackpotOverlay by remember { mutableStateOf(false) }
    var showWinOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.spinPhase) {
        if (uiState.spinPhase == SpinPhase.RESULT) {
            when {
                // Skip overlays during Hermes auto-reshuffle (VM handles the flow)
                uiState.isAutoReshuffling -> { /* VM will auto-trigger next spin */ }
                isZeusJackpot -> {
                    kotlinx.coroutines.delay(500L)
                    showJackpotOverlay = true
                }
                isThreeOfAKind -> {
                    kotlinx.coroutines.delay(500L)
                    showWinOverlay = true
                }
                else -> {
                    // 2-of-a-kind and no-match auto-dismiss
                    kotlinx.coroutines.delay(2500L)
                    onResetToIdle()
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        BackgroundScene(background = uiState.currentBackground) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top bar: Title + Coin display
                TopBar(
                    coinBalance = uiState.coinBalance,
                    winStreak = uiState.winStreak
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Apollo double indicator
                AnimatedVisibility(
                    visible = uiState.apolloDoubleActive,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    ApolloDoubleBanner()
                }

                Spacer(modifier = Modifier.weight(1f))

                // God name banner
                GodBannerText(uiState = uiState)

                Spacer(modifier = Modifier.height(12.dp))

                // Win line (top)
                WinLineHighlight(
                    isVisible = isWin && uiState.spinPhase == SpinPhase.RESULT,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // The 3 Reels
                ReelRow(
                    reelSymbols = uiState.reelSymbols,
                    spinPhase = uiState.spinPhase,
                    winningGod = winningGod,
                    isThreeOfAKind = isThreeOfAKind
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Win line (bottom)
                WinLineHighlight(
                    isVisible = isWin && uiState.spinPhase == SpinPhase.RESULT,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Win result text
                WinResultDisplay(uiState = uiState)

                Spacer(modifier = Modifier.weight(1f))

                // Free spins indicator
                AnimatedVisibility(
                    visible = uiState.freeSpinsRemaining > 0,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    FreeSpinsBadge(count = uiState.freeSpinsRemaining)
                }

                // Low balance warning
                AnimatedVisibility(
                    visible = uiState.isLowBalance && uiState.spinPhase == SpinPhase.IDLE,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut()
                ) {
                    LowBalanceWarning()
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Spin button
                SpinButton(
                    isFree = uiState.freeSpinsRemaining > 0,
                    isEnabled = uiState.spinPhase == SpinPhase.IDLE &&
                            (uiState.freeSpinsRemaining > 0 || uiState.coinBalance >= GameConstants.SPIN_COST),
                    spinCost = GameConstants.SPIN_COST,
                    onClick = onSpin
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Win overlay (3-of-a-kind only, excludes Zeus jackpot)
        AnimatedVisibility(
            visible = showWinOverlay,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(300))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // God power particle effects ONLY for 3-of-a-kind
                if (winningGod != null && isThreeOfAKind) {
                    GodPowerOverlay(god = winningGod, modifier = Modifier.fillMaxSize())
                }
                val dismissWin = {
                    showWinOverlay = false
                    onResetToIdle()
                }
                val result = uiState.winResult
                if (result is WinResult.ThreeOfAKind) {
                    WinOverlayThreeOfAKind(
                        god = result.god,
                        payout = result.payout,
                        godPower = uiState.lastGodPower,
                        onDismiss = dismissWin
                    )
                }
            }
        }

        // Jackpot overlay (Zeus 3-of-a-kind)
        AnimatedVisibility(
            visible = showJackpotOverlay,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(400))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                GodPowerOverlay(god = God.ZEUS, modifier = Modifier.fillMaxSize())
                JackpotOverlay(
                    coinsWon = (uiState.winResult as? WinResult.ThreeOfAKind)?.payout ?: 500,
                    onDismiss = {
                        showJackpotOverlay = false
                        onResetToIdle()
                    }
                )
            }
        }
    }
}

@Composable
private fun TopBar(coinBalance: Int, winStreak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title
        Text(
            text = "OLYMPUS",
            color = OlympusGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp
        )

        CoinDisplay(balance = coinBalance)
    }

    // Win streak
    if (winStreak > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            val streakShape = RoundedCornerShape(12.dp)
            Text(
                text = "  $winStreak streak  ",
                color = OlympusGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(streakShape)
                    .background(SemiTransparentBlack)
                    .border(1.dp, OlympusGoldDark.copy(alpha = 0.5f), streakShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun ReelRow(
    reelSymbols: List<God>,
    spinPhase: SpinPhase,
    winningGod: God?,
    isThreeOfAKind: Boolean
) {
    val reelShape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(reelShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0A20),
                        Color(0xFF151535),
                        Color(0xFF0A0A20)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    listOf(OlympusGold.copy(alpha = 0.6f), OlympusBronze, OlympusGold.copy(alpha = 0.6f))
                ),
                shape = reelShape
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val stopDelays = listOf(
                GameConstants.REEL_1_STOP_MS,
                GameConstants.REEL_2_STOP_MS,
                GameConstants.REEL_3_STOP_MS
            )

            reelSymbols.forEachIndexed { index, god ->
                val isWinningReel = spinPhase == SpinPhase.RESULT &&
                        winningGod != null &&
                        (god == winningGod || god == God.HADES)

                ReelColumn(
                    targetGod = god,
                    spinPhase = spinPhase,
                    stopDelay = stopDelays[index],
                    isWinning = isWinningReel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GodBannerText(uiState: SlotUiState) {
    data class BannerState(val text: String, val color: Color, val fontSize: Int)

    val bannerState = when (uiState.spinPhase) {
        SpinPhase.IDLE -> BannerState("Spin the Reels", OlympusCream, 16)
        SpinPhase.SPINNING -> BannerState("The gods decide...", OlympusCream, 16)
        SpinPhase.RESOLVING -> BannerState("Revealing fate...", OlympusCream, 16)
        SpinPhase.RESULT -> when (val result = uiState.winResult) {
            is WinResult.ThreeOfAKind -> BannerState(
                "${result.god.displayName} - ${result.god.domain}", OlympusGold, 20
            )
            is WinResult.TwoOfAKind -> BannerState(
                "Minor Win!", OlympusGoldLight, 16
            )
            is WinResult.NoMatch -> BannerState("No favour this time", Color(0xFFBBBBBB), 16)
            null -> BannerState("", OlympusCream, 16)
        }
    }

    Crossfade(
        targetState = bannerState,
        animationSpec = tween(300),
        label = "bannerText"
    ) { state ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = state.text,
                color = state.color,
                fontSize = state.fontSize.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun WinResultDisplay(uiState: SlotUiState) {
    AnimatedVisibility(
        visible = uiState.spinPhase == SpinPhase.RESULT && uiState.winResult !is WinResult.NoMatch,
        enter = fadeIn() + scaleIn(initialScale = 0.5f),
        exit = fadeOut()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            val payout = when (val result = uiState.winResult) {
                is WinResult.ThreeOfAKind -> result.payout
                is WinResult.TwoOfAKind -> result.payout
                else -> 0
            }

            if (payout > 0) {
                val animatedPayout by animateIntAsState(
                    targetValue = payout,
                    animationSpec = tween(800, easing = EaseOutCubic),
                    label = "payout"
                )

                Text(
                    text = "+$animatedPayout",
                    color = OlympusGold,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                // God power description (only for 3-of-a-kind)
                val powerText = when (uiState.lastGodPower) {
                    is GodPower.Jackpot -> "JACKPOT!"
                    is GodPower.FreeSpins -> "+${(uiState.lastGodPower as GodPower.FreeSpins).count} Free Spins!"
                    is GodPower.AllWilds -> "Next spin FREE!"
                    is GodPower.DoubleNextSpin -> "Next spin x2!"
                    is GodPower.DailyBonusSpin -> "Bonus Spin unlocked!"
                    is GodPower.Reshuffle -> "Free reshuffle!"
                    else -> null
                }

                if (powerText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = powerText,
                        color = OlympusGoldLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }

}

@Composable
private fun ApolloDoubleBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "apolloBanner")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "apolloGlow"
    )

    val shape = RoundedCornerShape(8.dp)
    Text(
        text = "  x2 NEXT SPIN  ",
        color = OlympusPurpleDeep,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier
            .graphicsLayer { alpha = glowAlpha }
            .clip(shape)
            .background(
                Brush.horizontalGradient(listOf(ApolloOrange, OlympusGold, ApolloOrange))
            )
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
private fun FreeSpinsBadge(count: Int) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(PoseidonBlue.copy(alpha = 0.8f), Color(0xFF0097A7).copy(alpha = 0.8f))
                )
            )
            .border(1.dp, PoseidonBlue, shape)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$count FREE SPIN${if (count > 1) "S" else ""}",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun LowBalanceWarning() {
    val shape = RoundedCornerShape(12.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .clip(shape)
            .background(Color(0xCC1A0A00))
            .border(1.dp, OlympusGoldDark.copy(alpha = 0.5f), shape)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = "The gods' favour wanes...",
            color = OlympusGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Claim your Daily Blessing!",
            color = OlympusCream.copy(alpha = 0.7f),
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
    }
}
