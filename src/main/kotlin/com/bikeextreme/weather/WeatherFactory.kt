package com.bikeextreme.weather

class WeatherFactory {
    fun getWeather(dice: Int): Weather {
        return when (dice) {
            1 -> HeatWeather()
            2 -> RainWeather()
            3, 4 -> NormalWeather()
            5 -> TailwindWeather()
            6 -> StormWeather()
            else -> NormalWeather()
        }
    }
}
