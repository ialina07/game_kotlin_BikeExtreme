package com.bikeextreme.game

import com.bikeextreme.domain.Move
import com.bikeextreme.domain.PlayerState
import java.util.UUID

class MoveValidator(
    private val phaseExecutor: PhaseExecutor
) {
    fun validate(
        move: Move,
        currentPlayerId: UUID,
        currentSnapshots: Map<UUID, PlayerState>
    ) : Boolean {
        // проверка очереди
        if (move.playerId != currentPlayerId) {
            println("Ошибка: сейчас ходит другой игрок")
            return false
        }

        // проверяем, что состояние игрока существует
        val stateBefore = currentSnapshots[move.playerId]
        if (stateBefore == null) {
            println("Ошибка: состояние игрока не найдено")
            return false
        }

        // проверяем корректность бросков кубиков и типа хода
        if (move.dice1 !in 1..6 || move.dice2 !in 1..6) {
            println("Ошибка: значения кубиков должны быть от 1 до 6")
            return false
        }
        if (move.moveType !in listOf("move", "rest")) {
            println("Ошибка: тип хода должен быть 'move' или 'rest'")
            return false
        }

        return true
    }
}