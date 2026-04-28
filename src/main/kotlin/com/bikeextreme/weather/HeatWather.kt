package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class HeatWeather : Weather {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        // если есть вода, -1
        if (state.water > 0) {
            return state.copy(water = state.water - 1)
        }
        // если воды нет, тратим энергию
        val newEnergy = state.energy - 1
        val safeEnergy = if (newEnergy < 0) 0 else newEnergy
        return state.copy(energy = safeEnergy)
    }
}