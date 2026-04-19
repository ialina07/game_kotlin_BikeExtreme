package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState

class SprintEvent : Event {
    override fun apply(state: PlayerState): PlayerState {
        // +3 клетки движения, -1 энергия
        val newEnergy = state.energy - 1
        val safeEnergy = if (newEnergy < 0) 0 else newEnergy
        return state.copy(energy = safeEnergy)
    }
}