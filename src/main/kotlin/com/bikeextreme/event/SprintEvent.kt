package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class SprintEvent : Event {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        // спринт требует энергию
        if (state.energy > 0) {
            context.movementBonus += 3
            return state.copy(energy = state.energy - 1)
        }
        // если энергии нет, спринт не срабатывает
        return state
    }
}