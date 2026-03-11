package app.krafted.olympusslots.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.olympusslots.R
import app.krafted.olympusslots.data.LeaderboardEntry
import app.krafted.olympusslots.ui.theme.OlympusBronze
import app.krafted.olympusslots.ui.theme.OlympusCream
import app.krafted.olympusslots.ui.theme.OlympusGold
import app.krafted.olympusslots.ui.theme.OlympusGoldDark
import app.krafted.olympusslots.ui.theme.OlympusGoldLight
import app.krafted.olympusslots.ui.theme.OlympusPurpleDeep
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(
    topScores: List<LeaderboardEntry>,
    currentBalance: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .background(Color(0xCC1A0E3E))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "HALL OF OLYMPUS",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White, OlympusGoldLight, OlympusGold, OlympusGoldDark)
                    ),
                    shadow = Shadow(
                        color = OlympusGold.copy(alpha = 0.6f),
                        offset = Offset(0f, 3f),
                        blurRadius = 12f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (topScores.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "No scores yet",
                    color = OlympusCream.copy(alpha = 0.5f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(topScores) { index, entry ->
                        LeaderboardRow(rank = index + 1, entry = entry)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val currentShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(currentShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                OlympusPurpleDeep.copy(alpha = 0.8f),
                                Color(0xFF2D1B69).copy(alpha = 0.8f),
                                OlympusPurpleDeep.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .border(1.dp, OlympusGold.copy(alpha = 0.3f), currentShape)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Current: %,d coins".format(currentBalance),
                    color = OlympusGoldLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            BackButton(onClick = onNavigateBack)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LeaderboardRow(rank: Int, entry: LeaderboardEntry) {
    val rankColor = when (rank) {
        1 -> OlympusGold
        2 -> Color(0xFFC0C0C0)
        3 -> OlympusBronze
        else -> OlympusCream.copy(alpha = 0.7f)
    }

    val borderColor = when (rank) {
        1 -> OlympusGold.copy(alpha = 0.6f)
        2 -> Color(0xFFC0C0C0).copy(alpha = 0.4f)
        3 -> OlympusBronze.copy(alpha = 0.4f)
        else -> Color.White.copy(alpha = 0.1f)
    }

    val bgAlpha = when (rank) {
        1 -> 0.25f
        2 -> 0.18f
        3 -> 0.15f
        else -> 0.1f
    }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
    val formattedDate = remember(entry.date) { dateFormat.format(Date(entry.date)) }

    val rowShape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(OlympusPurpleDeep.copy(alpha = bgAlpha))
            .border(1.dp, borderColor, rowShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            color = rankColor,
            fontSize = if (rank <= 3) 20.sp else 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(48.dp)
        )

        Text(
            text = "%,d coins".format(entry.score),
            color = if (rank <= 3) rankColor else OlympusCream,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = formattedDate,
            color = OlympusCream.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .graphicsLayer {
                val s = if (isPressed) 0.94f else 1f
                scaleX = s
                scaleY = s
            }
            .width(160.dp)
            .height(48.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(OlympusGoldLight, OlympusGold, OlympusGoldDark)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.4f), OlympusGold.copy(alpha = 0.2f))
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
            text = "BACK",
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
