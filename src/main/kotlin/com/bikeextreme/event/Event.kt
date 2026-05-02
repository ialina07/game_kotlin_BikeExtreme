package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

/**
 * Интерфейс для игровых событий.
 * События происходят после броска второго кубика (dice2).
 * Каждое событие может изменять состояние игрока или добавлять бонус к движению.
 */
interface Event {
    /**
     * Применяет эффект события к состоянию игрока.
     * @param state состояние игрока до применения события
     * @param context контекст хода (содержит dice1, dice2, бонусы)
     * @return новое состояние игрока после применения события
     */
    fun apply(state: PlayerState, context: PhaseContext): PlayerState
}