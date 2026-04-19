package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState

class DownhillEvent : Event {
    override fun apply(state: PlayerState): PlayerState {
        // +2 клетки движения, состояние не меняется
        return state
    }
}