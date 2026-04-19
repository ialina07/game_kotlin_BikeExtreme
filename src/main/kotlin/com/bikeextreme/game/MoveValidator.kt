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
        // 1) проверка очереди
        if (move.playerId != currentPlayerId) {
            println("Ошибка: сейчас ходит другой игрок")
            return false
        }

        // 2) берем состояние игрока до хода
        val stateBefore = currentSnapshots[move.playerId] ?: return false

        // 3) создаем контекст
        val context = PhaseContext(
            dice1 = move.dice1,
            dice2 = move.dice2,
            moveType = move.moveType,
            restType = move.restType,
            tailwindBonus = false
        )

        // 4) применяем фазы к состоянию до
        val expectedState = phaseExecutor.executePhases(stateBefore, context)

        // 5) сравниваем с состоянием после
        if (expectedState != move.stateAfter) {
            println("Ошибка: ожидалось $expectedState, получено ${move.stateAfter}")
            return false
        }

        return true
    }
}