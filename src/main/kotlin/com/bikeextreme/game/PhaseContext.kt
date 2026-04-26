package com.bikeextreme.game

data class PhaseContext(
    val dice1: Int,
    val dice2: Int,
    val moveType: String,
    val restType: String?,
    var tailwindBonus: Boolean = false,
    var movementBonus: Int = 0
)