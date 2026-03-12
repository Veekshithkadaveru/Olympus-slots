package app.krafted.olympusslots

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import app.krafted.olympusslots.ui.navigation.OlympusNavGraph
import app.krafted.olympusslots.ui.theme.OLYMPUSSLOTSTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { keepSplashOpened }
        
        lifecycleScope.launch {

            keepSplashOpened = false
        }

        enableEdgeToEdge()
        setContent {
            OLYMPUSSLOTSTheme {
                OlympusNavGraph()
            }
        }
    }
}
