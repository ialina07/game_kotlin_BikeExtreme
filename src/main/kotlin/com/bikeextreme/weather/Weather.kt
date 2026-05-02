package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

/**
 * Интерфейс для всех типов погоды.
 * Погода определяется первым кубиком (dice1).
 */
interface Weather {
    /**
     * Применяет эффект погоды к состоянию игрока.
     * @param state состояние игрока до применения погоды
     * @param context контекст хода (содержит dice1, dice2, бонусы)
     * @return новое состояние игрока после применения погоды
     */
    fun apply(state: PlayerState, context: PhaseContext): PlayerState
}