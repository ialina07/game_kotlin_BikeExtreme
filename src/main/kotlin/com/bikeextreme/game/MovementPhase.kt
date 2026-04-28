package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState

class MovementPhase : Phase {
    override fun execute(state: PlayerState, context: PhaseContext): PlayerState {
        // при отдыхе движение не применяется
        if (context.moveType != "move") {
            return state
        }

        // движение = dice1 + dice2
        var movement = context.dice1 + context.dice2 + context.movementBonus

        // штраф за плохое состояние
        if (state.condition <= 2) {
            movement = movement - 2
            if (movement < 0) {
                movement = 0
            }
        }

        context.movementThisTurn = movement

        var newPosition = state.position + movement
        if (newPosition > PlayerState.TRACK_LENGTH) {
            newPosition = PlayerState.TRACK_LENGTH
        }

        return state.copy(position = newPosition)
    }
}