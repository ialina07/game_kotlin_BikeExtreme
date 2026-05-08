package com.bikeextreme.ui

import com.bikeextreme.game.*
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.InMemoryGameRepository
import com.bikeextreme.ui.swing.MoveDialog
import org.junit.Test
import org.junit.Assert.*
import javax.swing.JFrame

class MoveDialogTest {

    private fun createGameManager(): GameManager {
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
        val repository = InMemoryGameRepository()
        return GameManager(moveValidator, phaseExecutor, repository)
    }

    @Test
    fun testMoveDialogCanBeCreated() {
        val gameManager = createGameManager()
        gameManager.startGame(listOf("Анна", "Вика"))

        val parent = JFrame()
        val dialog = MoveDialog(parent, gameManager)

        assertNotNull(dialog)
        parent.dispose()
    }
}