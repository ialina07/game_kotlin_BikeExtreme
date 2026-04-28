package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

interface Event {
    fun apply(state: PlayerState, context: PhaseContext): PlayerState
}