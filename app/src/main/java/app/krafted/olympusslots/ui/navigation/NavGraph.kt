package app.krafted.olympusslots.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Slot : Screen("slot")
    object Win : Screen("win/{godName}/{coinsWon}")
    object Jackpot : Screen("jackpot")
    object DailyBonus : Screen("daily_bonus")
    object Leaderboard : Screen("leaderboard")
}
