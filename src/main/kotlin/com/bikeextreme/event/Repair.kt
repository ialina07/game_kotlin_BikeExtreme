package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState

class RepairEvent : Event {
    override fun apply(state: PlayerState): PlayerState {
        val newCondition = state.condition + 2
        val safeCondition = if (newCondition > PlayerState.MAX_CONDITION) PlayerState.MAX_CONDITION else newCondition
        return state.copy(condition = safeCondition)
    }
}