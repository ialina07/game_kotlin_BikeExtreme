package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState

class RainWeather : Weather {
    override fun apply(state: PlayerState): PlayerState {
        val newCondition = state.condition - 1
        val safeCondition = if (newCondition < 0) 0 else newCondition
        return state.copy(condition = safeCondition)
    }
}