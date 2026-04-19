package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState

class SpringEvent : Event {
    override fun apply(state: PlayerState): PlayerState {
        val newWater = state.water + 1
        val safeWater = if (newWater > PlayerState.MAX_WATER) PlayerState.MAX_WATER else newWater
        return state.copy(water = safeWater)
    }
}