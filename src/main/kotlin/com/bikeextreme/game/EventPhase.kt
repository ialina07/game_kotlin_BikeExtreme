package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.event.EventFactory
import com.bikeextreme.game.PhaseContext

class EventPhase(
    private val eventFactory: EventFactory
) : Phase {
    override fun execute(state: PlayerState, context: PhaseContext): PlayerState {
        val event = eventFactory.getEvent(context.dice2)
        return event.apply(state, context)
    }
}