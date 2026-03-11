package app.krafted.olympusslots

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.krafted.olympusslots.ui.navigation.OlympusNavGraph
import app.krafted.olympusslots.ui.theme.OLYMPUSSLOTSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OLYMPUSSLOTSTheme {
                OlympusNavGraph()
            }
        }
    }
}
