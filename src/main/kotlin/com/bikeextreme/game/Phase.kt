package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState

/**
 * Интерфейс для всех фаз хода.
 * Фазы применяются последовательно в PhaseExecutor.
 * Каждая фаза отвечает за одну часть игровой логики:
 * - WeatherPhase: погода
 * - EventPhase: случайные события
 * - MovementPhase: движение
 * - EnergyPhase: расход энергии
 * - RestPhase: отдых
 */
interface Phase {
    /**
     * Выполняет фазу, изменяя состояние игрока.
     * @param state текущее состояние игрока
     * @param context контекст хода (содержит dice1, dice2, тип хода, бонусы)
     * @return новое состояние игрока после применения фазы
     */
    fun execute(state: PlayerState, context: PhaseContext): PlayerState
}