---
name: Olympus Slots v1.0
overview: Build a casual casino-style slot machine game themed around Greek gods of Mount Olympus. Features 3 reels with 7 weighted god symbols, unique god powers (Zeus jackpot, Poseidon free spins, Hades wild, Apollo double, Hermes retry, Demeter daily bonus, Artemis minor bonus), virtual coin economy with daily bonuses, win streak rewards, staggered reel animations, dynamic backgrounds, and local leaderboard. Pure Jetpack Compose, no SurfaceView. 3-day delivery sprint.
todos:
  - id: project-setup
    content: Setup project with Compose, Room, Navigation, and copy asset pack into resources
    status: pending
  - id: data-layer
    content: Build Room database with PlayerData entity, LeaderboardEntry entity, DAO, and AppDatabase
    status: pending
    dependencies:
      - project-setup
  - id: game-engine
    content: Implement ReelEngine (weighted random), WinResolver (match detection + Hades wild), and GodPowerEngine
    status: pending
    dependencies:
      - project-setup
  - id: viewmodels
    content: Build SlotViewModel (spin state, reel logic, god powers) and CoinViewModel (balance, daily bonus, win streak)
    status: pending
    dependencies:
      - data-layer
      - game-engine
  - id: splash-screen
    content: Create SplashScreen with Olympus logo reveal and god icon animations
    status: pending
    dependencies:
      - project-setup
  - id: home-screen
    content: Build HomeScreen with coin balance, spin button nav, daily bonus indicator, leaderboard nav
    status: pending
    dependencies:
      - viewmodels
  - id: slot-screen
    content: Implement SlotScreen with 3 animated reels, staggered stop, spin button, background cycling, win line
    status: pending
    dependencies:
      - viewmodels
      - home-screen
  - id: win-screen
    content: Build WinScreen with god-specific win display and coin award animation
    status: pending
    dependencies:
      - slot-screen
  - id: jackpot-screen
    content: Create JackpotScreen with full-screen Zeus celebration, lightning, coin shower
    status: pending
    dependencies:
      - slot-screen
  - id: god-powers
    content: Implement all 7 god power animations and mechanics (free spins, wild, double, retry, daily bonus)
    status: pending
    dependencies:
      - slot-screen
      - win-screen
  - id: daily-bonus-screen
    content: Build DailyBonusScreen with 100 coin claim and 24-hour cooldown logic
    status: pending
    dependencies:
      - viewmodels
  - id: leaderboard-screen
    content: Create LeaderboardScreen showing top 10 all-time coin balances from Room
    status: pending
    dependencies:
      - data-layer
  - id: free-spin-system
    content: Implement free spin counter, Poseidon +3 spins, Hermes retry, Demeter daily spin
    status: pending
    dependencies:
      - god-powers
  - id: apollo-double
    content: Implement Apollo double-win mechanic — next paid spin only, then resets
    status: pending
    dependencies:
      - god-powers
  - id: coin-economy
    content: Wire up coin floor (< 20 coins safety net), win streak bonus (5 wins = 75 coins), all payout logic
    status: pending
    dependencies:
      - free-spin-system
      - apollo-double
  - id: audio-haptics
    content: Add sound effects (spin, win, jackpot, god powers) and haptic feedback
    status: pending
    dependencies:
      - god-powers
  - id: polish
    content: Edge case handling (zero coins, free spin chains), UI polish, background transitions
    status: pending
    dependencies:
      - coin-economy
      - audio-haptics
  - id: qa-delivery
    content: 50-spin crash test, acceptance criteria verification, client APK build
    status: pending
    dependencies:
      - polish
---

# Olympus Slots — Phase 1 Implementation Plan

> **Overview**: Build a casual casino-style slot machine game themed around Greek gods of Mount Olympus. Features 3 reels with 7 weighted god symbols, unique god powers (Zeus jackpot, Poseidon free spins, Hades wild, Apollo double, Hermes retry, Demeter daily bonus, Artemis minor bonus), virtual coin economy with daily bonuses, win streak rewards, staggered reel animations, dynamic backgrounds, and local leaderboard. Pure Jetpack Compose, no SurfaceView. 3-day delivery sprint.

> **Asset Note**: Agents must copy assets from `/Users/veekshith/Downloads/Olymp_N9_elements/` into `app/src/main/res/drawable/`. God symbols are `1.PNG`–`7.png`, backgrounds are `back 1.JPG`–`back 5.jpg`. Rename files to lowercase snake_case during copy (e.g., `god_zeus.png`, `bg_jackpot.jpg`).

## ✅ Project Status & Todos

### 🏗 Phase A: Core Engine (Day 1)
- [x] **A1: Project Setup** <!-- id: project-setup -->
- [ ] **A2: Data Layer** <!-- id: data-layer -->
- [ ] **A3: Game Engine** <!-- id: game-engine -->
- [ ] **A4: ViewModels** <!-- id: viewmodels -->

### 🎰 Phase B: Screens (Day 2)
- [ ] **B1: Splash Screen** <!-- id: splash-screen -->
- [ ] **B2: Home Screen** <!-- id: home-screen -->
- [ ] **B3: Slot Screen** <!-- id: slot-screen -->
- [ ] **B4: Win Screen** <!-- id: win-screen -->
- [ ] **B5: Jackpot Screen** <!-- id: jackpot-screen -->

### ⚡ Phase C: God Powers & Economy (Day 3)
- [ ] **C1: God Power Animations** <!-- id: god-powers -->
- [ ] **C2: Daily Bonus Screen** <!-- id: daily-bonus-screen -->
- [ ] **C3: Leaderboard Screen** <!-- id: leaderboard-screen -->
- [ ] **C4: Free Spin System** <!-- id: free-spin-system -->
- [ ] **C5: Apollo Double Mechanic** <!-- id: apollo-double -->
- [ ] **C6: Coin Economy** <!-- id: coin-economy -->

### 🚀 Phase D: Polish & Delivery (Day 3.5)
- [ ] **D1: Audio & Haptics** <!-- id: audio-haptics -->
- [ ] **D2: Polish & Edge Cases** <!-- id: polish -->
- [ ] **D3: QA & Delivery** <!-- id: qa-delivery -->

---

## 🏗 System Architecture

### 1. High-Level Architecture (MVVM + Compose)
```
┌─────────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI Layer                  │
│   (SplashScreen, HomeScreen, SlotScreen, WinScreen, etc.)  │
├─────────────────────────────────────────────────────────────┤
│                     ViewModel Layer                         │
│       (SlotViewModel + CoinViewModel via StateFlow)        │
├─────────────────────────────────────────────────────────────┤
│                    Game Logic Layer                         │
│     (ReelEngine, WinResolver, GodPowerEngine)              │
├─────────────────────────────────────────────────────────────┤
│                      Data Layer                            │
│     (Room DB: PlayerData, LeaderboardEntry, PlayerDao)     │
└─────────────────────────────────────────────────────────────┘
```

### 2. State Architecture (StateFlow + MVVM)
```
SlotViewModel
    ├── SlotUiState (StateFlow)
    │   ├── coinBalance: Int
    │   ├── reelSymbols: List<God>
    │   ├── spinPhase: SpinPhase
    │   ├── winResult: WinResult?
    │   ├── freeSpinsRemaining: Int
    │   ├── apolloDoubleActive: Boolean
    │   ├── dailyBonusAvailable: Boolean
    │   └── currentBackground: Background
    ├── ReelEngine (weighted random generation)
    ├── WinResolver (match detection + Hades wild)
    └── GodPowerEngine (special effect execution)

CoinViewModel
    ├── coinBalance: StateFlow<Int>
    ├── dailyBonusAvailable: StateFlow<Boolean>
    ├── winStreak: StateFlow<Int>
    └── Room DAO (persist balance + bonus timestamp)
```

### 3. Project File Structure
```
app/krafted/olympusslots/
├── MainActivity.kt                    # Compose NavHost, entry point
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt           # Logo reveal, god icons animate in
│   │   ├── HomeScreen.kt             # Balance display, nav to slot + leaderboard
│   │   ├── SlotScreen.kt             # 3 reels, spin button, win line, background
│   │   ├── WinScreen.kt              # Win result, god power animation, coins awarded
│   │   ├── JackpotScreen.kt          # Full-screen Zeus celebration
│   │   ├── DailyBonusScreen.kt       # Daily 100 coin claim
│   │   └── LeaderboardScreen.kt      # Top 10 from Room
│   ├── components/
│   │   ├── ReelColumn.kt             # Single reel with vertical scroll animation
│   │   ├── SpinButton.kt             # Animated spin trigger
│   │   ├── CoinDisplay.kt            # Balance with coin icon
│   │   ├── WinLineHighlight.kt       # Glow on matching symbols
│   │   ├── GodPowerOverlay.kt        # Canvas overlay for god FX
│   │   └── BackgroundScene.kt        # Dynamic temple background
│   ├── navigation/
│   │   └── NavGraph.kt               # Navigation routes
│   └── theme/
│       ├── Theme.kt                   # Olympus dark/gold theme
│       ├── Color.kt                   # God-specific colours
│       └── Type.kt                    # Typography
├── game/
│   ├── ReelEngine.kt                  # Reel outcome via weighted random
│   ├── WinResolver.kt                # Match detection, payout calc, god power trigger
│   ├── GodPowerEngine.kt             # Executes each god's special effect on state
│   └── GameConstants.kt              # Weights, payouts, costs, timing constants
├── viewmodel/
│   ├── SlotViewModel.kt              # All slot state via StateFlow
│   └── CoinViewModel.kt              # Global coin balance, daily bonus logic
├── data/
│   ├── PlayerData.kt                  # Room entity (coins, last bonus claim, spin count)
│   ├── LeaderboardEntry.kt           # Room entity (score, date)
│   ├── PlayerDao.kt                  # Room DAO
│   └── AppDatabase.kt                # Room database
└── res/
    └── drawable/
        ├── god_zeus.png               # 1.PNG → Zeus (Legendary)
        ├── god_poseidon.png           # 2.PNG → Poseidon (Rare)
        ├── god_hades.png              # 3.PNG → Hades (Rare, Wild)
        ├── god_artemis.png            # 4.png → Artemis (Uncommon)
        ├── god_apollo.png             # 5.png → Apollo (Uncommon)
        ├── god_demeter.png            # 6.png → Demeter (Common)
        ├── god_hermes.png             # 7.png → Hermes (Common)
        ├── bg_jackpot.jpg             # back 1.JPG → Zeus jackpot
        ├── bg_big_win.jpg             # back 2.JPG → Poseidon/Hades win
        ├── bg_standard_win.jpg        # back 3.JPG → Standard win
        ├── bg_minor_win.jpg           # back 4.jpg → Minor win (2 of a kind)
        └── bg_no_match.jpg            # back 5.jpg → No match
```

### 4. Asset Mapping Reference
| Source File | Drawable Name | God / Use | Rarity | Spawn Weight |
|-------------|---------------|-----------|--------|--------------|
| `1.PNG` | `god_zeus.png` | ⚡ Zeus — King of Gods | Legendary | 2 (~3%) |
| `2.PNG` | `god_poseidon.png` | 🔱 Poseidon — Sea & Storms | Rare | 4 (~7%) |
| `3.PNG` | `god_hades.png` | 💀 Hades — Underworld (Wild) | Rare | 5 (~8%) |
| `4.png` | `god_artemis.png` | 🏹 Artemis — Hunt & Moon | Uncommon | 10 (~17%) |
| `5.png` | `god_apollo.png` | ☀️ Apollo — Sun & Light | Uncommon | 10 (~17%) |
| `6.png` | `god_demeter.png` | 🌿 Demeter — Harvest | Common | 14 (~23%) |
| `7.png` | `god_hermes.png` | 🦅 Hermes — Messenger | Common | 15 (~25%) |
| `back 1.JPG` | `bg_jackpot.jpg` | Zeus jackpot background | — | Lightning flash overlay |
| `back 2.JPG` | `bg_big_win.jpg` | Big win (Poseidon/Hades) | — | Wave shimmer overlay |
| `back 3.JPG` | `bg_standard_win.jpg` | Standard win background | — | Gold glow pulse |
| `back 4.jpg` | `bg_minor_win.jpg` | Minor win (2 of a kind) | — | Soft light |
| `back 5.jpg` | `bg_no_match.jpg` | No match / idle | — | Dims to 70% brightness |

---

## 🚀 Detailed Implementation Roadmap

---

## Phase A: Core Engine (Day 1)

### A1: Project Setup <!-- id: project-setup -->
> **Goal**: Configure project with Compose, Room, Navigation dependencies and copy Olympus asset pack into resources.

**Duration**: 2 Hours

**Files to create/modify:**
| File | Description |
|------|-------------|
| `build.gradle.kts` (app) | Add Compose, Room, Navigation, Lifecycle dependencies |
| `gradle/libs.versions.toml` | Version catalog updates |
| `res/drawable/god_*.png` | Copy and rename 7 god symbol assets |
| `res/drawable/bg_*.jpg` | Copy and rename 5 background assets |
| `ui/navigation/NavGraph.kt` | Navigation routes and NavHost |
| `MainActivity.kt` | Compose NavHost entry point |

**Key Dependencies:**
```kotlin
// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Compose
implementation(platform("androidx.compose:compose-bom:2024.01.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.activity:activity-compose:1.8.2")
implementation("androidx.compose.animation:animation")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

**Asset Copy Commands:**
```bash
# God symbols (rename to snake_case lowercase)
cp "Olymp_N9_elements/1.PNG" app/src/main/res/drawable/god_zeus.png
cp "Olymp_N9_elements/2.PNG" app/src/main/res/drawable/god_poseidon.png
cp "Olymp_N9_elements/3.PNG" app/src/main/res/drawable/god_hades.png
cp "Olymp_N9_elements/4.png" app/src/main/res/drawable/god_artemis.png
cp "Olymp_N9_elements/5.png" app/src/main/res/drawable/god_apollo.png
cp "Olymp_N9_elements/6.png" app/src/main/res/drawable/god_demeter.png
cp "Olymp_N9_elements/7.png" app/src/main/res/drawable/god_hermes.png

# Backgrounds (rename to snake_case lowercase)
cp "Olymp_N9_elements/back 1.JPG" app/src/main/res/drawable/bg_jackpot.jpg
cp "Olymp_N9_elements/back 2.JPG" app/src/main/res/drawable/bg_big_win.jpg
cp "Olymp_N9_elements/back 3.JPG" app/src/main/res/drawable/bg_standard_win.jpg
cp "Olymp_N9_elements/back 4.jpg" app/src/main/res/drawable/bg_minor_win.jpg
cp "Olymp_N9_elements/back 5.jpg" app/src/main/res/drawable/bg_no_match.jpg
```

**Navigation Routes:**
```kotlin
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Slot : Screen("slot")
    object Win : Screen("win/{godName}/{coinsWon}")
    object Jackpot : Screen("jackpot")
    object DailyBonus : Screen("daily_bonus")
    object Leaderboard : Screen("leaderboard")
}
```

**Exit Criteria:**
- [ ] Project builds and runs on emulator
- [ ] All 12 assets render correctly in drawable
- [ ] NavHost navigates between placeholder screens
- [ ] No build errors or dependency conflicts

---

### A2: Data Layer <!-- id: data-layer -->
> **Goal**: Build Room database with player data persistence and leaderboard storage.

**Duration**: 1.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `data/PlayerData.kt` | Room entity — coins, last bonus claim timestamp, spin count, win streak |
| `data/LeaderboardEntry.kt` | Room entity — player name, score, date |
| `data/PlayerDao.kt` | Room DAO — CRUD for player data and leaderboard |
| `data/AppDatabase.kt` | Room database singleton |

**Room Entity — PlayerData:**
```kotlin
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
```

**Room Entity — LeaderboardEntry:**
```kotlin
@Entity(tableName = "leaderboard")
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val date: Long = System.currentTimeMillis()
)
```

**DAO:**
```kotlin
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

    @Insert
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntry)

    @Query("SELECT * FROM leaderboard ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<LeaderboardEntry>>
}
```

**Exit Criteria:**
- [ ] Room database creates on first launch
- [ ] PlayerData initialises with 500 coins
- [ ] Coin balance persists after app kill and restart
- [ ] Leaderboard stores and retrieves top 10 scores

---

### A3: Game Engine <!-- id: game-engine -->
> **Goal**: Implement the core slot mechanics — weighted random reel generation, win detection with Hades wild substitution, and god power trigger system.

**Duration**: 2 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `game/ReelEngine.kt` | Weighted random reel outcome generation |
| `game/WinResolver.kt` | Match detection, payout calculation, god power trigger |
| `game/GodPowerEngine.kt` | Executes each god's special effect on game state |
| `game/GameConstants.kt` | All weights, payouts, costs, and timing constants |

**God Enum & Weights:**
```kotlin
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

enum class Rarity { COMMON, UNCOMMON, RARE, LEGENDARY }
```

**ReelEngine — Weighted Random:**
```kotlin
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
```

**WinResolver — Match Detection with Hades Wild:**
```kotlin
object WinResolver {
    fun resolve(r1: God, r2: God, r3: God): WinResult {
        val reels = listOf(r1, r2, r3)
        val nonWild = reels.filter { it != God.HADES }

        // All three are Hades
        if (nonWild.isEmpty()) return WinResult.ThreeOfAKind(God.HADES, 150)

        // Check 3-of-a-kind (with wild substitution)
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

sealed class WinResult {
    data class ThreeOfAKind(val god: God, val payout: Int) : WinResult()
    data class TwoOfAKind(val god: God, val payout: Int) : WinResult()
    object NoMatch : WinResult()
}
```

**GodPowerEngine — Special Effects:**
```kotlin
object GodPowerEngine {
    fun getGodPower(god: God): GodPower = when (god) {
        God.ZEUS -> GodPower.Jackpot              // 500 coins, lightning animation
        God.POSEIDON -> GodPower.FreeSpins(3)     // +3 free spins
        God.HADES -> GodPower.AllWilds            // Next spin is free
        God.ARTEMIS -> GodPower.None              // No special power on 3-match
        God.APOLLO -> GodPower.DoubleNextSpin     // ×2 next paid spin
        God.DEMETER -> GodPower.DailyBonusSpin    // Unlocks daily bonus spin
        God.HERMES -> GodPower.Reshuffle          // Free retry with reshuffled reels
    }
}

sealed class GodPower {
    object Jackpot : GodPower()
    data class FreeSpins(val count: Int) : GodPower()
    object AllWilds : GodPower()
    object None : GodPower()
    object DoubleNextSpin : GodPower()
    object DailyBonusSpin : GodPower()
    object Reshuffle : GodPower()
}
```

**Payout Table Reference:**
| Match | Coins Won | God Power |
|-------|-----------|-----------|
| 3× Zeus ⚡ | 500 | JACKPOT banner, lightning animation |
| 3× Poseidon 🔱 | 200 | +3 Free Spins granted |
| 3× Hades 💀 | 150 | All wilds — next spin is free |
| 3× Artemis 🏹 | 100 | None |
| 3× Apollo ☀️ | 100 | Next spin winnings ×2 |
| 3× Demeter 🌿 | 80 | Daily bonus spin unlocked |
| 3× Hermes 🦅 | 60 | Reels reshuffled, free retry |
| Any 2 of a kind | 15 | None |
| No match | 0 | — |

**Exit Criteria:**
- [ ] `spinReel()` produces Zeus ~3% and Hermes ~25% over 1000 spins
- [ ] Hades wild correctly substitutes to form winning matches for all 7 gods
- [ ] 3-of-a-kind returns correct payout for each god
- [ ] 2-of-a-kind returns 15 coins
- [ ] No match returns 0

---

### A4: ViewModels <!-- id: viewmodels -->
> **Goal**: Build SlotViewModel and CoinViewModel with StateFlow to manage all slot state and coin economy.

**Duration**: 2.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `viewmodel/SlotViewModel.kt` | Spin state, reel logic, god powers, background selection |
| `viewmodel/CoinViewModel.kt` | Coin balance, daily bonus 24h cooldown, win streak tracking |

**SlotUiState:**
```kotlin
data class SlotUiState(
    val coinBalance: Int = 500,
    val reelSymbols: List<God> = listOf(God.HERMES, God.DEMETER, God.APOLLO),
    val spinPhase: SpinPhase = SpinPhase.IDLE,
    val winResult: WinResult? = null,
    val freeSpinsRemaining: Int = 0,
    val apolloDoubleActive: Boolean = false,
    val dailyBonusAvailable: Boolean = false,
    val currentBackground: Background = Background.NEUTRAL,
    val winStreak: Int = 0,
    val lastGodPower: GodPower? = null
)

enum class SpinPhase { IDLE, SPINNING, RESOLVING, RESULT }

enum class Background(val drawableRes: Int) {
    JACKPOT(R.drawable.bg_jackpot),
    BIG_WIN(R.drawable.bg_big_win),
    STANDARD_WIN(R.drawable.bg_standard_win),
    MINOR_WIN(R.drawable.bg_minor_win),
    NEUTRAL(R.drawable.bg_no_match)
}
```

**SlotViewModel Core Logic:**
```kotlin
class SlotViewModel(private val playerDao: PlayerDao) : ViewModel() {
    private val _uiState = MutableStateFlow(SlotUiState())
    val uiState: StateFlow<SlotUiState> = _uiState.asStateFlow()

    fun spin() {
        val state = _uiState.value
        if (state.spinPhase != SpinPhase.IDLE) return

        val cost = if (state.freeSpinsRemaining > 0) 0 else 10
        if (state.coinBalance < cost) return

        _uiState.update { it.copy(
            spinPhase = SpinPhase.SPINNING,
            coinBalance = it.coinBalance - cost,
            freeSpinsRemaining = maxOf(0, it.freeSpinsRemaining - 1)
        )}

        viewModelScope.launch {
            val (r1, r2, r3) = ReelEngine.spinAllReels()
            delay(200) // brief spin start delay
            _uiState.update { it.copy(reelSymbols = listOf(r1, r2, r3), spinPhase = SpinPhase.RESOLVING) }

            delay(1000) // wait for reel animations (600+800+1000ms stagger handled by UI)
            val result = WinResolver.resolve(r1, r2, r3)
            val payout = calculatePayout(result, state.apolloDoubleActive)
            val background = resolveBackground(result)
            val godPower = resolveGodPower(result)

            _uiState.update { it.copy(
                spinPhase = SpinPhase.RESULT,
                winResult = result,
                coinBalance = it.coinBalance + payout,
                currentBackground = background,
                lastGodPower = godPower,
                // Apply god power state changes...
            )}

            playerDao.updateCoinBalance(_uiState.value.coinBalance)
        }
    }
}
```

**Daily Bonus Cooldown Logic:**
```kotlin
fun isDailyBonusAvailable(lastClaim: Long): Boolean {
    val twentyFourHours = 24 * 60 * 60 * 1000L
    return System.currentTimeMillis() - lastClaim >= twentyFourHours
}

suspend fun claimDailyBonus() {
    _uiState.update { it.copy(
        coinBalance = it.coinBalance + 100,
        dailyBonusAvailable = false
    )}
    playerDao.updateLastBonusClaim(System.currentTimeMillis())
    playerDao.updateCoinBalance(_uiState.value.coinBalance)
}
```

**Background Resolution:**
```kotlin
fun resolveBackground(result: WinResult): Background = when (result) {
    is WinResult.ThreeOfAKind -> when (result.god) {
        God.ZEUS -> Background.JACKPOT
        God.POSEIDON, God.HADES -> Background.BIG_WIN
        else -> Background.STANDARD_WIN
    }
    is WinResult.TwoOfAKind -> Background.MINOR_WIN
    WinResult.NoMatch -> Background.NEUTRAL
}
```

**Exit Criteria:**
- [ ] `spin()` deducts 10 coins and updates reel symbols
- [ ] Free spins cost 0 coins and decrement counter
- [ ] Apollo double applies ×2 to next paid spin only, then resets
- [ ] Win streak increments on win, resets on loss, grants 75 bonus at 5
- [ ] Daily bonus available exactly 24 hours after last claim
- [ ] Coin floor prevents spin when balance < 10 coins (and no free spins)
- [ ] All state changes emit via StateFlow

---

## Phase B: Screens (Day 2)

### B1: Splash Screen <!-- id: splash-screen -->
> **Goal**: Create an epic Mount Olympus logo reveal with god icons animating in.

**Duration**: 1.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/SplashScreen.kt` | Logo reveal, god icons animate in sequence |

**Animation Sequence:**
1. Black screen fades in temple background (1s)
2. "OLYMPUS SLOTS" title scales up with gold glow (0.5s)
3. 7 god icons fly in from edges in a circle formation (1s staggered)
4. Brief hold (0.5s)
5. Auto-navigate to Home Screen

**Implementation Notes:**
- Use `AnimatedVisibility` with `fadeIn` + `scaleIn` for title
- Use `Animatable` with `slideIn` offset per god icon (staggered 100ms each)
- Total splash duration: ~3.5 seconds
- Background: `bg_jackpot.jpg` at 60% opacity

**Exit Criteria:**
- [ ] Splash displays for ~3.5 seconds
- [ ] Logo and god icons animate smoothly
- [ ] Auto-navigates to Home
- [ ] No flash of blank screen

---

### B2: Home Screen <!-- id: home-screen -->
> **Goal**: Build the main hub with coin balance, spin button, daily bonus indicator, and leaderboard navigation.

**Duration**: 2 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/HomeScreen.kt` | Main hub layout |
| `ui/components/CoinDisplay.kt` | Animated coin balance display |

**Layout (Top to Bottom):**
```
[ Temple background — bg_no_match.jpg at 80% ]
[ ────────────────────────────────────── ]
[        OLYMPUS SLOTS title             ]
[        Coin balance display            ]
[ ────────────────────────────────────── ]
[     ⚡ SPIN THE REELS ⚡ (button)     ]
[ ────────────────────────────────────── ]
[   🎁 Daily Bonus    📊 Leaderboard   ]
[ ────────────────────────────────────── ]
```

**Key Interactions:**
- Tap "SPIN THE REELS" → navigates to Slot Screen
- Tap "Daily Bonus" → navigates to DailyBonusScreen (with badge if available)
- Tap "Leaderboard" → navigates to LeaderboardScreen
- Coin balance animates on change (count-up effect)

**Exit Criteria:**
- [ ] Coin balance displays correctly from Room
- [ ] Navigation to Slot, DailyBonus, Leaderboard works
- [ ] Daily bonus badge shows when available
- [ ] Premium Olympus visual theme applied

---

### B3: Slot Screen <!-- id: slot-screen -->
> **Goal**: Implement the main game screen with 3 animated reels, staggered stop timing, spin button, win line highlight, and dynamic background switching.

**Duration**: 4 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/SlotScreen.kt` | Main slot machine layout and orchestration |
| `ui/components/ReelColumn.kt` | Single reel with vertical scroll animation |
| `ui/components/SpinButton.kt` | Animated spin trigger button |
| `ui/components/WinLineHighlight.kt` | Glow on matching centre symbols |
| `ui/components/BackgroundScene.kt` | Dynamic temple background with overlays |

**Slot Screen Layout (Top to Bottom):**
```
[ Background — temple scene (cycles per result)  ]
[ God name banner — reacts to spin outcome        ]
[ ───────────────────────────────────────────── ]
[  REEL 1   |   REEL 2   |   REEL 3             ]
[  (god)    |   (god)    |   (god)              ]
[ ───────────────────────────────────────────── ]
[ Win line highlight bar                         ]
[ Coin balance display                           ]
[ Free spins remaining indicator                 ]
[ ──────── SPIN button ────────────             ]
```

**Reel Animation Spec:**
- Each reel spins independently using `Animatable` + `LaunchedEffect`
- Symbols scroll vertically, showing 3 symbols per reel (centre = active line)
- Staggered stop: Reel 1 at 600ms, Reel 2 at 800ms, Reel 3 at 1000ms
- Stop animation uses `tween` with `EaseOutBounce` for satisfying snap
- Winning centre symbols glow with god-specific colour on match

**ReelColumn Animation:**
```kotlin
@Composable
fun ReelColumn(
    targetGod: God,
    spinPhase: SpinPhase,
    stopDelay: Long, // 600, 800, or 1000
    isWinning: Boolean
) {
    val scrollOffset = remember { Animatable(0f) }

    LaunchedEffect(spinPhase) {
        when (spinPhase) {
            SpinPhase.SPINNING -> {
                // Continuous fast scroll
                scrollOffset.animateTo(
                    targetValue = 1000f,
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                )
            }
            SpinPhase.RESOLVING -> {
                delay(stopDelay)
                // Snap to target with bounce
                scrollOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300, easing = EaseOutBounce)
                )
            }
            else -> { }
        }
    }
    // Render 3 god symbols vertically, centre = active
}
```

**Background Switching Logic:**
| Spin Result | Background | Overlay Effect |
|-------------|-----------|----------------|
| Jackpot (Zeus) | `bg_jackpot.jpg` | Lightning flash overlay |
| Big win (Poseidon/Hades) | `bg_big_win.jpg` | Wave shimmer overlay |
| Standard win | `bg_standard_win.jpg` | Gold glow pulse |
| Minor win (2 of a kind) | `bg_minor_win.jpg` | Soft light |
| No match | `bg_no_match.jpg` | Dims to 70% brightness |

**Exit Criteria:**
- [ ] 3 reels display god symbols correctly
- [ ] Reels spin with smooth vertical scroll animation
- [ ] Staggered stop: R1 → R2 → R3 at 600/800/1000ms
- [ ] Winning symbols glow on match
- [ ] Background changes based on win result
- [ ] Spin button disabled during spin, enabled at IDLE
- [ ] Coin balance updates in real-time
- [ ] Free spin counter shows when > 0

---

### B4: Win Screen <!-- id: win-screen -->
> **Goal**: Build the win result display with god-specific animations and coin award.

**Duration**: 2 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/WinScreen.kt` | Win result overlay with god portrait and coins |

**Win Display Behaviour:**
- Appears as an overlay/dialog on the Slot Screen (not a full navigation)
- Shows matched god portrait, name, and domain
- Coin award animates counting up
- God power description text animates in
- "SPIN AGAIN" button to dismiss
- Auto-dismiss after 3 seconds if no interaction

**For 2-of-a-kind:**
- Smaller overlay, just "+15 coins" text with brief glow
- No god portrait, auto-dismiss after 1.5 seconds

**Exit Criteria:**
- [ ] 3-of-a-kind shows god portrait and full payout
- [ ] 2-of-a-kind shows minor win text
- [ ] Coin count-up animation plays
- [ ] God power description displays
- [ ] Dismiss returns to IDLE spin state

---

### B5: Jackpot Screen <!-- id: jackpot-screen -->
> **Goal**: Create the full-screen Zeus celebration with lightning, coin shower, and maximum impact.

**Duration**: 2 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/JackpotScreen.kt` | Full-screen Zeus jackpot celebration |
| `ui/components/GodPowerOverlay.kt` | Canvas overlay for lightning + particle effects |

**Jackpot Sequence:**
1. Screen flashes white (100ms)
2. `bg_jackpot.jpg` fades in as full background
3. Zeus portrait scales up from centre with lightning bolts (Canvas drawLine with glow)
4. "JACKPOT!" text slams in with scale animation
5. "+500 COINS" counter animates counting up from 0
6. Coin shower particle effect (Canvas circles falling)
7. Hold for 3 seconds
8. "CLAIM" button pulses — tap to dismiss

**Lightning Effect (Compose Canvas):**
```kotlin
Canvas(modifier = Modifier.fillMaxSize()) {
    // Draw 3-5 branching lightning bolts from top
    // Using drawLine with white color and BlendMode.Screen
    // Random offsets per frame for flicker effect
}
```

**Exit Criteria:**
- [ ] Jackpot screen triggers only on 3× Zeus match
- [ ] Lightning bolts render via Canvas
- [ ] Coin shower particles animate
- [ ] "+500" counter animates
- [ ] Claim button dismisses and returns to slot

---

## Phase C: God Powers & Economy (Day 3)

### C1: God Power Animations <!-- id: god-powers -->
> **Goal**: Implement all 7 god-specific power animations and their mechanical effects on game state.

**Duration**: 3 Hours

**Files to modify/create:**
| File | Description |
|------|-------------|
| `ui/components/GodPowerOverlay.kt` | All god power Canvas animations |
| `viewmodel/SlotViewModel.kt` | God power state mutations |

**God Power Animation Specs:**
| God | Animation | State Effect |
|-----|-----------|-------------|
| Zeus ⚡ | Full-screen lightning bolts, coin shower particle effect | +500 coins, navigate to JackpotScreen |
| Poseidon 🔱 | Water wave ripple across screen, +3 free spin counter appears | `freeSpinsRemaining += 3` |
| Hades 💀 | Dark smoke effect on reel, wild substitution highlight | Next spin free (`freeSpinsRemaining += 1`) |
| Apollo ☀️ | Sun ray burst from centre, "×2 NEXT SPIN" banner | `apolloDoubleActive = true` |
| Artemis 🏹 | Moonlight arrows streak across reels (no special mechanic) | Coin payout only |
| Demeter 🌿 | Harvest golden particles rise from bottom, "BONUS SPIN" text | `dailyBonusAvailable = true` |
| Hermes 🦅 | Reels reshuffle at high speed, free retry glow | Trigger automatic free re-spin |

**Poseidon Wave Effect (Canvas):**
```kotlin
// Sine wave drawn across screen width
// Amplitude oscillates over 1 second
// Blue-green translucent fill
drawPath(
    path = wavePath,
    color = Color(0x4400BCD4),
    style = Fill
)
```

**Apollo Sun Ray Effect (Canvas):**
```kotlin
// Radial lines from screen centre
// Rotating slowly with golden gradient
// "×2 NEXT SPIN" text overlay with scale animation
```

**Exit Criteria:**
- [ ] All 7 god power animations render correctly
- [ ] Zeus triggers JackpotScreen navigation
- [ ] Poseidon adds exactly 3 free spins
- [ ] Hades adds 1 free spin
- [ ] Apollo sets double flag, applies to next paid spin only, then resets
- [ ] Demeter unlocks daily bonus
- [ ] Hermes triggers automatic re-spin with reshuffled reels

---

### C2: Daily Bonus Screen <!-- id: daily-bonus-screen -->
> **Goal**: Build the daily 100-coin claim screen with 24-hour cooldown.

**Duration**: 1 Hour

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/DailyBonusScreen.kt` | Daily bonus claim UI |

**Layout:**
```
[ Temple background with golden glow      ]
[ ──────────────────────────────────────── ]
[     🎁 DAILY BLESSING 🎁               ]
[     "The gods bestow their favour"      ]
[ ──────────────────────────────────────── ]
[     +100 COINS (animated counter)       ]
[     Random god portrait blesses player  ]
[ ──────────────────────────────────────── ]
[     [ CLAIM ] button (if available)     ]
[     or "Return in HH:MM:SS" countdown   ]
[ ──────────────────────────────────────── ]
```

**Cooldown Logic:**
- 24-hour cooldown stored as `lastBonusClaim` timestamp in Room
- If `System.currentTimeMillis() - lastBonusClaim >= 86_400_000` → claimable
- On claim: add 100 coins, update timestamp
- If not claimable: show countdown timer to next availability

**Exit Criteria:**
- [ ] 100 coins awarded on claim
- [ ] 24-hour cooldown enforced
- [ ] Countdown timer shows remaining time
- [ ] Cannot claim twice within 24 hours
- [ ] Timestamp persists across app restarts

---

### C3: Leaderboard Screen <!-- id: leaderboard-screen -->
> **Goal**: Display top 10 all-time coin balances from Room database.

**Duration**: 1 Hour

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/LeaderboardScreen.kt` | Top 10 scores list |

**Layout:**
```
[ Temple background                       ]
[ ──────────────────────────────────────── ]
[     🏆 HALL OF OLYMPUS 🏆              ]
[ ──────────────────────────────────────── ]
[  #1  ⚡ 2,500 coins    Mar 10, 2026    ]
[  #2  🔱 1,800 coins    Mar 09, 2026    ]
[  #3  💀 1,200 coins    Mar 08, 2026    ]
[  ...                                    ]
[  #10 🦅   300 coins    Mar 05, 2026    ]
[ ──────────────────────────────────────── ]
[     Current: 500 coins                  ]
[     [ BACK ] button                     ]
```

**Score Recording Logic:**
- After each spin session (when leaving slot screen), record current balance as a leaderboard entry
- Top 10 sorted by score DESC
- Each entry shows rank, score, and date

**Exit Criteria:**
- [ ] Top 10 scores display from Room
- [ ] Scores sorted by descending coin balance
- [ ] Current balance shown for comparison
- [ ] Navigation back to Home works

---

### C4: Free Spin System <!-- id: free-spin-system -->
> **Goal**: Implement the complete free spin mechanic triggered by Poseidon, Hermes, and Demeter.

**Duration**: 1.5 Hours

**Files to modify:**
| File | Description |
|------|-------------|
| `viewmodel/SlotViewModel.kt` | Free spin counter logic and auto-respin |

**Free Spin Rules:**
| Source | Spins Granted | Cost |
|--------|--------------|------|
| Poseidon 3-match | +3 free spins | 0 coins each |
| Hades 3-match | +1 free spin (next spin free) | 0 coins |
| Hermes 3-match | Auto-reshuffle + free retry | 0 coins |
| Demeter 3-match | Daily bonus spin unlocked | 0 coins |

**Implementation Notes:**
- Free spin counter visible on Slot Screen when > 0
- Free spins chain: if a free spin triggers Poseidon, add 3 more
- Hermes reshuffle is an immediate auto-spin (no user tap needed)
- Spin button text changes: "SPIN (10 coins)" → "FREE SPIN" when counter > 0

**Exit Criteria:**
- [ ] Poseidon adds exactly 3 free spins
- [ ] Free spins decrement on each use
- [ ] Free spins cost 0 coins
- [ ] Hermes triggers automatic re-spin
- [ ] Free spin chaining works (free spin → Poseidon → +3 more)
- [ ] Counter displays correctly on UI

---

### C5: Apollo Double Mechanic <!-- id: apollo-double -->
> **Goal**: Implement Apollo's ×2 next-spin multiplier that applies to the next paid spin only.

**Duration**: 0.5 Hours

**Files to modify:**
| File | Description |
|------|-------------|
| `viewmodel/SlotViewModel.kt` | Apollo double flag and payout logic |

**Rules:**
- 3× Apollo sets `apolloDoubleActive = true`
- Next **paid** spin (not free) has its payout doubled
- After applying, `apolloDoubleActive` resets to `false`
- Visual: "×2" banner persists on Slot Screen until consumed
- Does not stack (second Apollo just refreshes the flag)

**Exit Criteria:**
- [ ] Apollo win sets double flag
- [ ] Next paid spin payout is ×2
- [ ] Flag resets after one paid spin
- [ ] Free spins do NOT consume the double
- [ ] "×2" indicator visible on UI when active

---

### C6: Coin Economy <!-- id: coin-economy -->
> **Goal**: Wire up the complete coin economy — spin cost, coin floor safety net, win streak bonus, and all payout logic.

**Duration**: 1.5 Hours

**Files to modify:**
| File | Description |
|------|-------------|
| `viewmodel/SlotViewModel.kt` | Coin floor, win streak, payout integration |
| `viewmodel/CoinViewModel.kt` | Economy constants and balance management |
| `game/GameConstants.kt` | All economy values |

**Coin Economy Constants:**
```kotlin
object GameConstants {
    const val STARTING_BALANCE = 500
    const val SPIN_COST = 10
    const val DAILY_BONUS = 100
    const val DAILY_BONUS_COOLDOWN_MS = 24 * 60 * 60 * 1000L
    const val COIN_FLOOR = 20         // Below this, can't spin
    const val WIN_STREAK_THRESHOLD = 5
    const val WIN_STREAK_BONUS = 75

    // Reel animation timing
    const val REEL_1_STOP_MS = 600L
    const val REEL_2_STOP_MS = 800L
    const val REEL_3_STOP_MS = 1000L
}
```

**Coin Sources Summary:**
| Source | Amount | Frequency |
|--------|--------|-----------|
| Starting bonus | 500 coins | Once |
| Daily login bonus | 100 coins | Every 24 hours |
| Demeter match | 50 coins | On trigger |
| Win streak (5 wins) | 75 bonus coins | Per streak |
| Spin wins | Varies (15–500) | Per spin |

**Coin Floor Logic:**
```kotlin
fun canSpin(): Boolean {
    val state = _uiState.value
    if (state.freeSpinsRemaining > 0) return true
    return state.coinBalance >= GameConstants.SPIN_COST
}

// When balance < COIN_FLOOR and no free spins:
// Show dialog: "The gods' favour wanes... Claim your daily blessing!"
// Offer early daily bonus claim or wait
```

**Win Streak Logic:**
```kotlin
fun updateWinStreak(isWin: Boolean) {
    _uiState.update {
        if (isWin) {
            val newStreak = it.winStreak + 1
            val bonus = if (newStreak % GameConstants.WIN_STREAK_THRESHOLD == 0)
                GameConstants.WIN_STREAK_BONUS else 0
            it.copy(
                winStreak = newStreak,
                coinBalance = it.coinBalance + bonus
            )
        } else {
            it.copy(winStreak = 0)
        }
    }
}
```

**Exit Criteria:**
- [ ] Spin costs exactly 10 coins
- [ ] Coin floor blocks spin when balance < 10 (no free spins)
- [ ] Coin floor dialog offers daily bonus or wait
- [ ] Win streak grants 75 coins every 5 consecutive wins
- [ ] Win streak resets on loss
- [ ] All payouts match the payout table exactly
- [ ] Player never permanently locked out of spinning

---

## Phase D: Polish & Delivery (Day 3.5)

### D1: Audio & Haptics <!-- id: audio-haptics -->
> **Goal**: Add sound effects and haptic feedback for all key interactions.

**Duration**: 1.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `game/SoundManager.kt` | SoundPool-based audio playback |

**Sound Effects:**
| Event | Sound | Duration |
|-------|-------|----------|
| Spin start | Reel whirring / click | Loop during spin |
| Reel stop | Mechanical click / thud | Per reel (×3) |
| Win (minor) | Coin clink | 0.5s |
| Win (major) | Triumphant fanfare | 1.5s |
| Jackpot (Zeus) | Thunder crack + epic brass | 3s |
| Poseidon power | Ocean wave crash | 1s |
| No match | Low thud / dimming tone | 0.5s |
| Button press | Soft click | 0.1s |
| Daily bonus | Blessing chime | 1s |

**Haptic Feedback:**
```kotlin
// Spin start: light vibration (50ms)
// Each reel stop: medium pulse (30ms)
// Win: success pattern (100ms-50ms-100ms)
// Jackpot: strong extended (500ms)
// No match: single soft pulse (20ms)
```

**Exit Criteria:**
- [ ] All sound effects play at correct moments
- [ ] No audio overlap or lag
- [ ] Haptic feedback fires on spin, stop, win, loss
- [ ] Sounds respect device volume settings

---

### D2: Polish & Edge Cases <!-- id: polish -->
> **Goal**: Handle all edge cases, polish UI transitions, and ensure robust state management.

**Duration**: 2 Hours

**Edge Cases to Handle:**
| Edge Case | Expected Behaviour |
|-----------|-------------------|
| Balance = 0, no free spins | Show coin floor dialog, offer daily bonus |
| Free spin chain (Poseidon → Poseidon) | Free spins stack correctly |
| Hermes reshuffle → win → Hermes again | Auto-respin chains, max 3 reshuffles |
| Apollo double + free spin | Double applies to next PAID spin, not free |
| App killed mid-spin | On restart, load last saved balance (pre-spin deduction) |
| Daily bonus claimed + Demeter triggers | Demeter unlocks an additional bonus spin, separate from daily |
| Rapid spin tapping | Spin button disabled during SPINNING/RESOLVING phases |
| Device rotation | State preserved via ViewModel |

**UI Polish Tasks:**
- Smooth screen transitions (fade/slide via Compose Navigation)
- Loading states for Room database reads
- Error handling for corrupt Room data (reset to defaults)
- Consistent Olympus gold/purple/dark theme across all screens
- God-specific accent colours for win highlights
- Coin count-up animation using `animateIntAsState`

**Exit Criteria:**
- [ ] All edge cases handled gracefully
- [ ] No crashes across 50 consecutive spins
- [ ] Screen transitions are smooth
- [ ] State persists through configuration changes
- [ ] Theme is consistent across all screens

---

### D3: QA & Delivery <!-- id: qa-delivery -->
> **Goal**: Run acceptance criteria verification and build client APK.

**Duration**: 2 Hours

**Acceptance Criteria Checklist:**
| Criterion | Verification Method |
|-----------|-------------------|
| Weighted reel produces Zeus ~3% and Hermes ~25% over 100 spins | Log 100 spin outcomes, verify distribution |
| Hades wild correctly substitutes to form winning matches | Test all 7 god combinations with Hades present |
| Reels stop in staggered order (R1→R2→R3) every spin | Visual QA, 10 spins |
| Zeus jackpot screen triggers only on 3× Zeus match | Force Zeus outcome in debug, verify screen |
| Apollo double applies to next paid spin only, then resets | Play 3 spins after Apollo win, verify |
| Free spins decrement correctly and cost 0 coins | Trigger Poseidon, use all 3 free spins |
| Daily bonus available exactly 24 hours after last claim | Set device clock forward, verify |
| Coin balance persists after app kill and restart | Kill app, reopen, verify balance unchanged |
| Coin floor prevents spin when balance < 10 coins | Drain balance manually, verify block |
| No crashes across 50 consecutive spins | QA automated tap test |
| All 7 god assets and 5 backgrounds render correctly on device | Visual check on real Android device |

**Debug Helpers (remove before release):**
```kotlin
// Force specific reel outcomes for testing
object DebugReelEngine {
    var forceOutcome: Triple<God, God, God>? = null
    fun spinAllReels(): Triple<God, God, God> =
        forceOutcome ?: ReelEngine.spinAllReels()
}

// Quick coin grant for testing economy
fun debugGrantCoins(amount: Int) { /* ... */ }
```

**Build Tasks:**
- [ ] Remove all debug helpers
- [ ] ProGuard/R8 minification enabled
- [ ] Signed release APK/AAB generated
- [ ] APK size < 30 MB (static assets only)
- [ ] Test on real device (API 26+ minimum)

**Exit Criteria:**
- [ ] All 11 acceptance criteria pass
- [ ] Signed APK builds successfully
- [ ] App runs on real Android device (API 26+)
- [ ] No crashes, no ANRs
- [ ] Client APK delivered

---

## 📊 Timeline Summary

```
Day 1 (AM)  ████████████  Phase A1: Project Setup + A2: Data Layer
Day 1 (PM)  ████████████  Phase A3: Game Engine + A4: ViewModels
Day 2 (AM)  ████████████  Phase B1: Splash + B2: Home Screen
Day 2 (PM)  ████████████  Phase B3: Slot Screen + B4: Win Screen + B5: Jackpot
Day 3 (AM)  ████████████  Phase C1: God Powers + C2: Daily Bonus + C3: Leaderboard
Day 3 (PM)  ████████████  Phase C4-C6: Free Spins + Apollo Double + Coin Economy
Day 3.5     ████████████  Phase D1-D3: Audio, Polish, QA, APK Delivery
```

**Total Duration: 3.5 Days**

---

## 🎯 Success Metrics

| Metric | Target |
|--------|--------|
| Frame Rate | 60 FPS on mid-range device |
| Load Time | < 2 seconds to Home Screen |
| APK Size | < 30 MB |
| Spin Cycle | < 2 seconds (tap → result) |
| Crash Rate | 0 crashes in 50 consecutive spins |
| Zeus Frequency | ~3% per reel over 100 spins |
| Hermes Frequency | ~25% per reel over 100 spins |
| Coin Persistence | 100% across app kill/restart |

---

## ⚠️ Risk Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Reel animation jank | Medium | High | Use `Animatable` with `tween`, avoid recomposition in hot path |
| Room DB corruption | Low | High | Default values in entity, fallback to 500 coins on error |
| Asset loading lag | Low | Medium | Preload all 12 drawables in SplashScreen |
| Free spin infinite loop | Medium | Medium | Cap Hermes reshuffles at 3 per spin |
| Coin economy exploits | Low | Low | Server-less game, local-only — acceptable for v1.0 |
| Compose Canvas perf | Low | Medium | Particle effects capped at 50 particles, simple draw calls only |

---

## 🚫 Scope Boundaries

### In Scope (v1.0)
- 3-reel slot machine with 7 weighted god symbols
- All 7 god special powers with unique animations
- Staggered reel stop animation (600 / 800 / 1000ms)
- Background cycling based on win result
- Virtual coin economy with daily bonus
- Win streak bonus (5 consecutive wins)
- Free spins system (Poseidon, Hermes, Demeter, Hades)
- Apollo double-win mechanic
- Hades wild symbol substitution
- Zeus jackpot full-screen celebration
- Local leaderboard (Room, top 10 coin balances)
- Coin floor safety net (never locked out)
- Sound effects and haptic feedback

### Out of Scope (v1.0)
| Excluded | Reason |
|----------|--------|
| Real money / IAP | Not requested, Play Store compliance |
| Online leaderboard | Backend required |
| 5-reel mode | Scope creep, v2 feature |
| Multiple paylines | Keeps mechanics clean for v1 |
| Tournament mode | Post-launch |
| Social sharing | Post-launch |
| Animated god sprites | Static PNG sufficient for v1 |
| AdMob integration | v2.0 feature per PRD |

---

*Document Version: 1.0*
*Last Updated: March 2026*
*Project: Olympus Slots — Kotlin + Jetpack Compose*
