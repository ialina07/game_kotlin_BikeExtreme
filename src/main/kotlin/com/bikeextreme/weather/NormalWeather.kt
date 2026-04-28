package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext

class NormalWeather : Weather {
    override fun apply(state: PlayerState, context: PhaseContext): PlayerState {
        return state
    }
}