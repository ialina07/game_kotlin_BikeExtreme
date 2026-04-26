package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState
import org.junit.Test
import org.junit.Assert.*

class MovementPhaseTest {

    @Test
    fun testSimpleMovement() {
        val phase = MovementPhase()
        val state = PlayerState(position = 10)
        val context = PhaseContext(
            dice1 = 3,
            dice2 = 4,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = phase.execute(state, context)
        assertEquals(17, result.position)
    }

    @Test
    fun testMovementWithBonus() {
        val phase = MovementPhase()
        val state = PlayerState(position = 10)
        val context = PhaseContext(
            dice1 = 3,
            dice2 = 4,
            moveType = "move",
            restType = null,
            movementBonus = 5
        )

        val result = phase.execute(state, context)
        assertEquals(22, result.position)
    }

    @Test
    fun testMovementWithConditionPenalty() {
        val phase = MovementPhase()
        val state = PlayerState(position = 10, condition = 2)
        val context = PhaseContext(
            dice1 = 3,
            dice2 = 4,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = phase.execute(state, context)
        // 3 + 4 = 7, штраф -2 = 5, 10 + 5 = 15
        assertEquals(15, result.position)
    }
}