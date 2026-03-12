package app.krafted.olympusslots.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_data WHERE id = 1")
    fun getPlayerData(): Flow<PlayerData?>

    @Upsert
    suspend fun upsertPlayerData(data: PlayerData)

    @Query("UPDATE player_data SET coinBalance = :balance WHERE id = 1")
    suspend fun updateCoinBalance(balance: Int)

    @Query("UPDATE player_data SET lastBonusClaim = :timestamp WHERE id = 1")
    suspend fun updateLastBonusClaim(timestamp: Long)

    @Query("UPDATE player_data SET currentWinStreak = :streak WHERE id = 1")
    suspend fun updateWinStreak(streak: Int)

    @Query("UPDATE player_data SET totalSpins = totalSpins + 1 WHERE id = 1")
    suspend fun incrementTotalSpins()

    @Query("UPDATE player_data SET totalWins = totalWins + 1 WHERE id = 1")
    suspend fun incrementTotalWins()

    @Query("UPDATE player_data SET jackpotCount = jackpotCount + 1 WHERE id = 1")
    suspend fun incrementJackpotCount()

    @Query("UPDATE player_data SET bestWinStreak = :streak WHERE id = 1 AND bestWinStreak < :streak")
    suspend fun updateBestWinStreak(streak: Int)

    @Insert
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntry)

    @Query("SELECT * FROM leaderboard ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<LeaderboardEntry>>
}
