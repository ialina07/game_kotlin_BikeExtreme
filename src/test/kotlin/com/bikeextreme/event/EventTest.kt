package com.bikeextreme.event

import com.bikeextreme.domain.PlayerState
import com.bikeextreme.game.PhaseContext
import org.junit.Test
import org.junit.Assert.*

class EventTest {

    private val context = PhaseContext(
        dice1 = 1,
        dice2 = 1,
        moveType = "move",
        restType = null,
        movementBonus = 0
    )

    @Test
    fun testSpringEvent() {
        val state = PlayerState(water = 3)
        val event = SpringEvent()
        val result = event.apply(state, context)

        assertEquals(4, result.water)
    }

    @Test
    fun testSpringEventMaxWater() {
        val state = PlayerState(water = PlayerState.MAX_WATER)
        val event = SpringEvent()
        val result = event.apply(state, context)

        assertEquals(PlayerState.MAX_WATER, result.water)
    }

    @Test
    fun testPunctureEvent() {
        val state = PlayerState(condition = 5)
        val event = PunctureEvent()
        val result = event.apply(state, context)

        assertEquals(3, result.condition)
    }

    @Test
    fun testPunctureEventWithoutCondition() {
        val state = PlayerState(condition = 0)
        val event = PunctureEvent()
        val result = event.apply(state, context)

        assertEquals(0, result.condition)
    }

    @Test
    fun testSnackEvent() {
        val state = PlayerState(energy = 5)
        val event = SnackEvent()
        val result = event.apply(state, context)

        assertEquals(6, result.energy)
    }

    @Test
    fun testSnackEventMaxEnergy() {
        val state = PlayerState(energy = PlayerState.MAX_ENERGY)
        val event = SnackEvent()
        val result = event.apply(state, context)

        assertEquals(PlayerState.MAX_ENERGY, result.energy)
    }

    @Test
    fun testRepairEvent() {
        val state = PlayerState(condition = 5)
        val event = RepairEvent()
        val result = event.apply(state, context)

        assertEquals(7, result.condition)
    }

    @Test
    fun testRepairEventMaxCondition() {
        val state = PlayerState(condition = PlayerState.MAX_CONDITION)
        val event = RepairEvent()
        val result = event.apply(state, context)

        assertEquals(PlayerState.MAX_CONDITION, result.condition)
    }

    @Test
    fun testDownhillEvent() {
        val state = PlayerState(position = 10)
        val event = DownhillEvent()
        val result = event.apply(state, context)

        // спуск не меняет состояние, только дает бонус в MovementPhase
        assertEquals(10, result.position)
    }

    @Test
    fun testSprintEvent() {
        val state = PlayerState(position = 10, energy = 5)
        val event = SprintEvent()

        val sprintContext = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )

        val result = event.apply(state, sprintContext)

        assertEquals(10, result.position)
        assertEquals(4, result.energy)
        assertEquals(3, sprintContext.movementBonus)
    }

    @Test
    fun testSprintEventWithoutEnergy() {
        val state = PlayerState(position = 10, energy = 0)
        val event = SprintEvent()
        val sprintContext = PhaseContext(
            dice1 = 1,
            dice2 = 1,
            moveType = "move",
            restType = null,
            movementBonus = 0
        )
        val result = event.apply(state, sprintContext)

        assertEquals(10, result.position)
        assertEquals(0, result.energy)
        assertEquals(0, sprintContext.movementBonus)  // бонус не даётся
    }

    @Test
    fun testSprintEventExtraCells() {
        val event = SprintEvent()
        assertEquals(3, event.extraCells())
    }

    @Test
    fun testDownhillEventExtraCells() {
        val event = DownhillEvent()
        assertEquals(2, event.extraCells())
    }
}