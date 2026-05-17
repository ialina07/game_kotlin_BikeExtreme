package com.bikeextreme.ui.swing

import com.bikeextreme.statistics.ReplayService
import com.bikeextreme.repository.GameRepository
import java.awt.BorderLayout
import javax.swing.*

class ReplayDialog(
    private val parent: JFrame,
    private val replayService: ReplayService,
    private val repository: GameRepository,
    private val games: List<com.bikeextreme.domain.Game>
) : JDialog(parent, "Повтор партии", false) {

    private val textArea = JTextArea()

    init {
        if (showGameSelector()) {
            setupUI()
            isVisible = true
        } else {
            dispose()
        }
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

    private fun showGameSelector(): Boolean {
        val listModel = DefaultListModel<GameItem>()
        games.forEach { game ->
            val players = game.playerIds.mapNotNull { repository.getPlayer(it)?.name }.joinToString(", ")
            val winner = game.winnerId?.let { repository.getPlayer(it)?.name } ?: "не определён"
            val displayText = "$players | Победитель: $winner"
            listModel.addElement(GameItem(game.id, displayText))
        }

        val gameList = JList(listModel)
        gameList.selectionMode = ListSelectionModel.SINGLE_SELECTION

        val optionPane = JOptionPane(
            JScrollPane(gameList),
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION
        )
        val dialog = optionPane.createDialog(this, "Выберите партию")
        dialog.isVisible = true

        if (optionPane.value == JOptionPane.OK_OPTION) {
            val selected = gameList.selectedValue
            if (selected != null) {
                loadReplay(selected.gameId)
                title = "Повтор партии: ${selected.gameId}"
                return true
            } else {
                JOptionPane.showMessageDialog(this, "Партия не выбрана")
                return false
            }
        } else {
            return false
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

    private data class GameItem(
        val gameId: java.util.UUID,
        val displayText: String
    ) {
        override fun toString(): String = displayText
    }
}