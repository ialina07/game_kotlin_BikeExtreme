package com.bikeextreme.integration

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.*
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.InMemoryGameRepository
import org.junit.Test
import org.junit.Assert.*

class IntegrationTests {

    private fun createGameManager(repository: InMemoryGameRepository): GameManager {
        val weatherFactory = WeatherFactory()
        val eventFactory = EventFactory()

        val phases: List<Phase> = listOf(
            WeatherPhase(weatherFactory),
            EventPhase(eventFactory),
            MovementPhase(),
            EnergyPhase(),
            RestPhase()
        )
        val phaseExecutor = PhaseExecutor(phases)
        val moveValidator = MoveValidator(phaseExecutor)

        return GameManager(moveValidator, phaseExecutor, repository)
    }

    // тест 1: GameManager + репозиторий + фазы
    @Test
    fun testGameManagerWithRepository() {
        val repository = InMemoryGameRepository()
        val gameManager = createGameManager(repository)

        val game = gameManager.startGame(listOf("Анна", "Вика"))

        val success = gameManager.recordMove(
            playerId = game.playerIds[0],
            dice1 = 3,
            dice2 = 3,
            moveType = "move",
            restType = null,
            stateBefore = PlayerState(position = 0, energy = 5)
        )

        assertTrue(success)

        val moves = repository.getMoves(game.id)
        assertEquals(1, moves.size)
    }

    // тест 2: PhaseExecutor + все фазы с отдыхом
    @Test
    fun testPhaseExecutorWithRest() {
        val weatherFactory = WeatherFactory()
        val eventFactory = EventFactory()

        val phases: List<Phase> = listOf(
            WeatherPhase(weatherFactory),
            EventPhase(eventFactory),
            MovementPhase(),
            EnergyPhase(),
            RestPhase()
        )
        val executor = PhaseExecutor(phases)

        val initialState = PlayerState(energy = 5, position = 10)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.ENERGY,
            movementBonus = 0
        )

        val result = executor.executePhases(initialState, context)

        assertEquals(7, result.energy)
        assertEquals(10, result.position)
    }
}
