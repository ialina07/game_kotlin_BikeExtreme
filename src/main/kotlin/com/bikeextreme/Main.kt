package com.bikeextreme

import com.bikeextreme.game.GameManager
import com.bikeextreme.game.MoveValidator
import com.bikeextreme.game.PhaseExecutor
import com.bikeextreme.game.WeatherPhase
import com.bikeextreme.game.EventPhase
import com.bikeextreme.game.MovementPhase
import com.bikeextreme.game.EnergyPhase
import com.bikeextreme.game.RestPhase
import com.bikeextreme.game.Phase
import com.bikeextreme.weather.WeatherFactory
import com.bikeextreme.event.EventFactory
import com.bikeextreme.repository.InMemoryGameRepository
import com.bikeextreme.statistics.StatisticsService
import com.bikeextreme.statistics.ReplayService
import com.bikeextreme.ui.ConsoleUI

fun main() {
    val weatherFactory = WeatherFactory()
    val eventFactory = EventFactory()

    val phases: List<Phase> = listOf(
        WeatherPhase(weatherFactory),
        EventPhase(eventFactory),
        MovementPhase(),
        EnergyPhase(),
        RestPhase()
    )

    val phaseExecutor = PhaseExecutor(phases)
    val moveValidator = MoveValidator(phaseExecutor)
    val repository = InMemoryGameRepository()
    val gameManager = GameManager(moveValidator, repository)
    val statisticsService = StatisticsService(repository)
    val replayService = ReplayService(repository, phaseExecutor)

    val consoleUI = ConsoleUI(
        gameManager = gameManager,
        statisticsService = statisticsService,
        replayService = replayService,
        repository = repository
    )

    consoleUI.start()
}