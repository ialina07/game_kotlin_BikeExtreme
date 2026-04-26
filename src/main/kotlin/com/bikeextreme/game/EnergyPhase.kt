package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState

class EnergyPhase : Phase {
    override fun execute(state: PlayerState, context: PhaseContext): PlayerState {
        // во время отдыха энергия не тратится
        if (context.moveType != "move") {
            return state
        }

        // при движении тратим 1 энергию
        var newEnergy = state.energy - 1
        if (newEnergy < 0) {
            newEnergy = 0
        }

        // если энергии нет, проезжаем половину клеток
        var newPosition = state.position
        if (state.energy == 0) {
            newPosition = state.position / 2
        }

        return state.copy(energy = newEnergy, position = newPosition)
    }
}