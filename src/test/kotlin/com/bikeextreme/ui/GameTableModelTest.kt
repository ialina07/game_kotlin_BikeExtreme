package com.bikeextreme.ui

import com.bikeextreme.game.*
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.InMemoryGameRepository
import com.bikeextreme.ui.swing.GameTableModel
import org.junit.Test
import org.junit.Assert.*

class GameTableModelTest {

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
    fun testGetRowCount() {
        val gameManager = createGameManager()
        gameManager.startGame(listOf("Анна", "Вика"))
        val model = GameTableModel(gameManager)

        assertEquals(2, model.rowCount)
    }

    @Test
    fun testGetColumnCount() {
        val gameManager = createGameManager()
        val model = GameTableModel(gameManager)

        assertEquals(5, model.columnCount)
    }

    @Test
    fun testGetColumnName() {
        val gameManager = createGameManager()
        val model = GameTableModel(gameManager)

        assertEquals("Игрок", model.getColumnName(0))
        assertEquals("Позиция", model.getColumnName(1))
        assertEquals("Энергия", model.getColumnName(2))
        assertEquals("Состояние", model.getColumnName(3))
        assertEquals("Вода", model.getColumnName(4))
    }

    @Test
    fun testGetValueAt() {
        val gameManager = createGameManager()
        gameManager.startGame(listOf("Анна", "Борис"))
        val model = GameTableModel(gameManager)

        assertEquals("Анна", model.getValueAt(0, 0))
        assertEquals(0, model.getValueAt(0, 1))  // позиция
        assertEquals(5, model.getValueAt(0, 2))  // энергия
        assertEquals(5, model.getValueAt(0, 3))  // состояние
        assertEquals(3, model.getValueAt(0, 4))  // вода
    }

    @Test
    fun testRefresh() {
        val gameManager = createGameManager()
        gameManager.startGame(listOf("Анна", "Вика"))
        val model = GameTableModel(gameManager)

        // проверяем, что refresh не падает
        model.refresh()

        assertEquals(2, model.rowCount)
    }
}

