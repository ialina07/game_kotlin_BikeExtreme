package com.bikeextreme.repository

import com.bikeextreme.domain.Game
import com.bikeextreme.domain.Move
import com.bikeextreme.domain.Player
import java.util.UUID

/**
 * Интерфейс для хранения данных игры.
 * Позволяет сохранять и загружать игры, ходы и игроков.
 * Реализации: InMemoryGameRepository, в будущем SQLite.
 */
interface GameRepository {
    /** Сохранить партию */
    fun saveGame(game: Game)

    /** Найти партию по ID (вернёт null, если не найдена) */
    fun getGame(id: UUID): Game?

    /** Сохранить ход */
    fun saveMove(move: Move)

    /** Получить все ходы партии (в порядке добавления) */
    fun getMoves(gameId: UUID): List<Move>

    /** Получить все сохранённые партии */
    fun getAllGames(): List<Game>

    /** Получить все партии, в которых участвовал игрок */
    fun getGamesByPlayer(playerId: UUID): List<Game>

    /** Сохранить игрока  */
    fun savePlayer(player: Player)

    /** Найти игрока по ID */
    fun getPlayer(id: UUID): Player?

    /** Найти игрока по имени (без учёта регистра) */
    fun getPlayerByName(name: String): Player?

    /** Получить всех игроков */
    fun getAllPlayers(): List<Player>
}
