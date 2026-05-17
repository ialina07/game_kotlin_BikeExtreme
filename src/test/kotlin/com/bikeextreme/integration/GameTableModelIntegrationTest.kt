package com.bikeextreme.integration

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.*
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.InMemoryGameRepository
import com.bikeextreme.ui.swing.GameTableModel
import org.junit.Test
import org.junit.Assert.*

class GameTableModelIntegrationTest {

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

    @Test
    fun testTableModelUpdatesAfterMove() {
        val repository = InMemoryGameRepository()
        val gameManager = createGameManager(repository)
        val game = gameManager.startGame(listOf("Анна", "Вика"))
        val annaId = game.playerIds[0]

        val model = GameTableModel(gameManager)

        // до хода — позиция 0
        assertEquals(0, model.getValueAt(0, 1))

        gameManager.recordMove(
            playerId = annaId,
            dice1 = 3,
            dice2 = 3,
            moveType = "move",
            restType = null,
            stateBefore = PlayerState(position = 0, energy = 5)
        )

        model.refresh()

        // после хода — позиция больше 0
        val newPosition = model.getValueAt(0, 1) as Int
        assertTrue(newPosition > 0)
    }
}