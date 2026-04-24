package com.bikeextreme.game

import com.bikeextreme.domain.Game
import com.bikeextreme.domain.Move
import com.bikeextreme.domain.Player
import com.bikeextreme.domain.PlayerState
import com.bikeextreme.repository.GameRepository
import java.util.UUID

class GameManager(
    private val moveValidator: MoveValidator,
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
            status = "IN_PROGRESS"
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

    fun recordMove(move: Move): Boolean {
        if (isGameOver) {
            println("Игра уже закончена")
            return false
        }

        val playerId = currentPlayerId
        if (playerId == null) {
            println("Ошибка: нет текущего игрока")
            return false
        }

        // валидация хода
        val isValid = moveValidator.validate(
            move = move,
            currentPlayerId = playerId,
            currentSnapshots = snapshots
        )

        if (isValid == false) {
            println("Ход невалидный")
            return false
        }

        repository.saveMove(move)

        // обновляем состояние игрока
        snapshots[move.playerId] = move.stateAfter

        // проверяем, не победил ли игрок
        val positionAfter = move.stateAfter.position
        if (positionAfter >= PlayerState.TRACK_LENGTH) {
            isGameOver = true
            winnerId = move.playerId
            println("Победитель: ${move.playerId}")
            return true
        }

        // ход следующего игрока
        val gameId = currentGameId
        if (gameId == null) {
            println("Ошибка: нет текущей игры")
            return false
        }

        val game = repository.getGame(gameId)
        if (game == null) {
            println("Ошибка: игра не найдена")
                return false
        }

        val playersInGame = game.playerIds
        val currentIndex = playersInGame.indexOf(playerId)
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