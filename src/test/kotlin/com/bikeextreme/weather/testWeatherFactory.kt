package com.bikeextreme.weather

import org.junit.Test
import org.junit.Assert.*

class WeatherFactoryTest {

    private val factory = WeatherFactory()

    @Test
    fun getWeatherByDice() {
        assertTrue(factory.getWeather(1) is HeatWeather)
        assertTrue(factory.getWeather(2) is RainWeather)
        assertTrue(factory.getWeather(3) is NormalWeather)
        assertTrue(factory.getWeather(4) is NormalWeather)
        assertTrue(factory.getWeather(5) is TailwindWeather)
        assertTrue(factory.getWeather(6) is StormWeather)
    }

    @Test
    fun getWeatherWithInvalidDice() {
        assertTrue(factory.getWeather(7) is NormalWeather)
        assertTrue(factory.getWeather(0) is NormalWeather)
    }
}