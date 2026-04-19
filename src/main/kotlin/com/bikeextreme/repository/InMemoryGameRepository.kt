package com.bikeextreme.repository

import com.bikeextreme.domain.Game
import com.bikeextreme.domain.Move
import java.util.UUID

class InMemoryGameRepository : GameRepository {
    private val games = mutableMapOf<UUID, Game>()
    private val moves = mutableMapOf<UUID, MutableList<Move>>()

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
        val allGames = games.values
        val result = mutableListOf<Game>()

        for (game in allGames) {
            if (game.playerIds.contains(playerId)) {
                result.add(game)
            }
        }

        return result
    }
}