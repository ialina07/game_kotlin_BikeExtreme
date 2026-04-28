package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext
import org.junit.Assert
import org.junit.Test

class WeatherTest {

    private val context = PhaseContext(
        dice1 = 1,
        dice2 = 1,
        moveType = "move",
        restType = null,
        movementBonus = 0
    )

    @Test
    fun testHeatWeather() {
        val state = PlayerState(water = 3, energy = 5)
        val weather = HeatWeather()
        val result = weather.apply(state, context)

        Assert.assertEquals(2, result.water)
        Assert.assertEquals(5, result.energy)
    }

    @Test
    fun testHeatWeatherWithoutWater() {
        val state = PlayerState(water = 0, energy = 5)
        val weather = HeatWeather()
        val result = weather.apply(state, context)

        Assert.assertEquals(0, result.water)
        Assert.assertEquals(4, result.energy)
    }

    @Test
    fun testRainWeather() {
        val state = PlayerState(condition = 5)
        val weather = RainWeather()
        val result = weather.apply(state, context)

        Assert.assertEquals(4, result.condition)
    }

    @Test
    fun testNormalWeather() {
        val state = PlayerState(position = 10, water = 3, condition = 5, energy = 5)
        val weather = NormalWeather()
        val result = weather.apply(state, context)

        Assert.assertEquals(state.position, result.position)
        Assert.assertEquals(state.water, result.water)
        Assert.assertEquals(state.condition, result.condition)
        Assert.assertEquals(state.energy, result.energy)
    }

    @Test
    fun testTailwindWeather() {
        val state = PlayerState(position = 10)
        val weather = TailwindWeather()
        val result = weather.apply(state, context)

        // ветер не меняет состояние, только дает бонус в MovementPhase
        Assert.assertEquals(10, result.position)
    }

    @Test
    fun testStormWeather() {
        val state = PlayerState(condition = 5)
        val weather = StormWeather()
        val result = weather.apply(state, context)

        Assert.assertEquals(3, result.condition)
    }

    @Test
    fun testWeatherZero() {
        val state = PlayerState(condition = 0, energy = 0, water = 0)

        val rain = RainWeather()
        val result1 = rain.apply(state, context)
        Assert.assertEquals(0, result1.condition)

        val storm = StormWeather()
        val result2 = storm.apply(state, context)
        Assert.assertEquals(0, result2.condition)

        val heat = HeatWeather()
        val result3 = heat.apply(state, context)
        Assert.assertEquals(0, result3.energy)
    }

    @Test
    fun testTailwindWeatherExtraCells() {
        val weather = TailwindWeather()
        Assert.assertEquals(2, weather.extraCells())
    }
}