package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState

class RestPhase : Phase {
    override fun execute(state: PlayerState, context: PhaseContext): PlayerState {
        if (context.moveType != "rest")
            return state

        return when (context.restType) {
            "energy" -> {
                var newEnergy = state.energy + 2
                if (newEnergy > PlayerState.MAX_ENERGY)
                    newEnergy = PlayerState.MAX_ENERGY
                state.copy(energy = newEnergy)
            }

            "condition" -> {
                var newCondition = state.condition + 1
                if (newCondition > PlayerState.MAX_CONDITION)
                    newCondition = PlayerState.MAX_CONDITION
                state.copy(condition = newCondition)
            }

            "water" -> {
                var newWater = state.water + 3
                if (newWater > PlayerState.MAX_WATER)
                    newWater = PlayerState.MAX_WATER
                state.copy(water = newWater)
            }
            else -> state
        }
    }
}