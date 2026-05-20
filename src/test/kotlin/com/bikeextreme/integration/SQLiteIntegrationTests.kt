package com.bikeextreme.integration

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.*
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.SQLiteGameRepository
import org.junit.*
import org.junit.Assert.*
import java.io.File

class SQLiteIntegrationTests {

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
    fun testGameManagerWithSQLiteRepository() {
        val gameManager = createGameManager()

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

    @Test
    fun testSQLiteData() {
        val gameManager = createGameManager()
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

        val savedGame = repository.getGame(game.id)
        assertNotNull(savedGame)

        val moves = repository.getMoves(game.id)
        assertEquals(1, moves.size)

        val savedPlayer = repository.getPlayer(annaId)
        assertNotNull(savedPlayer)
        if (savedPlayer != null) {
            assertEquals("Анна", savedPlayer.name)
        }
    }

    @Test
    fun testSQLiteMultipleGames() {
        val gameManager = createGameManager()

        // игра 1
        val game1 = gameManager.startGame(listOf("Анна", "Вика"))
        val annaId = game1.playerIds[0]

        val success1 = gameManager.recordMove(
            playerId = annaId,
            dice1 = 3,
            dice2 = 3,
            moveType = "move",
            restType = null,
            stateBefore = PlayerState(position = 0, energy = 5)
        )
        assertTrue(success1)

        // игра 2
        val game2 = gameManager.startGame(listOf("Анна", "Вика"))
        val annaId2 = game2.playerIds[0]

        val success2 = gameManager.recordMove(
            playerId = annaId2,
            dice1 = 4,
            dice2 = 4,
            moveType = "move",
            restType = null,
            stateBefore = PlayerState(position = 0, energy = 5)
        )
        assertTrue(success2)

        // проверяем, что обе игры сохранились
        val allGames = repository.getAllGames()
        assertEquals(2, allGames.size)

        // проверяем, что ходы сохранились для каждой игры
        val moves1 = repository.getMoves(game1.id)
        val moves2 = repository.getMoves(game2.id)
        assertEquals(1, moves1.size)
        assertEquals(1, moves2.size)
        assertEquals(3, moves1[0].dice1)
        assertEquals(4, moves2[0].dice1)
    }
}