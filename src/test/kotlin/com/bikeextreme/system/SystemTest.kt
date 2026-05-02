package com.bikeextreme.system

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.*
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.InMemoryGameRepository
import com.bikeextreme.statistics.StatisticsService
import org.junit.Test
import org.junit.Assert.*

class SystemTests {

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
    fun testFullGameUntilWinner() {
        val repository = InMemoryGameRepository()
        val gameManager = createGameManager(repository)

        val game = gameManager.startGame(listOf("Анна", "Вика"))
        val annaId = game.playerIds[0]
        val vikaId = game.playerIds[1]

        var annaState = PlayerState(position = 0, energy = 100)
        var vikaState = PlayerState(position = 0, energy = 100)

        // 10 ходов достаточно для победы Анны
        for (i in 1..10) {
            // Ход Анны
            val successAnna = gameManager.recordMove(
                playerId = annaId,
                dice1 = 3, dice2 = 3,
                moveType = "move", restType = null,
                stateBefore = annaState
            )
            assertTrue(successAnna)
            val newAnnaState = gameManager.getCurrentState()[annaId]
            if (newAnnaState == null) {
                fail("Состояние Анны не найдено")
                return
            }
            annaState = newAnnaState

            if (gameManager.isGameFinished()) break

            // Ход Вики
            val successVika = gameManager.recordMove(
                playerId = vikaId,
                dice1 = 1, dice2 = 1,
                moveType = "move", restType = null,
                stateBefore = vikaState
            )
            assertTrue(successVika)
            val newVikaState = gameManager.getCurrentState()[vikaId]
            if (newVikaState == null) {
                fail("Состояние Вики не найдено")
                return
            }
            vikaState = newVikaState
        }

        assertTrue(gameManager.isGameFinished())
        assertEquals(annaId, gameManager.getWinnerId())
    }

    @Test
    fun testGameWithStatistics() {
        val repository = InMemoryGameRepository()
        val gameManager = createGameManager(repository)

        val game = gameManager.startGame(listOf("Анна", "Вика"))
        val annaId = game.playerIds[0]
        val vikaId = game.playerIds[1]

        var annaState = PlayerState(position = 0, energy = 100)
        var vikaState = PlayerState(position = 0, energy = 100)

        while (!gameManager.isGameFinished()) {
            val successAnna = gameManager.recordMove(
                playerId = annaId,
                dice1 = 3, dice2 = 3,
                moveType = "move", restType = null,
                stateBefore = annaState
            )
            assertTrue(successAnna)
            val newAnnaState = gameManager.getCurrentState()[annaId]
            if (newAnnaState == null) {
                fail("Состояние Анны не найдено")
                return
            }
            annaState = newAnnaState

            if (gameManager.isGameFinished()) break

            val successVika = gameManager.recordMove(
                playerId = vikaId,
                dice1 = 1, dice2 = 1,
                moveType = "move", restType = null,
                stateBefore = vikaState
            )
            assertTrue(successVika)
            val newVikaState = gameManager.getCurrentState()[vikaId]
            if (newVikaState == null) {
                fail("Состояние Вики не найдено")
                return
            }
            vikaState = newVikaState
        }

        assertTrue(gameManager.isGameFinished())
        assertEquals(annaId, gameManager.getWinnerId())

        val statisticsService = StatisticsService(repository)
        val annaStats = statisticsService.getPlayerStats("Анна")
        assertNotNull(annaStats)
        annaStats?.let { stats ->
            assertEquals(1, stats.wins)
            assertEquals(1, stats.totalGames)
            assertEquals(100.0, stats.winRate, 0.01)
        }
    }
}
