package com.bikeextreme.event

import org.junit.Test
import org.junit.Assert.*

class EventFactoryTest {

    private val factory = EventFactory()

    @Test
    fun getEventByDice() {
        assertTrue(factory.getEvent(1) is SpringEvent)
        assertTrue(factory.getEvent(2) is PunctureEvent)
        assertTrue(factory.getEvent(3) is SnackEvent)
        assertTrue(factory.getEvent(4) is RepairEvent)
        assertTrue(factory.getEvent(5) is DownhillEvent)
        assertTrue(factory.getEvent(6) is SprintEvent)
    }

    @Test
    fun getEventWithInvalidDice() {
        assertTrue(factory.getEvent(7) is SpringEvent)
        assertTrue(factory.getEvent(0) is SpringEvent)
    }
}