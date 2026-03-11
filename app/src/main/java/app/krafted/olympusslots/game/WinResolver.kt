package app.krafted.olympusslots.game

sealed class WinResult {
    data class ThreeOfAKind(val god: God, val payout: Int) : WinResult()
    data class TwoOfAKind(val god: God, val payout: Int) : WinResult()
    data object NoMatch : WinResult()
}

object WinResolver {
    fun resolve(r1: God, r2: God, r3: God): WinResult {
        val reels = listOf(r1, r2, r3)
        val nonWild = reels.filter { it != God.HADES }

        if (nonWild.isEmpty()) return WinResult.ThreeOfAKind(God.HADES, getFullPayout(God.HADES))

        val majority = nonWild.groupingBy { it }.eachCount().maxByOrNull { it.value }
        if (majority != null) {
            val matchGod = majority.key
            val matchCount = majority.value + reels.count { it == God.HADES }
            if (matchCount >= 3) return WinResult.ThreeOfAKind(matchGod, getFullPayout(matchGod))
            if (matchCount >= 2) return WinResult.TwoOfAKind(matchGod, 15)
        }

        return WinResult.NoMatch
    }

    private fun getFullPayout(god: God): Int = when (god) {
        God.ZEUS -> 500
        God.POSEIDON -> 200
        God.HADES -> 150
        God.ARTEMIS -> 100
        God.APOLLO -> 100
        God.DEMETER -> 80
        God.HERMES -> 60
    }
}
