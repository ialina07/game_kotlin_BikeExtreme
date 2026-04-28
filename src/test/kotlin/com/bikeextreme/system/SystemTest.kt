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
    fun testSimpleMove() {
        val repository = InMemoryGameRepository()
        val gameManager = createGameManager(repository)

        val game = gameManager.startGame(listOf("Анна", "Вика"))
        val annaId = game.playerIds[0]

        val success = gameManager.recordMove(
            playerId = annaId,
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
            annaState = gameManager.getCurrentState()[annaId]!!

            if (gameManager.isGameFinished()) break

            // Ход Вики
            val successVika = gameManager.recordMove(
                playerId = vikaId,
                dice1 = 1, dice2 = 1,
                moveType = "move", restType = null,
                stateBefore = vikaState
            )
            assertTrue(successVika)
            vikaState = gameManager.getCurrentState()[vikaId]!!
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
            annaState = gameManager.getCurrentState()[annaId]!!

            if (gameManager.isGameFinished()) break

            val successVika = gameManager.recordMove(
                playerId = vikaId,
                dice1 = 1, dice2 = 1,
                moveType = "move", restType = null,
                stateBefore = vikaState
            )
            assertTrue(successVika)
            vikaState = gameManager.getCurrentState()[vikaId]!!
        }

        assertTrue(gameManager.isGameFinished())
        assertEquals(annaId, gameManager.getWinnerId())

        val statisticsService = StatisticsService(repository)
        val annaStats = statisticsService.getPlayerStats("Анна")
        assertNotNull(annaStats)
        assertEquals(1, annaStats!!.wins)
        assertEquals(1, annaStats.totalGames)
        assertEquals(100.0, annaStats.winRate, 0.01)
    }
}