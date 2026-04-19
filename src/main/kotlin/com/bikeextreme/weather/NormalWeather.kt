package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState

class NormalWeather : Weather {
    override fun apply(state: PlayerState): PlayerState {
        return state
    }
}