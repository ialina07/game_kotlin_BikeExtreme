package com.bikeextreme.game

import com.bikeextreme.game.RestType

data class PhaseContext(
    val dice1: Int,
    val dice2: Int,
    val moveType: String,
    val restType: RestType?,
    var movementBonus: Int = 0,
    var movementThisTurn: Int = 0
)