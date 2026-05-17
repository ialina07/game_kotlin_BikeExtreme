package com.bikeextreme.ui.swing

import com.bikeextreme.statistics.PlayerStats
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

class StatsDialog(
    private val parent: JFrame,
    private val playerName: String,
    private val stats: PlayerStats
) : JDialog(parent, "Статистика игрока: $playerName", true) {

    init {
        setupUI()
    }

    private fun setupUI() {
        layout = BorderLayout()
        setSize(300, 150)
        setLocationRelativeTo(parent)

        val panel = JPanel().apply {
            layout = GridLayout(3, 2, 10, 10)
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

            add(JLabel("Всего игр:"))
            add(JLabel(stats.totalGames.toString()))
            add(JLabel("Побед:"))
            add(JLabel(stats.wins.toString()))
            add(JLabel("Процент побед:"))
            add(JLabel(String.format("%.1f%%", stats.winRate)))
        }
        add(panel, BorderLayout.CENTER)

        val buttonPanel = JPanel().apply {
            add(JButton("OK").apply {
                addActionListener { dispose() }
            })
        }
        add(buttonPanel, BorderLayout.SOUTH)
    }
}