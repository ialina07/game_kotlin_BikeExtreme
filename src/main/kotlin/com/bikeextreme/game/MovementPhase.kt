package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState

class MovementPhase : Phase {
    override fun execute(state: PlayerState, context: PhaseContext): PlayerState {
        // движение = dice1 + dice2
        var movement = context.dice1 + context.dice2

        // штраф за плохое состояние
        if (state.condition <= 2) {
            movement = movement - 2
            if (movement < 0)
                movement = 0
        }

        // бонус от ветра
        if (context.tailwindBonus)
            movement += 2

        var newPosition = state.position + movement
        if (newPosition > PlayerState.TRACK_LENGTH) {
            newPosition = PlayerState.TRACK_LENGTH
        }

        return state.copy(position = newPosition)
    }
}