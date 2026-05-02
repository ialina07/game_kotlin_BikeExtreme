package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class SpringEvent : Event {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        val newWater = state.water + 1
        val safeWater = if (newWater > PlayerState.MAX_WATER) PlayerState.MAX_WATER else newWater
        return state.copy(water = safeWater)
    }
}