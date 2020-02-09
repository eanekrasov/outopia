package ru.o4fun.enums

enum class SquadUnit(
    val damage: Long,
    val health: Long,
    val cost: Map<Resource, Long>
) {
    Infantry(10, 10, mapOf(Resource.Gold to 10L)),
    Gunner(5, 5, mapOf(Resource.Gold to 10L))
}
