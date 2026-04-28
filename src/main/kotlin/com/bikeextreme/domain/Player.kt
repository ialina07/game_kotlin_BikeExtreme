package com.bikeextreme.domain

import java.util.UUID

data class Player(
    val id: UUID = UUID.randomUUID(),
    val name: String
)