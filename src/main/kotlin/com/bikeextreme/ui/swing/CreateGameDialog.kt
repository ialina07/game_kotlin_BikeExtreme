package com.bikeextreme.ui.swing

import com.bikeextreme.game.GameManager
import java.awt.BorderLayout
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.*

class CreateGameDialog(
    private val parent: JFrame,
    private val gameManager: GameManager
) : JDialog(parent, "Новая игра", true) {

    private val playersField = JTextField(20)

    init {
        setupUI()
    }

    private fun setupUI() {
        layout = BorderLayout()
        setSize(400, 120)
        setLocationRelativeTo(parent)

        val inputPanel = JPanel().apply {
            layout = GridLayout(2, 1, 10, 10)
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

            add(JLabel("Введите имена игроков через запятую (минимум 2):"))
            add(playersField)
        }
        add(inputPanel, BorderLayout.CENTER)

        val buttonPanel = JPanel().apply {
            add(JButton("Создать").apply {
                addActionListener { createGame() }
            })
            add(JButton("Отмена").apply {
                addActionListener { dispose() }
            })
        }
        add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun createGame() {
        val names = playersField.text.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (names.size < 2) {
            JOptionPane.showMessageDialog(this, "Нужно минимум 2 игрока!", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }

        val game = gameManager.startGame(names)
        JOptionPane.showMessageDialog(this, "Игра создана! ID: ${game.id}")
        dispose()
    }
}





