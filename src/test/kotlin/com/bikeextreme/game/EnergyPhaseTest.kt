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
        val movementPhase = MovementPhase()
        val energyPhase = EnergyPhase()

        val state = PlayerState(energy = 0, position = 10)
        val context = PhaseContext(
            dice1 = 6,
            dice2 = 4,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )
        // применяем MovementPhase
        val afterMove = movementPhase.execute(state, context)
        // применяем EnergyPhase
        val result = energyPhase.execute(afterMove, context)

        assertEquals(0, result.energy)
        assertEquals(15, result.position) // 20 - 10 / 2
    }
}