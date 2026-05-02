package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.weather.WeatherFactory

class WeatherPhase(
    private val weatherFactory: WeatherFactory
) : Phase {
    override fun execute(state: PlayerState, context: PhaseContext): PlayerState {
        val weather = weatherFactory.getWeather(context.dice1)
        return weather.apply(state, context)
    }
}