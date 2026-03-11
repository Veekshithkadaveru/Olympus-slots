package app.krafted.olympusslots.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OlympusColorScheme = darkColorScheme(
    primary = OlympusGold,
    onPrimary = OlympusPurpleDeep,
    primaryContainer = OlympusGoldDark,
    onPrimaryContainer = OlympusCream,
    secondary = OlympusBronze,
    onSecondary = Color.White,
    secondaryContainer = OlympusPurple,
    onSecondaryContainer = OlympusGoldLight,
    tertiary = PoseidonBlue,
    onTertiary = Color.White,
    background = OlympusPurpleDeep,
    onBackground = OlympusCream,
    surface = OlympusPurple,
    onSurface = OlympusCream,
    surfaceVariant = Color(0xFF2A1F4E),
    onSurfaceVariant = Color(0xFFD4C4A8)
)

@Composable
fun OLYMPUSSLOTSTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = OlympusColorScheme,
        typography = Typography,
        content = content
    )
}
