package app.krafted.olympusslots.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.krafted.olympusslots.data.AppDatabase
import app.krafted.olympusslots.ui.screens.DailyBonusScreen
import app.krafted.olympusslots.ui.screens.HomeScreen
import app.krafted.olympusslots.ui.screens.LeaderboardScreen
import app.krafted.olympusslots.ui.screens.SlotScreen
import app.krafted.olympusslots.viewmodel.CoinViewModel
import app.krafted.olympusslots.viewmodel.SlotViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Slot : Screen("slot")
    object DailyBonus : Screen("daily_bonus")
    object Leaderboard : Screen("leaderboard")
}

@Composable
fun OlympusNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val coinViewModel = remember { CoinViewModel(db.playerDao()) }
    val slotViewModel = remember { SlotViewModel(db.playerDao()) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { -it / 4 } },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 4 } }
    ) {
        composable(Screen.Splash.route) {
            app.krafted.olympusslots.ui.screens.SplashScreen(onSplashComplete = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            val coinBalance by coinViewModel.coinBalance.collectAsState()
            val dailyBonusAvailable by coinViewModel.dailyBonusAvailable.collectAsState()

            HomeScreen(
                coinBalance = coinBalance,
                dailyBonusAvailable = dailyBonusAvailable,
                onNavigateToSlot = { navController.navigate(Screen.Slot.route) },
                onNavigateToDailyBonus = { navController.navigate(Screen.DailyBonus.route) },
                onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) }
            )
        }

        composable(Screen.Slot.route) {
            val uiState by slotViewModel.uiState.collectAsState()

            DisposableEffect(Unit) {
                onDispose {
                    coinViewModel.recordScore(slotViewModel.uiState.value.coinBalance)
                }
            }

            SlotScreen(
                uiState = uiState,
                onSpin = { slotViewModel.spin() },
                onResetToIdle = { slotViewModel.resetToIdle() }
            )
        }

        composable(Screen.DailyBonus.route) {
            val coinBalance by coinViewModel.coinBalance.collectAsState()
            val dailyBonusAvailable by coinViewModel.dailyBonusAvailable.collectAsState()
            val playerData by coinViewModel.playerData.collectAsState()

            DailyBonusScreen(
                coinBalance = coinBalance,
                dailyBonusAvailable = dailyBonusAvailable,
                lastBonusClaim = playerData?.lastBonusClaim ?: 0L,
                onClaim = { coinViewModel.claimDailyBonus() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Leaderboard.route) {
            val topScores by coinViewModel.topScores.collectAsState()
            val coinBalance by coinViewModel.coinBalance.collectAsState()

            LeaderboardScreen(
                topScores = topScores,
                currentBalance = coinBalance,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
