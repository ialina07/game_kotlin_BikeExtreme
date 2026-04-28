package com.bikeextreme.event

class EventFactory {
    fun getEvent(dice: Int): Event {
        return when (dice) {
            1 -> SpringEvent()
            2 -> PunctureEvent()
            3 -> SnackEvent()
            4 -> RepairEvent()
            5 -> DownhillEvent()
            6 -> SprintEvent()
            else -> SpringEvent()
        }
    }
}
