package com.bikeextreme.ui.swing

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.GameManager
import javax.swing.table.AbstractTableModel
import java.util.UUID

class GameTableModel(
    private val gameManager: GameManager
) : AbstractTableModel() {
    private val columns = arrayOf("Игрок", "Позиция", "Энергия", "Состояние", "Вода")

    override fun getRowCount(): Int = gameManager.getCurrentState().size

    override fun getColumnCount(): Int = columns.size

    override fun getColumnName(column: Int): String = columns[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        val entries = gameManager.getCurrentState().entries.toList()
        if (rowIndex >= entries.size) return ""
        val (playerId, state) = entries[rowIndex]
        val player = gameManager.getPlayer(playerId) ?: return ""

        return when (columnIndex) {
            0 -> player.name
            1 -> state.position
            2 -> state.energy
            3 -> state.condition
            4 -> state.water
            else -> ""
        }
    }

    fun refresh() {
        fireTableDataChanged()
    }
}