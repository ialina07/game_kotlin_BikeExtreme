package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState

class TailwindWeather : Weather {
    override fun apply(state: PlayerState): PlayerState {
        // ветер дает бонус к движению, состояние игрока не меняется
        return state
    }
}