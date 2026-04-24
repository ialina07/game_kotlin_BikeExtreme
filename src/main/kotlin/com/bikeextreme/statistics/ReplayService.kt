package com.bikeextreme.statistics

import com.bikeextreme.domain.Game
import com.bikeextreme.domain.Move
import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext
import com.bikeextreme.game.PhaseExecutor
import com.bikeextreme.repository.GameRepository
import java.util.UUID

class ReplayService(
    private val repository: GameRepository,
    private val phaseExecutor: PhaseExecutor
) {
    fun replayGame(gameId: UUID) {
        val game = repository.getGame(gameId)
        if (game == null) {
            println("Игра с ID $gameId не найдена")
            return
        }

        val moves = repository.getMoves(gameId)
        if (moves.isEmpty()) {
            println("У игры $gameId нет ходов")
            return
        }

        println("ПОВТОР ПАРТИИ $gameId")

        for (move in moves) {
            val playerName = getPlayerName(move.playerId)
            println("\nХод ${move.turnNumber}: $playerName")
            println("  dice1 = ${move.dice1}, dice2 = ${move.dice2}")

            if (move.moveType == "rest") {
                println("  Тип: отдых (${move.restType})")
            } else {
                println("  Тип: движение")
            }

            println(
                "Было: позиция=${move.stateBefore.position}, энергия=${move.stateBefore.energy},состояние=" + "${move.stateBefore.condition}, вода=" +
                        "${move.stateBefore.water}"
            )

            println(
                "Стало: позиция=${move.stateAfter.position}, энергия=" +
                        "${move.stateAfter.energy},состояние=${move.stateAfter.condition}, вода=" +
                        "${move.stateAfter.water}"
            )

            // проверка коррекности хода (через движок)
            val context = PhaseContext(
                dice1 = move.dice1,
                dice2 = move.dice2,
                moveType = move.moveType,
                restType = move.restType,
                tailwindBonus = false
            )

            val expectedState = phaseExecutor.executePhases(move.stateBefore, context)

            if (expectedState == move.stateAfter) {
                println("  Ход корректен")
            } else {
                println("  Ход НЕ корректен")
                println(
                    "  Ожидалось: позиция=${expectedState.position}, энергия=" +
                            "${expectedState.energy},состояние=${expectedState.condition}, вода=${expectedState.water}"
                )
            }
        }

        val winnerId = game.winnerId
        if (winnerId != null) {
            val winnerName = getPlayerName(winnerId)
            println("\n   ПОБЕДИТЕЛЬ: $winnerName")
        } else {
            println("\n   Победитель не определен")
        }
    }

    fun getMoveHistory(gameId: UUID): List<Move> {
        return repository.getMoves(gameId)
    }

    private fun getPlayerName(playerId: UUID): String {
        val player = repository.getPlayer(playerId)
        if (player == null) {
            return "Неизвестный игрок"
        } else {
            return player.name
        }
    }
}
