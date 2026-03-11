package app.krafted.olympusslots.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_data")
data class PlayerData(
    @PrimaryKey val id: Int = 1,
    val coinBalance: Int = 500,
    val lastBonusClaim: Long = 0L,
    val totalSpins: Int = 0,
    val currentWinStreak: Int = 0,
    val bestWinStreak: Int = 0,
    val totalWins: Int = 0,
    val jackpotCount: Int = 0
)
