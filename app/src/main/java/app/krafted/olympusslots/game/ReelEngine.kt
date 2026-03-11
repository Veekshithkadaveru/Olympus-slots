package app.krafted.olympusslots.game

import app.krafted.olympusslots.R
import kotlin.random.Random

enum class Rarity { COMMON, UNCOMMON, RARE, LEGENDARY }

enum class God(
    val displayName: String,
    val domain: String,
    val rarity: Rarity,
    val spawnWeight: Int,
    val drawableRes: Int
) {
    ZEUS("Zeus", "King of Gods", Rarity.LEGENDARY, 2, R.drawable.god_zeus),
    POSEIDON("Poseidon", "Sea & Storms", Rarity.RARE, 4, R.drawable.god_poseidon),
    HADES("Hades", "Underworld", Rarity.RARE, 5, R.drawable.god_hades),
    ARTEMIS("Artemis", "Hunt & Moon", Rarity.UNCOMMON, 10, R.drawable.god_artemis),
    APOLLO("Apollo", "Sun & Light", Rarity.UNCOMMON, 10, R.drawable.god_apollo),
    DEMETER("Demeter", "Harvest", Rarity.COMMON, 14, R.drawable.god_demeter),
    HERMES("Hermes", "Messenger", Rarity.COMMON, 15, R.drawable.god_hermes)
}

object ReelEngine {
    private val totalWeight = God.entries.sumOf { it.spawnWeight } // 60

    fun spinReel(): God {
        val roll = Random.nextInt(totalWeight)
        var cumulative = 0
        for (god in God.entries) {
            cumulative += god.spawnWeight
            if (roll < cumulative) return god
        }
        return God.HERMES
    }

    fun spinAllReels(): Triple<God, God, God> =
        Triple(spinReel(), spinReel(), spinReel())
}
