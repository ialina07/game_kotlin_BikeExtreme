package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class DownhillEvent : Event {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        context.movementBonus += 2
        return state
    }
}