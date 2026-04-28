package com.bikeextreme.statistics

import com.bikeextreme.domain.Player
import com.bikeextreme.domain.Game
import com.bikeextreme.repository.GameRepository
import java.util.UUID

data class PlayerStats(
    val totalGames: Int,
    val wins: Int,
    val winRate: Double
)

data class PlayerRanking(
    val playerId: UUID,
    val playerName: String,
    val wins: Int,
    val winRate: Double
)

class StatisticsService(
    private val repository: GameRepository
) {
    fun getPlayerStats(playerName: String): PlayerStats? {
        val player = repository.getPlayerByName(playerName)
        if (player == null) {
            println("Игрок $playerName не найден")
            return null
        }
        return getPlayerStats(player.id)
    }

    fun getPlayerStats(playerId: UUID): PlayerStats? {
        val games = repository.getGamesByPlayer(playerId)
        val wins = countWins(playerId, games)
        val total = games.size

        if (total == 0) {
            return PlayerStats(
                totalGames = 0,
                wins = 0,
                winRate = 0.0
            )
        }

        val winRate = (wins.toDouble() / total) * 100.0

        return PlayerStats(
            totalGames = total,
            wins = wins,
            winRate = winRate
        )
    }

    fun getLeaderboard(): List<PlayerRanking> {
        val allPlayers = repository.getAllPlayers()
        val rankings = allPlayers.mapNotNull { player ->
            getPlayerStats(player.id)?.takeIf  { it.totalGames > 0 }?.let { stats ->
                PlayerRanking(
                    playerId = player.id,
                    playerName = player.name,
                    wins = stats.wins,
                    winRate = stats.winRate
                )
            }
        }

        // сортируем по убыванию побед
        return rankings.sortedByDescending { it.wins }
    }

    fun getWinRate(playerName: String): Double? {
        val player = repository.getPlayerByName(playerName)
        if (player == null) {
            println("Игрок $playerName не найден")
            return null
        }
        return getWinRate(player.id)
    }

    fun getWinRate(playerId: UUID): Double? {
        val stats = getPlayerStats(playerId) ?: return null
        return stats.winRate
    }

    private fun countWins(playerId: UUID, games: List<Game>): Int {
        return games.count { it.winnerId == playerId }
    }
}