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

        // проверяем, что stateBefore совпадает с тем, что пришло в move
        if (stateBefore != move.stateBefore) {
            println("Ошибка: состояние игрока не совпадает с текущим")
            return false
        }

        return true
    }

    fun getExpectedState(
        move: Move,
        currentSnapshots: Map<UUID, PlayerState>
    ): PlayerState? {
        val stateBefore = currentSnapshots[move.playerId] ?: return null

        val context = PhaseContext(
            dice1 = move.dice1,
            dice2 = move.dice2,
            moveType = move.moveType,
            restType = move.restType,
            tailwindBonus = false,
            movementBonus = 0
        )

        return phaseExecutor.executePhases(stateBefore, context)
    }
}