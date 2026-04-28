package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class PunctureEvent : Event {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        val newCondition = state.condition - 2
        val safeCondition = if (newCondition < 0) 0 else newCondition
        return state.copy(condition = safeCondition)
    }
}