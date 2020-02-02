package ru.o4fun.models

import ru.o4fun.Resource

enum class SquadUnit(
    val damage: Long,
    val health: Long,
    val cost: Map<Resource, Long>
) {
    INFANTRY(10, 10, mapOf(Resource.GOLD to 10L)),
    GUNNER(5, 5, mapOf(Resource.GOLD to 10L))
}
