package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState

interface Event {
    fun apply(state: PlayerState): PlayerState
}