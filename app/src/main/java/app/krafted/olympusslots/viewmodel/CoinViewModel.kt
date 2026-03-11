package app.krafted.olympusslots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.olympusslots.data.LeaderboardEntry
import app.krafted.olympusslots.data.PlayerDao
import app.krafted.olympusslots.data.PlayerData
import app.krafted.olympusslots.game.GameConstants
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CoinViewModel(private val playerDao: PlayerDao) : ViewModel() {

    val playerData: StateFlow<PlayerData?> = playerDao.getPlayerData()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val coinBalance: StateFlow<Int> = playerData
        .map { it?.coinBalance ?: GameConstants.STARTING_BALANCE }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameConstants.STARTING_BALANCE)

    val dailyBonusAvailable: StateFlow<Boolean> = playerData
        .map { data ->
            if (data == null) false
            else System.currentTimeMillis() - data.lastBonusClaim >= GameConstants.DAILY_BONUS_COOLDOWN_MS
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val topScores: StateFlow<List<LeaderboardEntry>> = playerDao.getTopScores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val existing = playerData.first { true }
            if (existing == null) {
                playerDao.upsertPlayerData(PlayerData())
            }
        }
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            val data = playerData.value ?: return@launch
            val newBalance = data.coinBalance + GameConstants.DAILY_BONUS
            playerDao.updateCoinBalance(newBalance)
            playerDao.updateLastBonusClaim(System.currentTimeMillis())
        }
    }

    fun recordScore(score: Int) {
        viewModelScope.launch {
            playerDao.insertLeaderboardEntry(LeaderboardEntry(score = score))
        }
    }
}
