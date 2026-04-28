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

        // корректировка позиции, если энергии не было
        val newPosition = if (state.energy == 0) {
            // возвращаемся на половину пройденного пути
            state.position - (context.movementThisTurn / 2)
        } else {
            state.position
        }

        return state.copy(energy = newEnergy, position = newPosition)
    }
}