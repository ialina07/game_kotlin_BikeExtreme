package com.bikeextreme.repository

import com.bikeextreme.domain.Game
import com.bikeextreme.domain.GameStatus
import com.bikeextreme.domain.Move
import com.bikeextreme.domain.Player
import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.RestType
import java.sql.*
import java.time.LocalDateTime
import java.util.UUID

class SQLiteGameRepository(private val dbPath: String = "jdbc:sqlite:game.db") : GameRepository {

    init {
        initTables()
    }

    private fun getConnection(): Connection = DriverManager.getConnection(dbPath)

    private fun initTables() {
        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                // таблица игр
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS games (
                        id TEXT PRIMARY KEY,
                        date TEXT NOT NULL,
                        status TEXT NOT NULL,
                        isFinished INTEGER NOT NULL,
                        winnerId TEXT,
                        playerIds TEXT NOT NULL
                    )
                """.trimIndent())

                // таблица ходов
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS moves (
                        id TEXT PRIMARY KEY,
                        gameId TEXT NOT NULL,
                        playerId TEXT NOT NULL,
                        turnNumber INTEGER NOT NULL,
                        dice1 INTEGER NOT NULL,
                        dice2 INTEGER NOT NULL,
                        moveType TEXT NOT NULL,
                        restType TEXT,
                        stateBefore TEXT NOT NULL,
                        stateAfter TEXT NOT NULL,
                        isValid INTEGER NOT NULL,
                        FOREIGN KEY (gameId) REFERENCES games(id)
                    )
                """.trimIndent())

                // таблица игроков
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS players (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL UNIQUE
                    )
                """.trimIndent())
            }
        }
    }

    override fun saveGame(game: Game) {
        getConnection().use { conn ->
            val sql = """
                INSERT OR REPLACE INTO games (id, date, status, isFinished, winnerId, playerIds)
                VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, game.id.toString())
                pstmt.setString(2, game.date.toString())
                pstmt.setString(3, game.status.name)
                pstmt.setInt(4, if (game.isFinished) 1 else 0)
                pstmt.setString(5, game.winnerId?.toString())
                pstmt.setString(6, game.playerIds.joinToString(","))
                pstmt.executeUpdate()
            }
        }
    }

    override fun getGame(id: UUID): Game? {
        getConnection().use { conn ->
            val sql = "SELECT * FROM games WHERE id = ?"
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, id.toString())
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    return Game(
                        id = UUID.fromString(rs.getString("id")),
                        date = LocalDateTime.parse(rs.getString("date")),
                        status = GameStatus.valueOf(rs.getString("status")),
                        isFinished = rs.getInt("isFinished") == 1,
                        playerIds = rs.getString("playerIds").split(",").filter { it.isNotEmpty() }.map { UUID.fromString(it) },
                        winnerId = rs.getString("winnerId")?.let { UUID.fromString(it) }
                    )
                }
            }
        }
        return null
    }

    override fun saveMove(move: Move) {
        getConnection().use { conn ->
            val sql = """
                INSERT OR REPLACE INTO moves (id, gameId, playerId, turnNumber, dice1, dice2, moveType, restType, stateBefore, stateAfter, isValid)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, move.id.toString())
                pstmt.setString(2, move.gameId.toString())
                pstmt.setString(3, move.playerId.toString())
                pstmt.setInt(4, move.turnNumber)
                pstmt.setInt(5, move.dice1)
                pstmt.setInt(6, move.dice2)
                pstmt.setString(7, move.moveType)
                pstmt.setString(8, move.restType?.name)
                pstmt.setString(9, "${move.stateBefore.position},${move.stateBefore.energy},${move.stateBefore.condition},${move.stateBefore.water}")
                pstmt.setString(10, "${move.stateAfter.position},${move.stateAfter.energy},${move.stateAfter.condition},${move.stateAfter.water}")
                pstmt.setInt(11, if (move.isValid) 1 else 0)
                pstmt.executeUpdate()
            }
        }
    }

    override fun getMoves(gameId: UUID): List<Move> {
        val moves = mutableListOf<Move>()
        getConnection().use { conn ->
            val sql = "SELECT * FROM moves WHERE gameId = ? ORDER BY turnNumber"
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, gameId.toString())
                val rs = pstmt.executeQuery()
                while (rs.next()) {
                    val stateBeforeParts = rs.getString("stateBefore").split(",").map { it.toInt() }
                    val stateAfterParts = rs.getString("stateAfter").split(",").map { it.toInt() }
                    moves.add(
                        Move(
                            id = UUID.fromString(rs.getString("id")),
                            gameId = UUID.fromString(rs.getString("gameId")),
                            playerId = UUID.fromString(rs.getString("playerId")),
                            turnNumber = rs.getInt("turnNumber"),
                            dice1 = rs.getInt("dice1"),
                            dice2 = rs.getInt("dice2"),
                            moveType = rs.getString("moveType"),
                            restType = rs.getString("restType")?.let { RestType.valueOf(it) },
                            stateBefore = PlayerState(
                                position = stateBeforeParts[0],
                                energy = stateBeforeParts[1],
                                condition = stateBeforeParts[2],
                                water = stateBeforeParts[3]
                            ),
                            stateAfter = PlayerState(
                                position = stateAfterParts[0],
                                energy = stateAfterParts[1],
                                condition = stateAfterParts[2],
                                water = stateAfterParts[3]
                            ),
                            isValid = rs.getInt("isValid") == 1
                        )
                    )
                }
            }
        }
        return moves
    }

    override fun getAllGames(): List<Game> {
        val games = mutableListOf<Game>()
        getConnection().use { conn ->
            val sql = "SELECT * FROM games"
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                while (rs.next()) {
                    games.add(
                        Game(
                            id = UUID.fromString(rs.getString("id")),
                            date = LocalDateTime.parse(rs.getString("date")),
                            status = GameStatus.valueOf(rs.getString("status")),
                            isFinished = rs.getInt("isFinished") == 1,
                            playerIds = rs.getString("playerIds").split(",").filter { it.isNotEmpty() }.map { UUID.fromString(it) },
                            winnerId = rs.getString("winnerId")?.let { UUID.fromString(it) }
                        )
                    )
                }
            }
        }
        return games
    }

    override fun getGamesByPlayer(playerId: UUID): List<Game> {
        return getAllGames().filter { it.playerIds.contains(playerId) }
    }

    override fun savePlayer(player: Player) {
        getConnection().use { conn ->
            val sql = "INSERT OR IGNORE INTO players (id, name) VALUES (?, ?)"
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, player.id.toString())
                pstmt.setString(2, player.name)
                pstmt.executeUpdate()
            }
        }
    }

    override fun getPlayer(id: UUID): Player? {
        getConnection().use { conn ->
            val sql = "SELECT * FROM players WHERE id = ?"
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, id.toString())
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    return Player(
                        id = UUID.fromString(rs.getString("id")),
                        name = rs.getString("name")
                    )
                }
            }
        }
        return null
    }

    override fun getPlayerByName(name: String): Player? {
        getConnection().use { conn ->
            val sql = "SELECT * FROM players WHERE name = ?"
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, name)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    return Player(
                        id = UUID.fromString(rs.getString("id")),
                        name = rs.getString("name")
                    )
                }
            }
        }
        return null
    }

    override fun getAllPlayers(): List<Player> {
        val players = mutableListOf<Player>()
        getConnection().use { conn ->
            val sql = "SELECT * FROM players"
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                while (rs.next()) {
                    players.add(
                        Player(
                            id = UUID.fromString(rs.getString("id")),
                            name = rs.getString("name")
                        )
                    )
                }
            }
        }
        return players
    }
}