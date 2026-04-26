package com.bikeextreme.game

import com.bikeextreme.domain.Move
import com.bikeextreme.domain.PlayerState
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import org.junit.Test
import org.junit.Assert.*
import java.util.UUID

class MoveValidatorTest {

    private fun createValidator(): MoveValidator {
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
        return MoveValidator(phaseExecutor)
    }

    @Test
    fun testWrongPlayer() {
        val validator = createValidator()

        val playerA = UUID.randomUUID()
        val move = Move(
            gameId = UUID.randomUUID(),
            playerId = playerA,
            turnNumber = 1,
            dice1 = 3,
            dice2 = 4,
            moveType = "move",
            restType = null,
            stateBefore = PlayerState(),
            stateAfter = PlayerState(),
            isValid = false
        )

        val playerB = UUID.randomUUID()
        val snapshots = mapOf(playerB to PlayerState())

        val result = validator.validate(move, playerB, snapshots)

        assertFalse(result)
    }

    @Test
    fun testCorrectPlayer() {
        val validator = createValidator()

        val playerId = UUID.randomUUID()
        val state = PlayerState(position = 10, energy = 5, condition = 5, water = 3)

        val move = Move(
            gameId = UUID.randomUUID(),
            playerId = playerId,
            turnNumber = 1,
            dice1 = 3,
            dice2 = 4,
            moveType = "move",
            restType = null,
            stateBefore = state,
            stateAfter = state,
            isValid = false
        )

        val snapshots = mapOf(playerId to state)

        val result = validator.validate(move, playerId, snapshots)

        assertTrue(result)
    }
}