package com.bikeextreme.repository

import com.bikeextreme.domain.Game
import com.bikeextreme.domain.Move
import com.bikeextreme.domain.Player
import java.util.UUID

class InMemoryGameRepository : GameRepository {
    private val games = mutableMapOf<UUID, Game>()
    private val moves = mutableMapOf<UUID, MutableList<Move>>()
    private val players = mutableMapOf<UUID, Player>()

    override fun saveGame(game: Game) {
        games[game.id] = game
    }

    override fun getGame(id: UUID): Game? {
        val game = games[id]
        return game
    }

    override fun saveMove(move: Move) {
        val gameId = move.gameId
        var movesList = moves[gameId]

        if (movesList == null) {
            movesList = mutableListOf()
            moves[gameId] = movesList
        }

        movesList.add(move)
    }

    override fun getMoves(gameId: UUID): List<Move> {
        val movesList = moves[gameId]

        if (movesList == null) {
            return emptyList()
        }

        return movesList
    }

    override fun getAllGames(): List<Game> {
        val allGamesValues = games.values
        val gamesList = allGamesValues.toList()
        return gamesList
    }

    override fun getGamesByPlayer(playerId: UUID): List<Game> {
            return games.values.filter { it.playerIds.contains(playerId) }.toList()
        }

    override fun savePlayer(player: Player) {
        players[player.id] = player
    }

    override fun getPlayer(id: UUID): Player? {
        return players[id]
    }

    override fun getPlayerByName(name: String): Player? {
        for (player in players.values) {
            if (player.name.equals(name, ignoreCase = true)) {
                return player
            }
        }
        return null
    }

    override fun getAllPlayers(): List<Player> = players.values.toList()
}
