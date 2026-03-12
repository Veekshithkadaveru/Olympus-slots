package app.krafted.olympusslots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.olympusslots.R
import app.krafted.olympusslots.data.PlayerDao
import app.krafted.olympusslots.game.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SpinPhase { IDLE, SPINNING, RESOLVING, RESULT }

enum class Background(val drawableRes: Int) {
    JACKPOT(R.drawable.bg_jackpot),
    BIG_WIN(R.drawable.bg_big_win),
    STANDARD_WIN(R.drawable.bg_standard_win),
    MINOR_WIN(R.drawable.bg_minor_win),
    NEUTRAL(R.drawable.bg_no_match)
}

data class SlotUiState(
    val coinBalance: Int = GameConstants.STARTING_BALANCE,
    val reelSymbols: List<God> = listOf(God.HERMES, God.DEMETER, God.APOLLO),
    val spinPhase: SpinPhase = SpinPhase.IDLE,
    val winResult: WinResult? = null,
    val freeSpinsRemaining: Int = 0,
    val apolloDoubleActive: Boolean = false,
    val dailyBonusAvailable: Boolean = false,
    val currentBackground: Background = Background.NEUTRAL,
    val winStreak: Int = 0,
    val lastGodPower: GodPower? = null,
    val hermesReshuffleCount: Int = 0,
    val isAutoReshuffling: Boolean = false,
    val isLowBalance: Boolean = false
)

class SlotViewModel(private val playerDao: PlayerDao) : ViewModel() {
    private val _uiState = MutableStateFlow(SlotUiState())
    val uiState: StateFlow<SlotUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playerDao.getPlayerData().collect { playerData ->
                if (playerData != null) {
                    _uiState.update { it.copy(
                        coinBalance = playerData.coinBalance,
                        winStreak = playerData.currentWinStreak,
                        dailyBonusAvailable = isDailyBonusAvailable(playerData.lastBonusClaim)
                    )}
                }
            }
        }
    }

    fun canSpin(): Boolean {
        val state = _uiState.value
        if (state.freeSpinsRemaining > 0) return true
        return state.coinBalance >= GameConstants.SPIN_COST
    }

    fun spin() {
        val state = _uiState.value
        if (state.spinPhase != SpinPhase.IDLE) return
        if (!canSpin()) return

        val isFree = state.freeSpinsRemaining > 0
        val cost = if (isFree) 0 else GameConstants.SPIN_COST

        _uiState.update { it.copy(
            spinPhase = SpinPhase.SPINNING,
            coinBalance = it.coinBalance - cost,
            freeSpinsRemaining = if (isFree) it.freeSpinsRemaining - 1 else it.freeSpinsRemaining,
            winResult = null,
            lastGodPower = null,
            currentBackground = Background.NEUTRAL
        )}

        viewModelScope.launch {
            executeSpin(isFree)
        }
    }

    private suspend fun executeSpin(isFree: Boolean) {
        val stateBeforeSpin = _uiState.value
        val (r1, r2, r3) = ReelEngine.spinAllReels()
        delay(200)
        _uiState.update { it.copy(
            reelSymbols = listOf(r1, r2, r3),
            spinPhase = SpinPhase.RESOLVING
        )}

        delay(GameConstants.REEL_3_STOP_MS + 300)
        val result = WinResolver.resolve(r1, r2, r3)
        val background = resolveBackground(result)
        val isWin = result !is WinResult.NoMatch

        var payout = when (result) {
            is WinResult.ThreeOfAKind -> result.payout
            is WinResult.TwoOfAKind -> result.payout
            is WinResult.NoMatch -> 0
        }

        // Apply Apollo double (only on paid spins with a win)
        val consumeApollo = stateBeforeSpin.apolloDoubleActive && !isFree
        if (consumeApollo && payout > 0) {
            payout *= 2
        }

        // God powers only trigger on 3-of-a-kind
        val godPower = GodPowerEngine.resolve(result)

        // Hermes reshuffle: auto-respin if under max limit
        val isHermesReshuffle = godPower is GodPower.Reshuffle &&
                stateBeforeSpin.hermesReshuffleCount < GameConstants.MAX_HERMES_RESHUFFLES

        _uiState.update {
            var newState = it.copy(
                spinPhase = SpinPhase.RESULT,
                winResult = result,
                coinBalance = it.coinBalance + payout,
                currentBackground = background,
                lastGodPower = if (godPower is GodPower.None) null else godPower,
                apolloDoubleActive = if (consumeApollo) false else it.apolloDoubleActive,
                hermesReshuffleCount = if (isHermesReshuffle) it.hermesReshuffleCount + 1 else 0
            )
            newState = applyGodPower(newState, godPower, isHermesReshuffle)
            newState = updateWinStreak(newState, isWin)
            newState = newState.copy(
                isLowBalance = newState.coinBalance < GameConstants.COIN_FLOOR && newState.freeSpinsRemaining <= 0
            )
            newState
        }

        // Persist to Room
        val currentState = _uiState.value
        playerDao.updateCoinBalance(currentState.coinBalance)
        playerDao.updateWinStreak(currentState.winStreak)
        playerDao.updateBestWinStreak(currentState.winStreak)
        playerDao.incrementTotalSpins()
        if (isWin) playerDao.incrementTotalWins()
        if (result is WinResult.ThreeOfAKind && result.god == God.ZEUS) {
            playerDao.incrementJackpotCount()
        }

        // Hermes auto-respin: wait for result display, then auto-trigger (costs 0 coins)
        if (isHermesReshuffle) {
            delay(1500) // Brief pause to show Hermes result
            _uiState.update { it.copy(
                spinPhase = SpinPhase.IDLE,
                isAutoReshuffling = true,
                currentBackground = Background.NEUTRAL
            )}
            delay(300)
            _uiState.update { it.copy(
                spinPhase = SpinPhase.SPINNING,
                winResult = null,
                lastGodPower = null
            )}
            executeSpin(isFree = true)
        } else {
            _uiState.update { it.copy(isAutoReshuffling = false) }
        }
    }

    fun resetToIdle() {
        _uiState.update { it.copy(
            spinPhase = SpinPhase.IDLE,
            hermesReshuffleCount = 0,
            currentBackground = Background.NEUTRAL
        )}
    }

    private fun applyGodPower(state: SlotUiState, power: GodPower?, isHermesReshuffle: Boolean = false): SlotUiState {
        return when (power) {
            is GodPower.FreeSpins -> state.copy(freeSpinsRemaining = state.freeSpinsRemaining + power.count)
            is GodPower.AllWilds -> state.copy(freeSpinsRemaining = state.freeSpinsRemaining + 1)
            is GodPower.DoubleNextSpin -> state.copy(apolloDoubleActive = true)
            is GodPower.DailyBonusSpin -> state.copy(dailyBonusAvailable = true)
            is GodPower.Reshuffle -> {
                // Auto-reshuffle handles the free re-spin automatically; no extra free spin
                // If max reshuffles reached, grant a free spin instead
                if (isHermesReshuffle) state
                else state.copy(freeSpinsRemaining = state.freeSpinsRemaining + 1)
            }
            is GodPower.Jackpot -> state
            is GodPower.None -> state
            null -> state
        }
    }

    private fun updateWinStreak(state: SlotUiState, isWin: Boolean): SlotUiState {
        return if (isWin) {
            val newStreak = state.winStreak + 1
            val bonus = if (newStreak % GameConstants.WIN_STREAK_THRESHOLD == 0)
                GameConstants.WIN_STREAK_BONUS else 0
            state.copy(
                winStreak = newStreak,
                coinBalance = state.coinBalance + bonus
            )
        } else {
            state.copy(winStreak = 0)
        }
    }

    private fun resolveBackground(result: WinResult): Background = when (result) {
        is WinResult.ThreeOfAKind -> when (result.god) {
            God.ZEUS -> Background.JACKPOT
            God.POSEIDON, God.HADES -> Background.BIG_WIN
            else -> Background.STANDARD_WIN
        }
        is WinResult.TwoOfAKind -> Background.MINOR_WIN
        is WinResult.NoMatch -> Background.NEUTRAL
    }

    private fun isDailyBonusAvailable(lastClaim: Long): Boolean {
        return System.currentTimeMillis() - lastClaim >= GameConstants.DAILY_BONUS_COOLDOWN_MS
    }
}
