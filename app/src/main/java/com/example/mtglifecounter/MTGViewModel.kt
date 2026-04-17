package com.example.mtglifecounter

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class PlayerData(
    val id: Int,
    val life: Int,
    val dieRoll: Int? = null,
    val isWinner: Boolean = false,
    val color: Color,
    val secondaryColor: Color
)

data class GameState(
    val players: List<PlayerData> = emptyList(),
    val playerCount: Int = 4,
    val initialLife: Int = 40
)

class MTGViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var diceTimerJob: Job? = null

    private val mtgThemes = listOf(
        Pair(Color(0xFFD3202A), Color(0xFF4A0E0E)), // Red
        Pair(Color(0xFF0E68AB), Color(0xFF072B45)), // Blue
        Pair(Color(0xFF00733E), Color(0xFF002414)), // Green
        Pair(Color(0xFF2B2522), Color(0xFF000000)), // Black
        Pair(Color(0xFFF9FAF4), Color(0xFF7A7A6A)), // White
        Pair(Color(0xFF90ADBB), Color(0xFF2E3D45))  // Artifact
    )

    init {
        setupGame(4, 40)
    }

    fun setupGame(count: Int, initialLife: Int) {
        diceTimerJob?.cancel()
        val players = (1..count).map { id ->
            val theme = mtgThemes[(id - 1) % mtgThemes.size]
            PlayerData(
                id = id,
                life = initialLife,
                color = theme.first,
                secondaryColor = theme.second
            )
        }
        _gameState.update { it.copy(players = players, playerCount = count, initialLife = initialLife) }
    }

    fun updateLife(playerId: Int, delta: Int) {
        _gameState.update { state ->
            state.copy(players = state.players.map { player ->
                if (player.id == playerId) player.copy(life = player.life + delta) else player
            })
        }
    }

    fun rollAllDice() {
        diceTimerJob?.cancel()
        
        var rolls: List<Int>
        var maxRoll: Int
        var winners: List<Int>

        // Re-roll until there is a single winner with the highest number
        do {
            rolls = List(_gameState.value.playerCount) { Random.nextInt(1, 7) }
            maxRoll = rolls.maxOrNull() ?: 0
            winners = rolls.indices.filter { rolls[it] == maxRoll }
        } while (winners.size > 1)

        _gameState.update { state ->
            state.copy(players = state.players.mapIndexed { index, player ->
                val roll = rolls[index]
                player.copy(
                    dieRoll = roll,
                    isWinner = rolls[index] == maxRoll
                )
            })
        }

        // Auto-clear after 5 seconds
        diceTimerJob = viewModelScope.launch {
            delay(5000)
            _gameState.update { state ->
                state.copy(players = state.players.map { it.copy(dieRoll = null, isWinner = false) })
            }
        }
    }

    fun resetGame() {
        setupGame(_gameState.value.playerCount, _gameState.value.initialLife)
    }
}
