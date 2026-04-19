package com.bikeextreme.weather

import com.bikeextreme.domain.PlayerState

interface Weather {
    fun apply(state: PlayerState): PlayerState
}