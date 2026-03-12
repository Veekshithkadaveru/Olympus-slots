package app.krafted.olympusslots.game

object GameConstants {
    const val STARTING_BALANCE = 500
    const val SPIN_COST = 30
    const val DAILY_BONUS = 100
    const val DAILY_BONUS_COOLDOWN_MS = 24 * 60 * 60 * 1000L
    const val COIN_FLOOR = 20
    const val WIN_STREAK_THRESHOLD = 5
    const val WIN_STREAK_BONUS = 75
    const val MAX_HERMES_RESHUFFLES = 3
    const val MAX_SPINS_PER_SESSION = 30

    // Reel animation timing
    const val REEL_1_STOP_MS = 600L
    const val REEL_2_STOP_MS = 800L
    const val REEL_3_STOP_MS = 1000L
}
