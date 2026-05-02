package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class RainWeather : Weather {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        val newCondition = state.condition - 1
        val safeCondition = if (newCondition < 0) 0 else newCondition
        return state.copy(condition = safeCondition)
    }
}