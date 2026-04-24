package com.bikeextreme.ui

import com.bikeextreme.domain.Move
import com.bikeextreme.game.GameManager
import com.bikeextreme.repository.GameRepository
import com.bikeextreme.statistics.ReplayService
import com.bikeextreme.statistics.StatisticsService
import java.util.UUID

class ConsoleUI(
    private val gameManager: GameManager,
    private val statisticsService: StatisticsService,
    private val replayService: ReplayService,
    private val repository: GameRepository
) {
    private var currentGameId: UUID? = null

    fun start() {
        println("BikeExtreme")

        while (true) {
            println("\n--- МЕНЮ ---")
            println("1. Создать новую игру")
            println("2. Ввести ход")
            println("3. Показать текущее состояние")
            println("4. Статистика игрока")
            println("5. Таблица лидеров")
            println("6. Повтор партии")
            println("0. Выход")
            print("> ")

            val command = readln()

            if (command == "1") {
                createGame()
            } else if (command == "2") {
                inputMove()
            } else if (command == "3") {
                showGameState()
            } else if (command == "4") {
                showPlayerStats()
            } else if (command == "5") {
                showLeaderboard()
            } else if (command == "6") {
                showReplay()
            } else if (command == "0") {
                exit()
                return
            } else {
                println("Неизвестная команда. Попробуйте снова.")
            }
        }
    }

    private fun createGame() {
        println("Введите имена игроков через запятую")
        val input = readln()
        val parts = input.split(",")

        val playerNames = mutableListOf<String>()
        for (part in parts) {
            if (part != "") {
                playerNames.add(part)
            }
        }

        if (playerNames.size < 2) {
            println("Ошибка: нужно как минимум 2 игрока")
            return
        }

        val game = gameManager.startGame(playerNames)
        currentGameId = game.id
        println("Игра создана! ID: ${game.id}")
        println("Игроки: $playerNames")

        showGameState()
     }

    private fun inputMove() {
        if (currentGameId == null) {
            println("Ошибка: сначала создайте игру (команда 1)")
            return
        }

        val currentPlayerId = gameManager.getCurrentPlayerId()
        if (currentPlayerId == null) {
            println("Ошибка: нет текущего игрока")
            return
        }

        val player = repository.getPlayer(currentPlayerId)
        var playerName = ""
        if (player != null) {
            playerName = player.name
        }

        println("Сейчас ходит: $playerName")
        println("Введите данные хода:")

        print("  dice1 (погода, 1-6):")
        val dice1 = readln().toIntOrNull()
        if (dice1 == null) {
            println("Ошибка: введите число")
            return
        }
        if (dice1 < 1 || dice1 > 6) {
            println("Ошибка: введите число от 1 до 6")
            return
        }

        print("  dice2 (событие, 1-6):")
        val dice2 = readln().toIntOrNull()
        if (dice2 == null) {
            println("Ошибка: введите число")
            return
        }
        if (dice2 < 1 || dice2 > 6) {
            println("Ошибка: введите число от 1 до 6")
            return
        }

        print("  тип хода (move/rest): ")
        val moveType = readln()

        if (moveType != "move" && moveType != "rest") {
            println("Ошибка: тип хода должен быть 'move' или 'rest'")
            return
        }

        var restType: String? = null
        if (moveType == "rest") {
            print("  тип отдыха (energy/condition/water): ")
            restType = readln()
            if (restType != "energy" && restType != "condition" && restType != "water") {
                println("Ошибка: тип отдыха должен быть 'energy', 'condition' или 'water'")
                return
            }
        }

        // получаем текущее состояние игрока
        val currentStateMap = gameManager.getCurrentState()
        val currentState = currentStateMap[currentPlayerId]
        if (currentState == null) {
            println("Ошибка: состояние игрока не найдено")
            return
        }

        // получаем количество ходов (для номера хода)
        val gameId = currentGameId
        if (gameId == null) {
            println("Ошибка: нет текущей игры")
            return
        }
        val moves = repository.getMoves(gameId)
        val turnNumber = moves.size + 1

        // создаем объект Move
        val move = Move(
            gameId = gameId,
            playerId = currentPlayerId,
            turnNumber = turnNumber,
            dice1 = dice1,
            dice2 = dice2,
            moveType = moveType,
            restType = restType,
            stateBefore = currentState,
            stateAfter = currentState,
            isValid = false
        )

        val success = gameManager.recordMove(move)

        if (success) {
            println("Ход принят!")
        } else {
            println("Ход НЕ принят!")
        }
    }

    private fun showGameState() {
        if (currentGameId == null) {
            println("Ошибка: сначала создайте игру (команда 1)")
            return
        }

        val state = gameManager.getCurrentState()

        println("\n ТЕКУЩЕЕ СОСТОЯНИЕ ")
        for (entry in state) {
            val playerId = entry.key
            val playerState = entry.value
            val player = repository.getPlayer(playerId)

            var playerName = ""
            if (player != null) {
                playerName = player.name
            }

            println("$playerName:")
            println("  позиция: ${playerState.position}")
            println("  энергия: ${playerState.energy}")
            println("  состояние велосипеда: ${playerState.condition}")
            println("  вода: ${playerState.water}")
        }

        if (gameManager.isGameFinished()) {
            val winnerId = gameManager.getWinnerId()
            if (winnerId != null) {
                val winner = repository.getPlayer(winnerId)
                var winnerName = ""
                if (winner != null) {
                    winnerName = winner.name
                }
                println("Игра закончена! Победитель: $winnerName")
            }
        } else {
            val currentId = gameManager.getCurrentPlayerId()
            if (currentId != null) {
                val currentPlayer = repository.getPlayer(currentId)
                var currentName = ""
                if (currentPlayer != null) {
                    currentName = currentPlayer.name
                }
                println("Сейчас ходит: $currentName")
            }
        }
    }

    private fun showPlayerStats() {
        print("Введите имя игрока: ")
        val playerName = readln()

        val stats = statisticsService.getPlayerStats(playerName)
        if (stats == null) {
            println("Игрок '$playerName' не найден или ещё не играл")
            return
        }

        println("\n--- СТАТИСТИКА ИГРОКА: $playerName ---")
        println("Всего игр: ${stats.totalGames}")
        println("Побед: ${stats.wins}")
        println("Процент побед: ${stats.winRate}%")
    }

    private fun showLeaderboard() {
        val leaderboard = statisticsService.getLeaderboard()
        if (leaderboard.isEmpty()) {
            println("Таблица лидеров пуста")
            return
        }
        println("\n--- ТАБЛИЦА ЛИДЕРОВ ---")
        println("Место | Игрок | Победы | Win Rate")

        var index = 1
        for (ranking in leaderboard) {
            println("$index. | ${ranking.playerName} | ${ranking.wins} | ${ranking.winRate}%")
            index = index + 1
        }
    }

    private fun showReplay() {
        println("Введите ID партии для повтора:")
        val input = readln()

        val gameId = try {
            UUID.fromString(input)
        } catch (error: Exception) {
            println("Ошибка: неверный формат ID")
            return
        }

        replayService.replayGame(gameId)
    }

    private fun exit() {
        println("До свидания!")
    }
}