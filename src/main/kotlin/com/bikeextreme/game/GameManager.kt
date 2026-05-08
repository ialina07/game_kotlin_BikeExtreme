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
        // получаем существующих игроков или создаем новых
        val players = playerNames.map { name ->
            repository.getPlayerByName(name) ?: Player(name = name)
        }

        // сохраняем новых игроков (если они были созданы)
        for (player in players) {
            if (repository.getPlayer(player.id) == null) {
                repository.savePlayer(player)
            }
        }

        // создаем игру
        val playerIds = players.map { it.id }

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

        // валидация входных данных
        val tempMove = Move(
            gameId = gameId,
            playerId = playerId,
            turnNumber = currentTurn,
            dice1 = dice1,
            dice2 = dice2,
            moveType = moveType,
            restType = restType,
            stateBefore = stateBefore,
            stateAfter = stateBefore, // будет вычислено после валидации
            isValid = false
        )

        if (!moveValidator.validate(tempMove, currentId, snapshots)) {
            println("Ход невалидный")
            return false
        }

        // вычисляем ожидаемое состояние через PhaseExecutor
        val context = PhaseContext(
            dice1 = dice1,
            dice2 = dice2,
            moveType = moveType,
            restType = restType,
            movementBonus = 0,
        )
        val expectedState = phaseExecutor.executePhases(stateBefore, context)

        // создаём окончательный Move с вычисленным состоянием
        val move = Move(
            gameId = gameId,
            playerId = playerId,
            turnNumber = currentTurn,
            dice1 = dice1,
            dice2 = dice2,
            moveType = moveType,
            restType = restType,
            stateBefore = stateBefore,
            stateAfter = expectedState,
            isValid = true
        )

        // сохраняем ход
        repository.saveMove(move)
        // обновляем состояние игрока
        snapshots[playerId] = expectedState

        // проверяем, не победил ли игрок
        if (expectedState.position >= PlayerState.TRACK_LENGTH) {
            isGameOver = true
            winnerId = playerId

            // обновляем игру в репозитории
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
        currentTurn++

        println("Ход принят!")
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

    fun getPlayer(playerId: UUID): Player? = repository.getPlayer(playerId)

    fun getCurrentGameId(): UUID? = currentGameId

}