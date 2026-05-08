package com.bikeextreme.ui.swing

import com.bikeextreme.statistics.ReplayService
import com.bikeextreme.repository.GameRepository
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class ReplayDialog(
    private val parent: JFrame,
    private val replayService: ReplayService,
    private val repository: GameRepository
) : JDialog(parent, "Повтор партии", true) {

    private val textArea = JTextArea()

    init {
        setupUI()
        showGameSelector()
    }

    private fun setupUI() {
        layout = BorderLayout()
        setSize(600, 400)
        setLocationRelativeTo(parent)

        textArea.isEditable = false
        textArea.font = java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12)
        add(JScrollPane(textArea), BorderLayout.CENTER)

        val buttonPanel = JPanel().apply {
            add(JButton("Закрыть").apply {
                addActionListener { dispose() }
            })
        }
        add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun showGameSelector() {
        val games = repository.getAllGames().filter { it.isFinished }
        if (games.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет завершённых партий для повтора")
            dispose()
            return
        }

        val gameOptions = games.map { it.id.toString() }.toTypedArray()
        val selected = JOptionPane.showInputDialog(
            this,
            "Выберите партию для повтора:",
            "Повтор партии",
            JOptionPane.QUESTION_MESSAGE,
            null,
            gameOptions,
            gameOptions[0]
        )

        if (selected != null) {
            val gameId = java.util.UUID.fromString(selected.toString())
            loadReplay(gameId)
            title = "Повтор партии: $gameId"
        } else {
            dispose()
        }
    }

    private fun loadReplay(gameId: java.util.UUID) {
        val outputStream = java.io.ByteArrayOutputStream()
        val printStream = java.io.PrintStream(outputStream)
        val oldOut = System.out
        System.setOut(printStream)

        replayService.replayGame(gameId)

        System.out.flush()
        System.setOut(oldOut)
        textArea.text = outputStream.toString()
    }
}