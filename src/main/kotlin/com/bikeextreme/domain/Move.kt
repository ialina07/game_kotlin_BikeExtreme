package com.bikeextreme.domain

import java.util.UUID
import com.bikeextreme.game.RestType

data class Move(
    val id: UUID = UUID.randomUUID(),
    val gameId: UUID,
    val playerId: UUID,
    val turnNumber: Int,
    val dice1: Int, // погода
    val dice2: Int, // событие
    val moveType: String,
    val restType: RestType?,
    val stateBefore: PlayerState,
    val stateAfter: PlayerState,
    val isValid: Boolean = true
)