package com.bikeextreme.ui.swing

import com.bikeextreme.game.GameManager
import com.bikeextreme.game.MoveValidator
import com.bikeextreme.game.PhaseExecutor
import com.bikeextreme.game.WeatherPhase
import com.bikeextreme.game.EventPhase
import com.bikeextreme.game.MovementPhase
import com.bikeextreme.game.EnergyPhase
import com.bikeextreme.game.RestPhase
import com.bikeextreme.game.Phase
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.InMemoryGameRepository
import com.bikeextreme.statistics.StatisticsService
import com.bikeextreme.statistics.ReplayService
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.JOptionPane

class MainFrame : JFrame("BikeExtreme Judge") {

    private val repository = InMemoryGameRepository()
    private var gameManager: GameManager
    private var statisticsService: StatisticsService
    private var replayService: ReplayService
    private var tableModel: GameTableModel
    private var playersTable: JTable

    init {
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

        gameManager = GameManager(moveValidator, phaseExecutor, repository)
        statisticsService = StatisticsService(repository)
        replayService = ReplayService(repository, phaseExecutor)
        tableModel = GameTableModel(gameManager)
        playersTable = JTable(tableModel)

        setupUI()
    }

    private fun setupUI() {
        defaultCloseOperation = EXIT_ON_CLOSE
        preferredSize = Dimension(900, 600)
        layout = BorderLayout()

        // верхняя панель
        val infoPanel = createInfoPanel()
        add(infoPanel, BorderLayout.NORTH)

        // центральная панель с таблицей
        playersTable.fillsViewportHeight = true
        playersTable.setRowHeight(25)
        add(JScrollPane(playersTable), BorderLayout.CENTER)

        // нижняя панель с кнопками
        val buttonPanel = createButtonPanel()
        add(buttonPanel, BorderLayout.SOUTH)

        pack()
        setLocationRelativeTo(null)
    }

    private fun createInfoPanel(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = EmptyBorder(10, 10, 10, 10)

            val titleLabel = JLabel("BikeExtreme").apply {
                font = font.deriveFont(18f)
                alignmentX = CENTER_ALIGNMENT
            }
            add(titleLabel)
        }
    }

    private fun createButtonPanel(): JPanel {
        return JPanel().apply {
            layout = GridLayout(1, 6, 10, 10)
            border = EmptyBorder(10, 10, 10, 10)

            add(JButton("Новая игра").apply {
                addActionListener { createGame() }
            })
            add(JButton("Сделать ход").apply {
                addActionListener { makeMove() }
            })
            add(JButton("Статистика").apply {
                addActionListener { showStats() }
            })
            add(JButton("Таблица лидеров").apply {
                addActionListener { showLeaderboard() }
            })
            add(JButton("Повтор партии").apply {
                addActionListener { showReplay() }
            })
        }
    }

    private fun createGame() {
        if (gameManager.getCurrentGameId() != null && !gameManager.isGameFinished()) {
            val confirm = JOptionPane.showConfirmDialog(
                this,
                "Текущая игра не закончена. Начать новую? (текущая игра будет потеряна)",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION
            )
            if (confirm != JOptionPane.YES_OPTION) return
        }
        val dialog = CreateGameDialog(this, gameManager)
        dialog.isVisible = true
        refreshState()
    }

    private fun makeMove() {
        if (gameManager.getCurrentGameId() == null) {
            JOptionPane.showMessageDialog(this, "Сначала создайте игру!", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }
        if (gameManager.isGameFinished()) {
            JOptionPane.showMessageDialog(this, "Игра уже закончена! Создайте новую.", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }
        val dialog = MoveDialog(this, gameManager)
        dialog.isVisible = true
        refreshState()

        // проверяем, не закончилась ли игра после хода
        if (gameManager.isGameFinished()) {
            val winnerId = gameManager.getWinnerId()
            val winnerName = winnerId?.let { gameManager.getPlayer(it)?.name } ?: "Неизвестный"
            JOptionPane.showMessageDialog(
                this,
                "Победитель: $winnerName!",
                "Игра закончена",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    private fun refreshState() {
        tableModel.refresh()
        val currentPlayer = if (gameManager.isGameFinished()) {
            "—"
        } else {
            gameManager.getCurrentPlayerId()?.let {
                gameManager.getPlayer(it)?.name
            } ?: "—"
        }
        title = "BikeExtreme — Ходит: $currentPlayer"
    }

    private fun showStats() {
        val playerName = JOptionPane.showInputDialog(this, "Введите имя игрока:")
        if (playerName.isNullOrBlank()) return
        val stats = statisticsService.getPlayerStats(playerName)
        if (stats == null) {
            JOptionPane.showMessageDialog(this, "Игрок не найден", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }
        StatsDialog(this, playerName, stats).isVisible = true
    }

    private fun showLeaderboard() {
        val leaderboard = statisticsService.getLeaderboard()
        if (leaderboard.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет данных для таблицы лидеров")
            return
        }
        val message = leaderboard.joinToString("\n") {
            "${it.playerName}: ${it.wins} побед (${String.format("%.1f", it.winRate)}%)"
        }
        JOptionPane.showMessageDialog(this, message, "Таблица лидеров", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun showReplay() {
        showReplayDialog(this, replayService, repository)
    }
}