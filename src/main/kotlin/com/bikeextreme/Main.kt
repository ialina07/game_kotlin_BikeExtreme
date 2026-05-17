package com.bikeextreme

import com.bikeextreme.ui.swing.MainFrame
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        MainFrame().isVisible = true
    }
}