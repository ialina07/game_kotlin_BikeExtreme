package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

interface Weather {
    fun apply(state: PlayerState, context: PhaseContext): PlayerState
}