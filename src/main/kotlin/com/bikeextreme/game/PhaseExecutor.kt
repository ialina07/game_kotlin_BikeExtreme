package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState

class PhaseExecutor(
    private val phases: List<Phase>
) {
    fun executePhases(initialState: PlayerState, context: PhaseContext): PlayerState {
        var state = initialState
        for (phase in phases) {
            state = phase.execute(state, context)
        }
        return state
    }
}