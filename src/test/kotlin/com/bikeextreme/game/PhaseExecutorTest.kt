package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import org.junit.Test
import org.junit.Assert.*

class PhaseExecutorTest {

    private fun createPhaseExecutor(): PhaseExecutor {
        val weatherFactory = WeatherFactory()
        val eventFactory = EventFactory()

        val phases: List<Phase> = listOf(
            WeatherPhase(weatherFactory),
            EventPhase(eventFactory),
            MovementPhase(),
            EnergyPhase(),
            RestPhase()
        )
        return PhaseExecutor(phases)
    }

    @Test
    fun testExecuteAllPhasesMove() {
        val executor = createPhaseExecutor()
        val initialState = PlayerState(position = 10, energy = 5, condition = 5, water = 3)
        val context = PhaseContext(
            dice1 = 3,
            dice2 = 3,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = executor.executePhases(initialState, context)

        // движение: 3+3=6, позиция 10+6=16
        // энергия: 5+1-1=5
        assertEquals(16, result.position)
        assertEquals(5, result.energy)
        assertEquals(5, result.condition)
        assertEquals(3, result.water)
    }

    @Test
    fun testExecuteAllPhasesRest() {
        val executor = createPhaseExecutor()
        val initialState = PlayerState(energy = 5, condition = 5, water = 3)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.ENERGY,
            movementBonus = 0
        )

        val result = executor.executePhases(initialState, context)

        // отдых: энергия +2
        assertEquals(7, result.energy)
        assertEquals(5, result.condition)
        assertEquals(3, result.water)
    }

    @Test
    fun testExecuteAllPhasesWithBonus() {
        val executor = createPhaseExecutor()
        val initialState = PlayerState(position = 10, energy = 5, condition = 5, water = 3)
        val context = PhaseContext(
            dice1 = 5,  // TailwindWeather -> +2 бонус
            dice2 = 6,  // SprintEvent -> +3 бонус, -1 энергия
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = executor.executePhases(initialState, context)


        // ветер: context.movementBonus += 2
        // спринт: context.movementBonus += 3, энергия -1
        // итоговый бонус = 5
        // движение: 5+6=11, +5 = 16
        // позиция: 10+16=26
        // энергия: 5-1(спринт)-1(ход)=3
        assertEquals(26, result.position)
        assertEquals(3, result.energy)
    }

    @Test
    fun testExecuteAllPhasesWithConditionPenalty() {
        val executor = createPhaseExecutor()
        val initialState = PlayerState(position = 10, energy = 5, condition = 2, water = 3)
        val context = PhaseContext(
            dice1 = 3,
            dice2 = 3,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = executor.executePhases(initialState, context)

        // движение: 3+3=6, штраф -2 = 4, позиция 10+4=14
        // энергия: 5+1(SnackEvent) -1(ход) = 5
        assertEquals(14, result.position)
        assertEquals(5, result.energy)
        assertEquals(2, result.condition)
    }
}