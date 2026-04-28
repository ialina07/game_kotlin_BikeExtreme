package com.bikeextreme.game

import com.bikeextreme.domain.Game
import com.bikeextreme.domain.GameStatus
import com.bikeextreme.domain.Move
import com.bikeextreme.domain.Player
import com.bikeextreme.domain.PlayerState
import com.bikeextreme.repository.GameRepository
import java.util.UUID

class GameManager(
    private val moveValidator: MoveValidator,
    private val phaseExecutor: PhaseExecutor,
    private val repository: GameRepository
) {
    private var currentGameId: UUID? = null
    private var currentPlayerId: UUID? = null
    private val snapshots: MutableMap<UUID, PlayerState> = mutableMapOf()
    private var currentTurn: Int = 0
    private var isGameOver: Boolean = false
    private var winnerId: UUID? = null

    fun startGame(playerNames: List<String>): Game {
        val players  = mutableListOf<Player>()
        for (name in playerNames) {
            val player = Player(name = name)
            players.add(player)
        }

        for (player in players) {
            repository.savePlayer(player)
        }

        // создаем игру
        val playerIds = mutableListOf<UUID>()
        for (player in players) {
            playerIds.add(player.id)
        }

        val game = Game(
            playerIds = playerIds,
            status = GameStatus.IN_PROGRESS
        )
        repository.saveGame(game)
        currentGameId = game.id

        snapshots.clear()

        // инициализруем начальное состояние для каждого игрока
        for (player in players) {
            val initialState = PlayerState()
            snapshots[player.id] = initialState
        }

        // первый игрок начинает
        if (players.isNotEmpty()) {
            currentPlayerId = players[0].id
        }
        currentTurn = 1
        isGameOver = false
        winnerId = null

        return game
    }

    fun recordMove(
        playerId: UUID,
        dice1: Int,
        dice2: Int,
        moveType: String,
        restType: RestType?,
        stateBefore: PlayerState
    ): Boolean {
        if (isGameOver) {
            println("Игра уже закончена")
            return false
        }

        val currentId = currentPlayerId
        if (currentId == null) {
            println("Ошибка: нет текущего игрока")
            return false
        }

        if (playerId != currentId) {
            println("Ошибка: сейчас ходит другой игрок")
            return false
        }

        val gameId = currentGameId
        if (gameId == null) {
            println("Ошибка: нет текущей игры")
            return false
        }

        // Временно создаём Move для валидации
        val tempMove = Move(
            gameId = gameId,
            playerId = playerId,
            turnNumber = currentTurn,
            dice1 = dice1,
            dice2 = dice2,
            moveType = moveType,
            restType = restType,
            stateBefore = stateBefore,
            stateAfter = stateBefore, // будет вычислено во время валидации
            isValid = false
        )

        // валидация хода
        val isValid = moveValidator.validate(tempMove, currentId, snapshots)

        if (isValid == false) {
            println("Ход невалидный")
            return false
        }

        // получаем ожидаемое состояние из валидатора
        val expectedState = moveValidator.getExpectedState(tempMove, snapshots)
        if (expectedState == null) {
            println("Ошибка: не удалось вычислить состояние")
            return false
        }

        // создаем окончательный Move
        val move = tempMove.copy(
            stateAfter = expectedState,
            isValid = true
        )

        repository.saveMove(move)

        // обновляем состояние игрока
        snapshots[move.playerId] = expectedState

        // проверяем, не победил ли игрок
        if (expectedState.position >= PlayerState.TRACK_LENGTH) {
            isGameOver = true
            winnerId = playerId

            // Обновляем игру в репозитории
            val game = repository.getGame(gameId)
            if (game != null) {
                val updatedGame = game.copy(winnerId = playerId, isFinished = true)
                repository.saveGame(updatedGame)
            }

            val winner = repository.getPlayer(playerId)
            println("Победитель: ${winner?.name}")
            return true
        }

        val game = repository.getGame(gameId)
        if (game == null) {
            println("Ошибка: игра не найдена")
                return false
        }

        val playersInGame = game.playerIds
        val currentIndex = playersInGame.indexOf(currentId)
        val nextIndex = (currentIndex + 1) % playersInGame.size
        currentPlayerId = playersInGame[nextIndex]
        currentTurn = currentTurn + 1

        return true
    }

    fun getCurrentState(): Map<UUID, PlayerState> {
        // создаем новую карту и копируем все элементы
        val result = mutableMapOf<UUID, PlayerState>()
        for ((playerId, state) in snapshots) {
            result[playerId] = state
        }
        return result
    }

    fun getCurrentPlayerId(): UUID? = currentPlayerId

    fun isGameFinished(): Boolean = isGameOver

    fun getWinnerId(): UUID? = winnerId
}