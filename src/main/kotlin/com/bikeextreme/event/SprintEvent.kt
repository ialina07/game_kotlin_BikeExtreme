package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class SprintEvent : Event {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        // спринт требует энергию
        if (state.energy > 0) {
            context.movementBonus += 3
            val newEnergy = state.energy - 1
            val safeEnergy = if (newEnergy < 0) 0 else newEnergy
            return state.copy(energy = safeEnergy)
        }
        // если энергии нет, спринт не срабатывает
        return state
    }
}