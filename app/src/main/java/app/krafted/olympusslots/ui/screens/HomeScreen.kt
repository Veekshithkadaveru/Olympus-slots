package app.krafted.olympusslots.ui.screens

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
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
import app.krafted.olympusslots.R
import app.krafted.olympusslots.game.GameConstants
import app.krafted.olympusslots.game.God
import app.krafted.olympusslots.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    coinBalance: Int,
    dailyBonusAvailable: Boolean,
    onNavigateToSlot: () -> Unit,
    onNavigateToDailyBonus: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Layered background
        Image(
            painter = painterResource(id = R.drawable.bg_jackpot),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlays for depth
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

        // Floating golden particles
        GoldenParticles()

        // Radial light behind center
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.Center)
                .offset(y = (-40).dp)
                .graphicsLayer { alpha = 0.15f }
                .background(
                    Brush.radialGradient(
                        colors = listOf(OlympusGold, Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Ornamental top divider
            OrnamentalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            // Title section
            OlympusTitle()

            Spacer(modifier = Modifier.height(8.dp))

            OrnamentalDivider()

            Spacer(modifier = Modifier.height(24.dp))

            // Coin balance - premium style
            PremiumCoinDisplay(balance = coinBalance)

            Spacer(modifier = Modifier.height(28.dp))

            // Featured god showcase
            GodShowcase()

            Spacer(modifier = Modifier.weight(1f))

            // Main play button
            EpicPlayButton(onClick = onNavigateToSlot)

            Spacer(modifier = Modifier.weight(0.6f))

            // Bottom nav
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumNavCard(
                    icon = "\uD83C\uDF81",
                    label = "DAILY BONUS",
                    subtitle = if (dailyBonusAvailable) "CLAIM NOW" else "Claimed",
                    accentColor = if (dailyBonusAvailable) OlympusGold else OlympusCream.copy(alpha = 0.4f),
                    hasBadge = dailyBonusAvailable,
                    onClick = onNavigateToDailyBonus,
                    modifier = Modifier.weight(1f)
                )
                PremiumNavCard(
                    icon = "\uD83C\uDFC6",
                    label = "HALL OF FAME",
                    subtitle = "Top 10",
                    accentColor = OlympusBronze,
                    hasBadge = false,
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// -- Floating golden particle effect --
@Composable
private fun GoldenParticles() {
    data class Particle(val xFrac: Float, val yStart: Float, val speed: Float, val size: Float, val alpha: Float)

    val particles = remember {
        List(25) {
            Particle(
                xFrac = Math.random().toFloat(),
                yStart = Math.random().toFloat(),
                speed = 0.3f + Math.random().toFloat() * 0.7f,
                size = 1.5f + Math.random().toFloat() * 3f,
                alpha = 0.2f + Math.random().toFloat() * 0.5f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val y = ((p.yStart + time * p.speed) % 1.1f) * size.height
            val x = p.xFrac * size.width + sin((time + p.xFrac) * 2 * PI.toFloat()) * 20f
            drawCircle(
                color = OlympusGold.copy(alpha = p.alpha),
                radius = p.size.dp.toPx(),
                center = Offset(x, size.height - y)
            )
        }
    }
}

// -- Ornamental gold divider --
@Composable
private fun OrnamentalDivider() {
    val infiniteTransition = rememberInfiniteTransition(label = "divider")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dividerShimmer"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(3.dp)
    ) {
        val w = size.width
        val center = size.height / 2
        // Base line
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    OlympusGoldDark.copy(alpha = 0.6f),
                    OlympusGold.copy(alpha = 0.8f),
                    OlympusGoldDark.copy(alpha = 0.6f),
                    Color.Transparent
                )
            ),
            start = Offset(0f, center),
            end = Offset(w, center),
            strokeWidth = 1.5f
        )
        // Diamond center
        drawCircle(
            color = OlympusGold,
            radius = 3.dp.toPx(),
            center = Offset(w / 2, center)
        )
        // Shimmer
        val shimmerX = shimmer * w
        drawCircle(
            color = OlympusGoldLight.copy(alpha = 0.6f),
            radius = 12.dp.toPx(),
            center = Offset(shimmerX, center)
        )
    }
}

// -- Title with shadow and glow --
@Composable
private fun OlympusTitle() {
    val infiniteTransition = rememberInfiniteTransition(label = "title")
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
        // Glow layer behind title
        Box(contentAlignment = Alignment.Center) {
            // Blurred glow
            Text(
                text = "OLYMPUS",
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 10.sp,
                    color = OlympusGold.copy(alpha = glowAlpha * 0.4f)
                ),
                modifier = Modifier.blur(12.dp)
            )
            // Main text with shadow
            Text(
                text = "OLYMPUS",
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 10.sp,
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
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "S  L  O  T  S",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 14.sp,
                color = OlympusCream.copy(alpha = 0.85f),
                shadow = Shadow(
                    color = OlympusGold.copy(alpha = 0.3f),
                    offset = Offset(0f, 2f),
                    blurRadius = 8f
                )
            )
        )
    }
}

// -- Premium coin display --
@Composable
private fun PremiumCoinDisplay(balance: Int) {
    val animatedBalance by animateIntAsState(
        targetValue = balance,
        animationSpec = tween(500, easing = EaseOutCubic),
        label = "coinAnim"
    )

    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = Modifier
            .shadow(12.dp, shape, ambientColor = OlympusGold.copy(alpha = 0.2f))
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1A1040),
                        Color(0xFF251560),
                        Color(0xFF1A1040)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        OlympusGoldDark.copy(alpha = 0.4f),
                        OlympusGold.copy(alpha = 0.8f),
                        OlympusGoldDark.copy(alpha = 0.4f)
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 28.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coin icon circle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(OlympusGoldLight, OlympusGold, OlympusGoldDark)
                    )
                )
                .border(1.dp, OlympusGoldLight.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\u00A2",
                color = OlympusPurpleDeep,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "%,d".format(animatedBalance),
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                brush = Brush.verticalGradient(
                    listOf(OlympusGoldLight, OlympusGold)
                ),
                shadow = Shadow(
                    color = OlympusGold.copy(alpha = 0.4f),
                    blurRadius = 8f
                )
            )
        )
    }
}

// -- God showcase with circular arrangement --
@Composable
private fun GodShowcase() {
    val gods = God.entries.toList()
    val infiniteTransition = rememberInfiniteTransition(label = "godShowcase")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "godRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        // Center Zeus highlight
        Box(
            modifier = Modifier
                .size(90.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(OlympusGold.copy(alpha = 0.15f), Color.Transparent)
                        ),
                        radius = size.maxDimension
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.god_zeus),
                contentDescription = "Zeus",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(OlympusGold, OlympusGoldLight, OlympusGold, OlympusGoldDark, OlympusGold)
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Orbiting gods (excluding Zeus)
        val orbitGods = gods.filter { it != God.ZEUS }
        orbitGods.forEachIndexed { index, god ->
            val angle = rotation + (index * 360f / orbitGods.size)
            val radiusX = 130f
            val radiusY = 55f
            val rad = angle * PI.toFloat() / 180f
            val x = cos(rad) * radiusX
            val y = sin(rad) * radiusY
            // Depth: gods at top are further (smaller), at bottom are closer (bigger)
            val depthScale = 0.65f + 0.35f * ((sin(rad) + 1f) / 2f)
            val depthAlpha = 0.5f + 0.5f * ((sin(rad) + 1f) / 2f)

            Image(
                painter = painterResource(id = god.drawableRes),
                contentDescription = god.displayName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(x = x.dp, y = y.dp)
                    .graphicsLayer {
                        scaleX = depthScale
                        scaleY = depthScale
                        alpha = depthAlpha
                    }
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, OlympusGoldDark.copy(alpha = 0.6f), CircleShape)
            )
        }
    }
}

// -- Circular casino-style play button --
@Composable
private fun EpicPlayButton(onClick: () -> Unit) {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val debouncedClick = {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > 500L) {
            lastClickTime = now
            onClick()
        }
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "epicBtn")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "epicPulse"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRot"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "epicGlow"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )

    val buttonSize = 150.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(buttonSize + 40.dp)
        ) {
            // Outermost soft glow
            Box(
                modifier = Modifier
                    .size(buttonSize + 30.dp)
                    .graphicsLayer { alpha = glowAlpha * 0.25f }
                    .blur(20.dp)
                    .clip(CircleShape)
                    .background(OlympusGold)
            )

            // Rotating outer ring with sweep gradient
            Canvas(
                modifier = Modifier
                    .size(buttonSize + 16.dp)
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
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                )
            }

            // Static middle ring
            Box(
                modifier = Modifier
                    .size(buttonSize + 8.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                OlympusGoldLight.copy(alpha = 0.9f),
                                OlympusGoldDark.copy(alpha = 0.6f),
                                OlympusBronze.copy(alpha = 0.8f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Main circular button
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .graphicsLayer {
                        scaleX = pulseScale * pressScale
                        scaleY = pulseScale * pressScale
                    }
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        ambientColor = OlympusGold.copy(alpha = 0.4f),
                        spotColor = OlympusGold
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to Color(0xFFFFF5CC),
                                0.3f to OlympusGold,
                                0.7f to OlympusGoldDark,
                                1f to Color(0xFF8B6914)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = debouncedClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner highlight crescent at top
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 12.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SPIN",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 5.sp,
                            color = OlympusPurpleDeep,
                            shadow = Shadow(
                                color = OlympusGold.copy(alpha = 0.5f),
                                offset = Offset(0f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(1.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, OlympusPurpleDeep.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "THE REELS",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = OlympusPurpleDeep.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cost label below the circle
        Text(
            text = "${GameConstants.SPIN_COST} coins per spin",
            color = OlympusCream.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 1.sp
        )
    }
}

// -- Premium nav card --
@Composable
private fun PremiumNavCard(
    icon: String,
    label: String,
    subtitle: String,
    accentColor: Color,
    hasBadge: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    val shape = RoundedCornerShape(20.dp)

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(12.dp, shape, ambientColor = accentColor.copy(alpha = 0.15f))
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E1250),
                            Color(0xFF130D35)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.6f),
                            accentColor.copy(alpha = 0.2f)
                        )
                    ),
                    shape = shape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = OlympusCream,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 11.sp,
                fontWeight = if (hasBadge) FontWeight.Bold else FontWeight.Normal,
                letterSpacing = 1.sp
            )
        }

        // Pulsing badge
        if (hasBadge) {
            val infiniteTransition = rememberInfiniteTransition(label = "navBadge")
            val badgeScale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "badgePulse"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = (-4).dp)
                    .graphicsLayer {
                        scaleX = badgeScale
                        scaleY = badgeScale
                    }
                    .size(14.dp)
                    .shadow(4.dp, CircleShape, ambientColor = OlympusGold)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(OlympusGoldLight, OlympusGold))
                    )
                    .border(1.dp, OlympusPurpleDeep, CircleShape)
            )
        }
    }
}
