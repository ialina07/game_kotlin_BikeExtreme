package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState

class SnackEvent : Event {
    override fun apply(state: PlayerState): PlayerState {
        val newEnergy = state.energy + 1
        val safeEnergy = if (newEnergy > PlayerState.MAX_ENERGY) PlayerState.MAX_ENERGY else newEnergy
        return state.copy(energy = safeEnergy)
    }
}