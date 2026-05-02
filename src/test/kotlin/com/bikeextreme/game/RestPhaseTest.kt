package com.bikeextreme.game

import com.bikeextreme.domain.PlayerState
import org.junit.Test
import org.junit.Assert.*

class RestPhaseTest {

    @Test
    fun testRestEnergy() {
        val phase = RestPhase()
        val state = PlayerState(energy = 5, condition = 5, water = 3)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.ENERGY,
            movementBonus = 0
        )

        val result = phase.execute(state, context)

        assertEquals(7, result.energy)      // 5 + 2 = 7
        assertEquals(5, result.condition)
        assertEquals(3, result.water)
    }

    @Test
    fun testRestCondition() {
        val phase = RestPhase()
        val state = PlayerState(energy = 5, condition = 5, water = 3)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.CONDITION,
            movementBonus = 0
        )

        val result = phase.execute(state, context)

        assertEquals(5, result.energy)
        assertEquals(6, result.condition)  // 5 + 1 = 6
        assertEquals(3, result.water)
    }

    @Test
    fun testRestWater() {
        val phase = RestPhase()
        val state = PlayerState(energy = 5, condition = 5, water = 3)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.WATER,
            movementBonus = 0
        )

        val result = phase.execute(state, context)

        assertEquals(5, result.energy)
        assertEquals(5, result.condition)
        assertEquals(6, result.water)      // 3 + 3 = 6
    }

    @Test
    fun testRestMaxEnergy() {
        val phase = RestPhase()
        val state = PlayerState(energy = 9, condition = 5, water = 3)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.ENERGY,
            movementBonus = 0
        )

        val result = phase.execute(state, context)

        assertEquals(10, result.energy)     // не выше максимума
    }

    @Test
    fun testRestMaxCondition() {
        val phase = RestPhase()
        val state = PlayerState(energy = 5, condition = 9, water = 3)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.CONDITION,
            movementBonus = 0
        )

        val result = phase.execute(state, context)

        assertEquals(10, result.condition)  // не выше максимума
    }

    @Test
    fun testRestMaxWater() {
        val phase = RestPhase()
        val state = PlayerState(energy = 5, condition = 5, water = 8)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "rest",
            restType = RestType.WATER,
            movementBonus = 0
        )

        val result = phase.execute(state, context)

        assertEquals(10, result.water)      // не выше максимума
    }

    @Test
    fun testNoRestOnMove() {
        val phase = RestPhase()
        val state = PlayerState(energy = 5, condition = 5, water = 3)
        val context = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = phase.execute(state, context)

        assertEquals(5, result.energy)
        assertEquals(5, result.condition)
        assertEquals(3, result.water)
    }
}