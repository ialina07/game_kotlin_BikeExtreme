package com.bikeextreme.repository

import com.bikeextreme.domain.Game
import com.bikeextreme.domain.Move
import com.bikeextreme.domain.Player
import java.util.UUID

interface GameRepository {
    fun saveGame(game: Game)
    fun getGame(id: UUID): Game?
    fun saveMove(move: Move)
    fun getMoves(gameId: UUID): List<Move>
    fun getAllGames(): List<Game>
    fun getGamesByPlayer(playerId: UUID): List<Game>
}
