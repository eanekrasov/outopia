package ru.o4fun.interfaces

import ru.o4fun.models.SquadUnit

interface Squad {
    val owner: Player
    val origin: Value.Barracks
    val target: Value.Barracks
    val units: Map<SquadUnit, Long>
    val timeout: Int
}