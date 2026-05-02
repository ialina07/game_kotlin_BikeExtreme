package com.bikeextreme.game

import com.bikeextreme.game.RestType

/**
 * Контекст, передаваемый между фазами хода.
 * Содержит все данные, необходимые для выполнения фаз:
 * - dice1, dice2 — значения кубиков
 * - moveType — "move" или "rest"
 * - restType — тип отдыха (для фазы отдыха)
 * - movementBonus — накопительные бонусы к движению (ветер, спринт, спуск)
 */
data class PhaseContext(
    val dice1: Int,
    val dice2: Int,
    val moveType: String,
    val restType: RestType?,
    var movementBonus: Int = 0
)