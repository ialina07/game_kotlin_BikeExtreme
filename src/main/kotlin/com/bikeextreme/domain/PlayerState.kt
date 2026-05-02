package com.bikeextreme.domain

data class PlayerState(
    val position: Int = 0,
    val energy: Int = 5,
    val condition: Int = 5,
    val water: Int = 3
) {
    companion object {
        const val MAX_ENERGY = 10
        const val MAX_CONDITION = 10
        const val MAX_WATER = 10
        const val TRACK_LENGTH = 50
    }

    fun isMaxEnergy() = energy == MAX_ENERGY
    fun isMaxCondition() = condition == MAX_CONDITION
    fun isMaxWater() = water == MAX_WATER
}