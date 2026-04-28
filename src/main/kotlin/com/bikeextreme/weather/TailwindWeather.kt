package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class TailwindWeather : Weather {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        context.movementBonus += extraCells()
        return state
    }

    fun extraCells(): Int = 2
}