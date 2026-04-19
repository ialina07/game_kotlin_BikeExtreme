package com.bikeextreme.domain

import java.util.UUID

data class Move(
    val id: UUID = UUID.randomUUID(),
    val gameId: UUID,
    val playerId: UUID,
    val TurnNumber: Int,
    val  dice1: Int, // погода
    val dice2: Int, // событие
    val moveType: String,
    val restType: String?,
    val stateBefore: PlayerState,
    val stateAfter: PlayerState,
    val isValid: Boolean = true
)