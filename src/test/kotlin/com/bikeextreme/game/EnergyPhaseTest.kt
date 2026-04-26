package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState
import org.junit.Test
import org.junit.Assert.*

class EnergyPhaseTest {

    @Test
    fun testEnergyDecrease() {
        val phase = EnergyPhase()
        val state = PlayerState(energy = 5, position = 10)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = phase.execute(state, context)
        assertEquals(4, result.energy)
        assertEquals(10, result.position)
    }

    @Test
    fun testNoEnergyDecreaseOnRest() {
        val phase = EnergyPhase()
        val state = PlayerState(energy = 5, position = 10)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.ENERGY,
            movementBonus = 0
        )

        val result = phase.execute(state, context)
        assertEquals(5, result.energy)
        assertEquals(10, result.position)
    }

    @Test
    fun testHalfPositionWhenNoEnergy() {
        val phase = EnergyPhase()
        val state = PlayerState(energy = 0, position = 10)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = phase.execute(state, context)
        assertEquals(0, result.energy)
        assertEquals(5, result.position)
    }
}