package com.bikeextreme.repository

import com.bikeextreme.domain.*
import com.bikeextreme.game.RestType
import org.junit.*
import org.junit.Assert.*
import java.io.File
import java.util.UUID

class SQLiteGameRepositoryRegressionTest {

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

    // игры

    @Test
    fun regressionTestGameSave() {
        val game = Game(
            playerIds = listOf(UUID.randomUUID(), UUID.randomUUID()),
            status = GameStatus.IN_PROGRESS
        )
        repository.saveGame(game)

        val loaded = repository.getGame(game.id)
        assertNotNull(loaded)
        assertEquals(game.id, loaded?.id)
        assertEquals(game.playerIds, loaded?.playerIds)
        assertEquals(game.status, loaded?.status)
    }

    @Test
    fun regressionTestGameUpdateWinner() {
        val playerId = UUID.randomUUID()
        val game = Game(
            playerIds = listOf(playerId),
            status = GameStatus.IN_PROGRESS
        )
        repository.saveGame(game)

        val updatedGame = game.copy(winnerId = playerId, isFinished = true)
        repository.saveGame(updatedGame)

        val loaded = repository.getGame(game.id)
        assertNotNull(loaded)
        assertEquals(playerId, loaded?.winnerId)
        assertTrue(loaded?.isFinished ?: false)
    }

    // ходы

    @Test
    fun regressionTestMoveSaveLoad() {
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
        assertEquals(move.turnNumber, loaded.turnNumber)
        assertEquals(move.dice1, loaded.dice1)
        assertEquals(move.dice2, loaded.dice2)
        assertEquals(move.stateBefore.position, loaded.stateBefore.position)
        assertEquals(move.stateAfter.position, loaded.stateAfter.position)
    }

    @Test
    fun regressionTestMoveWithRest() {
        val player = Player(name = "Вика")
        repository.savePlayer(player)

        val game = Game(playerIds = listOf(player.id))
        repository.saveGame(game)

        val stateBefore = PlayerState(energy = 5)
        val stateAfter = PlayerState(energy = 7)

        val move = Move(
            gameId = game.id,
            playerId = player.id,
            turnNumber = 1,
            dice1 = 0,
            dice2 = 0,
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

    // игроки

    @Test
    fun regressionTestPlayerSaveLoad() {
        val player = Player(name = "Анна")
        repository.savePlayer(player)

        val loaded = repository.getPlayer(player.id)
        assertNotNull(loaded)
        assertEquals(player.id, loaded?.id)
        assertEquals(player.name, loaded?.name)
    }

    @Test
    fun regressionTestGetPlayerByName() {
        val player = Player(name = "Вика")
        repository.savePlayer(player)

        val loaded = repository.getPlayerByName("Вика")
        assertNotNull(loaded)
        assertEquals(player.id, loaded?.id)
    }

    @Test
    fun regressionTestGetAllPlayers() {
        repository.savePlayer(Player(name = "Анна"))
        repository.savePlayer(Player(name = "Вика"))

        val all = repository.getAllPlayers()
        assertEquals(2, all.size)
    }

    @Test
    fun regressionTestGetGamesByPlayer() {
        val player = Player(name = "Анна")
        repository.savePlayer(player)

        val game1 = Game(playerIds = listOf(player.id))
        val game2 = Game(playerIds = listOf(player.id))
        repository.saveGame(game1)
        repository.saveGame(game2)

        val games = repository.getGamesByPlayer(player.id)
        assertEquals(2, games.size)
    }

    // пустые данные

    @Test
    fun regressionTestGetNonExistentGame() {
        val game = repository.getGame(UUID.randomUUID())
        assertNull(game)
    }

    @Test
    fun regressionTestGetMovesForNonExistentGame() {
        val moves = repository.getMoves(UUID.randomUUID())
        assertTrue(moves.isEmpty())
    }

    @Test
    fun regressionTestGetPlayerByNameNotFound() {
        val player = repository.getPlayerByName("Несуществующий")
        assertNull(player)
    }
}