package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState

interface Phase {
    fun execute(state: PlayerState, context: PhaseContext): PlayerState
}