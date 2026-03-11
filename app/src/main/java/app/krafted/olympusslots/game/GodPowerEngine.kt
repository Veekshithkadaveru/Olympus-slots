package app.krafted.olympusslots.game

sealed class GodPower {
    data object Jackpot : GodPower()
    data class FreeSpins(val count: Int) : GodPower()
    data object AllWilds : GodPower()
    data object None : GodPower()
    data object DoubleNextSpin : GodPower()
    data object DailyBonusSpin : GodPower()
    data object Reshuffle : GodPower()
}

object GodPowerEngine {
    fun getGodPower(god: God): GodPower = when (god) {
        God.ZEUS -> GodPower.Jackpot
        God.POSEIDON -> GodPower.FreeSpins(3)
        God.HADES -> GodPower.AllWilds
        God.ARTEMIS -> GodPower.None
        God.APOLLO -> GodPower.DoubleNextSpin
        God.DEMETER -> GodPower.DailyBonusSpin
        God.HERMES -> GodPower.Reshuffle
    }
}
