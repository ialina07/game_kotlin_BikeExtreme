package com.bikeextreme.domain

import java.time.LocalDateTime
import java.util.UUID

data class Game (
    val id: UUID = UUID.randomUUID(),
    val date: LocalDateTime = LocalDateTime.now(),
    val status: String = "NOT_STARTED",
    val isFinished: Boolean = false,
    val playerIds: List<UUID> = emptyList(),
    var winnerId: UUID? = null
)