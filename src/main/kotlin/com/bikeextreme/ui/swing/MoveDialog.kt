package com.bikeextreme.ui.swing

import com.bikeextreme.game.GameManager
import com.bikeextreme.game.RestType
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

class MoveDialog(
    private val parent: JFrame,
    private val gameManager: GameManager
) : JDialog(parent, "Ввод хода", true) {

    private val dice1Field = JTextField(5)
    private val dice2Field = JTextField(5)
    private val moveTypeCombo = JComboBox(arrayOf("move", "rest"))
    private val restTypeCombo = JComboBox(arrayOf("energy", "condition", "water"))
    private val restTypePanel = JPanel()

    init {
        setupUI()
    }

    private fun setupUI() {
        layout = BorderLayout()
        setSize(350, 250)
        setLocationRelativeTo(parent)

        val inputPanel = JPanel().apply {
            layout = GridLayout(0, 2, 10, 10)
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

            add(JLabel("dice1 (погода, 1-6):"))
            add(dice1Field)
            add(JLabel("dice2 (событие, 1-6):"))
            add(dice2Field)
            add(JLabel("Тип хода:"))
            add(moveTypeCombo)

            restTypePanel.apply {
                add(JLabel("Тип отдыха:"))
                add(restTypeCombo)
            }
            add(JLabel()) // для выравнивания
            add(restTypePanel)
        }

        moveTypeCombo.addActionListener {
            restTypePanel.isVisible = moveTypeCombo.selectedItem == "rest"
            pack()
        }
        restTypePanel.isVisible = false

        add(inputPanel, BorderLayout.CENTER)

        val buttonPanel = JPanel().apply {
            add(JButton("OK").apply {
                addActionListener { submitMove() }
            })
            add(JButton("Отмена").apply {
                addActionListener { dispose() }
            })
        }
        add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun submitMove() {
        val currentPlayerId = gameManager.getCurrentPlayerId()
        if (currentPlayerId == null) {
            JOptionPane.showMessageDialog(this, "Нет текущего игрока", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }

        val dice1 = dice1Field.text.toIntOrNull()
        val dice2 = dice2Field.text.toIntOrNull()

        if (dice1 == null || dice2 == null || dice1 !in 1..6 || dice2 !in 1..6) {
            JOptionPane.showMessageDialog(this, "Введите числа от 1 до 6", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }

        val moveType = moveTypeCombo.selectedItem as String
        var restType: RestType? = null

        if (moveType == "rest") {
            restType = when (restTypeCombo.selectedItem as String) {
                "energy" -> RestType.ENERGY
                "condition" -> RestType.CONDITION
                else -> RestType.WATER
            }
        }

        val currentState = gameManager.getCurrentState()[currentPlayerId]
        if (currentState == null) {
            JOptionPane.showMessageDialog(this, "Состояние игрока не найдено", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }

        val success = gameManager.recordMove(
            playerId = currentPlayerId,
            dice1 = dice1,
            dice2 = dice2,
            moveType = moveType,
            restType = restType,
            stateBefore = currentState
        )

        if (success) {
            val newState = gameManager.getCurrentState()[currentPlayerId]
            JOptionPane.showMessageDialog(this, "Ход принят!\nНовая позиция: ${newState?.position}")
            dispose()
        } else {
            JOptionPane.showMessageDialog(this, "Ход невалидный!", "Ошибка", JOptionPane.ERROR_MESSAGE)
        }
    }
}