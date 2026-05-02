package com.bikeextreme.domain

import org.junit.Test
import org.junit.Assert.*

class PlayerStateTest {

    @Test
    fun testDefaultValues() {
        val state = PlayerState()
        assertEquals(0, state.position)
        assertEquals(5, state.energy)
        assertEquals(5, state.condition)
        assertEquals(3, state.water)
    }

    @Test
    fun testConstants() {
        assertEquals(10, PlayerState.MAX_ENERGY)
        assertEquals(10, PlayerState.MAX_CONDITION)
        assertEquals(10, PlayerState.MAX_WATER)
        assertEquals(50, PlayerState.TRACK_LENGTH)
    }

    @Test
    fun testIsMaxEnergy() {
        assertTrue(PlayerState(energy = 10).isMaxEnergy())
        assertFalse(PlayerState(energy = 5).isMaxEnergy())
    }

    @Test
    fun testIsMaxCondition() {
        assertTrue(PlayerState(condition = 10).isMaxCondition())
        assertFalse(PlayerState(condition = 5).isMaxCondition())
    }

    @Test
    fun testIsMaxWater() {
        assertTrue(PlayerState(water = 10).isMaxWater())
        assertFalse(PlayerState(water = 3).isMaxWater())
    }
}