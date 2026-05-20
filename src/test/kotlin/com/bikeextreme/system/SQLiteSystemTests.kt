package com.bikeextreme.system

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.*
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.SQLiteGameRepository
import com.bikeextreme.statistics.StatisticsService
import org.junit.*
import org.junit.Assert.*
import java.io.File

class SQLiteSystemTests {

    private lateinit var repository: SQLiteGameRepository
    private val testDbPath = "jdbc:sqlite:test.db"

    @Before
    fun setUp() {
        File("test.db").delete()
        repository = SQLiteGameRepository(testDbPath)
    }

    @After
    fun tearDown() {
        File("test.db").delete()
    }

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

        return GameManager(moveValidator, phaseExecutor, repository)
    }

    @Test
    fun testFullGameUntilWinnerWithSQLite() {
        val gameManager = createGameManager()

        val game = gameManager.startGame(listOf("Анна", "Вика"))
        val annaId = game.playerIds[0]
        val vikaId = game.playerIds[1]

        var annaState = PlayerState(position = 0, energy = 50)
        var vikaState = PlayerState(position = 0, energy = 50)

        while (!gameManager.isGameFinished()) {
            // ход Анны
            val successAnna = gameManager.recordMove(
                playerId = annaId,
                dice1 = 6, dice2 = 6,
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

            // ход Вики
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

        val savedGame = repository.getGame(game.id)
        assertNotNull(savedGame)
        if (savedGame != null) {
            assertEquals(annaId, savedGame.winnerId)
            assertTrue(savedGame.isFinished)
        }
    }

    @Test
    fun testGamesStatisticsWithSQLite() {
        val gameManager1 = createGameManager()
        val game1 = gameManager1.startGame(listOf("Анна", "Вика"))
        val annaId = game1.playerIds[0]
        val vikaId = game1.playerIds[1]

        // игра 1: Анна побеждает
        var annaState = PlayerState(position = 0, energy = 100)
        var vikaState = PlayerState(position = 0, energy = 100)

        while (!gameManager1.isGameFinished()) {
            // ход Анны
            var success = gameManager1.recordMove(
                playerId = annaId,
                dice1 = 6, dice2 = 6,
                moveType = "move", restType = null,
                stateBefore = annaState
            )
            assertTrue(success)
            val newAnnaState = gameManager1.getCurrentState()[annaId]
            if (newAnnaState == null) {
                fail("Состояние Анны не найдено")
                return
            }
            annaState = newAnnaState

            if (gameManager1.isGameFinished()) break

            // ход Вики
            success = gameManager1.recordMove(
                playerId = vikaId,
                dice1 = 1, dice2 = 1,
                moveType = "move", restType = null,
                stateBefore = vikaState
            )
            assertTrue(success)
            val newVikaState = gameManager1.getCurrentState()[vikaId]
            if (newVikaState == null) {
                fail("Состояние Вики не найдено")
                return
            }
            vikaState = newVikaState
        }
        assertTrue(gameManager1.isGameFinished())
        assertEquals(annaId, gameManager1.getWinnerId())

        // игра 2: Вика побеждает
        val gameManager2 = createGameManager()
        val game2 = gameManager2.startGame(listOf("Анна", "Вика"))
        val annaId2 = game2.playerIds[0]
        val vikaId2 = game2.playerIds[1]

        var annaState2 = PlayerState(position = 0, energy = 100)
        var vikaState2 = PlayerState(position = 0, energy = 100)

        while (!gameManager2.isGameFinished()) {
            // ход Анны
            var success = gameManager2.recordMove(
                playerId = annaId2,
                dice1 = 1, dice2 = 1,
                moveType = "move", restType = null,
                stateBefore = annaState2
            )
            assertTrue(success)
            val newAnnaState2 = gameManager2.getCurrentState()[annaId2]
            if (newAnnaState2 == null) {
                fail("Состояние Анны не найдено")
                return
            }
            annaState2 = newAnnaState2

            if (gameManager2.isGameFinished()) break

            // ход Вики
            success = gameManager2.recordMove(
                playerId = vikaId2,
                dice1 = 6, dice2 = 6,
                moveType = "move", restType = null,
                stateBefore = vikaState2
            )
            assertTrue(success)
            val newVikaState2 = gameManager2.getCurrentState()[vikaId2]
            if (newVikaState2 == null) {
                fail("Состояние Вики не найдено")
                return
            }
            vikaState2 = newVikaState2
        }
        assertTrue(gameManager2.isGameFinished())
        assertEquals(vikaId2, gameManager2.getWinnerId())

        val statisticsService = StatisticsService(repository)

        val annaStats = statisticsService.getPlayerStats("Анна")
        assertNotNull(annaStats)
        if (annaStats != null) {
            assertEquals(1, annaStats.wins)
            assertEquals(2, annaStats.totalGames)
            assertEquals(50.0, annaStats.winRate, 0.01)
        }

        val vikaStats = statisticsService.getPlayerStats("Вика")
        assertNotNull(vikaStats)
        if (vikaStats != null) {
            assertEquals(1, vikaStats.wins)
            assertEquals(2, vikaStats.totalGames)
            assertEquals(50.0, vikaStats.winRate, 0.01)
        }

        val leaderboard = statisticsService.getLeaderboard()
        assertEquals(2, leaderboard.size)
        assertTrue(leaderboard.all { it.wins == 1 })
    }

    @Test
    fun testGameWithRestAndSQLite() {
        val gameManager = createGameManager()
        val game = gameManager.startGame(listOf("Анна", "Вика"))
        val annaId = game.playerIds[0]
        val vikaId = game.playerIds[1]

        // ход Анны
        val success1 = gameManager.recordMove(
            playerId = annaId,
            dice1 = 3, dice2 = 3,
            moveType = "move", restType = null,
            stateBefore = PlayerState(position = 0, energy = 5)
        )
        assertTrue(success1)

        // ход Вики
        val vikaState = gameManager.getCurrentState()[vikaId]
        if (vikaState == null) {
            fail("Состояние Вики не найдено")
            return
        }
        val success2 = gameManager.recordMove(
            playerId = vikaId,
            dice1 = 2, dice2 = 2,
            moveType = "move", restType = null,
            stateBefore = vikaState
        )
        assertTrue(success2)

        // Анна отдыхает
        val annaState = gameManager.getCurrentState()[annaId]
        if (annaState == null) {
            fail("Состояние Анны не найдено")
            return
        }
        val success3 = gameManager.recordMove(
            playerId = annaId,
            dice1 = 1, dice2 = 1,
            moveType = "rest",
            restType = RestType.ENERGY,
            stateBefore = annaState
        )
        assertTrue(success3)

        val finalState = gameManager.getCurrentState()[annaId]
        if (finalState == null) {
            fail("Состояние Анны не найдено")
            return
        }
        assertEquals(7, finalState.energy)
        assertFalse(gameManager.isGameFinished())
    }
}