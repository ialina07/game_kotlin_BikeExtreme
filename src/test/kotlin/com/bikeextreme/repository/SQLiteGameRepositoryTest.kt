package com.bikeextreme.repository

import com.bikeextreme.domain.*
import com.bikeextreme.game.RestType
import org.junit.*
import org.junit.Assert.*
import java.io.File
import java.util.UUID

class SQLiteGameRepositoryTest {

    private lateinit var repository: SQLiteGameRepository
    private val testDbPath = "jdbc:sqlite:test.db"

    @Before
    fun setUp() {
        // удаляем старый файл перед каждым тестом
        File("test.db").delete()
        repository = SQLiteGameRepository(testDbPath)
    }

    @After
    fun tearDown() {
        // удаляем файл после теста
        File("test.db").delete()
    }

    // тесты для игроков

    @Test
    fun testSaveAndGetPlayer() {
        val player = Player(name = "Анна")
        repository.savePlayer(player)

        val loaded = repository.getPlayer(player.id)
        assertNotNull(loaded)
        assertEquals(player.id, loaded?.id)
        assertEquals(player.name, loaded?.name)
    }

    @Test
    fun testGetPlayerByName() {
        val player = Player(name = "Вика")
        repository.savePlayer(player)

        val loaded = repository.getPlayerByName("Вика")
        assertNotNull(loaded)
        assertEquals(player.id, loaded?.id)
        assertEquals("Вика", loaded?.name)
    }

    @Test
    fun testGetPlayerByNameCase() {
        val player = Player(name = "Анна")
        repository.savePlayer(player)

        val loaded = repository.getPlayerByName("анна")
        assertNull(loaded)
    }

    @Test
    fun testGetAllPlayers() {
        val player1 = Player(name = "Анна")
        val player2 = Player(name = "Вика")
        repository.savePlayer(player1)
        repository.savePlayer(player2)

        val all = repository.getAllPlayers()
        assertEquals(2, all.size)
        assertTrue(all.any { it.name == "Анна" })
        assertTrue(all.any { it.name == "Вика" })
    }

    @Test
    fun testSavePlayerDuplicate() {
        val player1 = Player(name = "Анна")
        val player2 = Player(name = "Анна")

        repository.savePlayer(player1)
        repository.savePlayer(player2)

        val all = repository.getAllPlayers()
        assertEquals(1, all.size)  // дубликат не добавился
    }

    // тесты для игр

    @Test
    fun testSaveAndGetGame() {
        val player1 = Player(name = "Анна")
        val player2 = Player(name = "Вика")
        repository.savePlayer(player1)
        repository.savePlayer(player2)

        val game = Game(
            playerIds = listOf(player1.id, player2.id),
            status = GameStatus.IN_PROGRESS
        )
        repository.saveGame(game)

        val loaded = repository.getGame(game.id)
        assertNotNull(loaded)
        assertEquals(game.id, loaded?.id)
        assertEquals(game.playerIds, loaded?.playerIds)
        assertEquals(game.status, loaded?.status)
        assertFalse(loaded?.isFinished ?: true)
    }

    @Test
    fun testGetAllGames() {
        val game1 = Game(status = GameStatus.IN_PROGRESS)
        val game2 = Game(status = GameStatus.FINISHED)
        repository.saveGame(game1)
        repository.saveGame(game2)

        val all = repository.getAllGames()
        assertEquals(2, all.size)
    }

    @Test
    fun testGetGamesByPlayer() {
        val player = Player(name = "Анна")
        repository.savePlayer(player)

        val game1 = Game(playerIds = listOf(player.id))
        val game2 = Game(playerIds = listOf(player.id))
        repository.saveGame(game1)
        repository.saveGame(game2)

        val games = repository.getGamesByPlayer(player.id)
        assertEquals(2, games.size)
    }

    // тесты для ходов

    @Test
    fun testSaveAndGetMove() {
        val player = Player(name = "Анна")
        repository.savePlayer(player)

        val game = Game(playerIds = listOf(player.id))
        repository.saveGame(game)

        val stateBefore = PlayerState(position = 10, energy = 5, condition = 5, water = 3)
        val stateAfter = PlayerState(position = 16, energy = 4, condition = 5, water = 3)

        val move = Move(
            gameId = game.id,
            playerId = player.id,
            turnNumber = 1,
            dice1 = 3,
            dice2 = 3,
            moveType = "move",
            restType = null,
            stateBefore = stateBefore,
            stateAfter = stateAfter,
            isValid = true
        )
        repository.saveMove(move)

        val moves = repository.getMoves(game.id)
        assertEquals(1, moves.size)

        val loaded = moves[0]
        assertEquals(move.id, loaded.id)
        assertEquals(move.gameId, loaded.gameId)
        assertEquals(move.playerId, loaded.playerId)
        assertEquals(move.turnNumber, loaded.turnNumber)
        assertEquals(move.dice1, loaded.dice1)
        assertEquals(move.dice2, loaded.dice2)
        assertEquals(move.moveType, loaded.moveType)
        assertEquals(move.restType, loaded.restType)
        assertEquals(move.stateBefore.position, loaded.stateBefore.position)
        assertEquals(move.stateBefore.energy, loaded.stateBefore.energy)
        assertEquals(move.stateAfter.position, loaded.stateAfter.position)
        assertEquals(move.isValid, loaded.isValid)
    }

    @Test
    fun testGetMovesOrderedByTurnNumber() {
        val player = Player(name = "Анна")
        repository.savePlayer(player)

        val game = Game(playerIds = listOf(player.id))
        repository.saveGame(game)

        val move1 = Move(
            gameId = game.id,
            playerId = player.id,
            turnNumber = 1,
            dice1 = 3, dice2 = 3,
            moveType = "move", restType = null,
            stateBefore = PlayerState(), stateAfter = PlayerState(),
            isValid = true
        )
        val move2 = Move(
            gameId = game.id,
            playerId = player.id,
            turnNumber = 2,
            dice1 = 4, dice2 = 4,
            moveType = "move", restType = null,
            stateBefore = PlayerState(), stateAfter = PlayerState(),
            isValid = true
        )
        repository.saveMove(move1)
        repository.saveMove(move2)

        val moves = repository.getMoves(game.id)
        assertEquals(2, moves.size)
        assertEquals(1, moves[0].turnNumber)
        assertEquals(2, moves[1].turnNumber)
    }

    @Test
    fun testGetMovesEmpty() {
        val gameId = UUID.randomUUID()
        val moves = repository.getMoves(gameId)
        assertTrue(moves.isEmpty())
    }

    // тест с отдыхом
    @Test
    fun testSaveMoveWithRest() {
        val player = Player(name = "Анна")
        repository.savePlayer(player)

        val game = Game(playerIds = listOf(player.id))
        repository.saveGame(game)

        val stateBefore = PlayerState(energy = 5)
        val stateAfter = PlayerState(energy = 7)

        val move = Move(
            gameId = game.id,
            playerId = player.id,
            turnNumber = 1,
            dice1 = 0, dice2 = 0,
            moveType = "rest",
            restType = RestType.ENERGY,
            stateBefore = stateBefore,
            stateAfter = stateAfter,
            isValid = true
        )
        repository.saveMove(move)

        val moves = repository.getMoves(game.id)
        assertEquals(1, moves.size)
        assertEquals(RestType.ENERGY, moves[0].restType)
        assertEquals(7, moves[0].stateAfter.energy)
    }

    // тест с финишем
    @Test
    fun testUpdateGameWinner() {
        val player1 = Player(name = "Анна")
        val player2 = Player(name = "Вика")
        repository.savePlayer(player1)
        repository.savePlayer(player2)

        val game = Game(
            playerIds = listOf(player1.id, player2.id),
            status = GameStatus.IN_PROGRESS
        )
        repository.saveGame(game)

        // обновляем игру с победителем
        val updatedGame = game.copy(winnerId = player1.id, isFinished = true)
        repository.saveGame(updatedGame)

        val loaded = repository.getGame(game.id)
        assertNotNull(loaded)
        assertEquals(player1.id, loaded?.winnerId)
        assertTrue(loaded?.isFinished ?: false)
    }
}