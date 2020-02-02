package ru.o4fun.interfaces

import ru.o4fun.models.SquadUnit

interface Cell {
    val x: Int
    val y: Int
    val owner: Player?
    val value: Set<Value>
    val discoveredBy: Set<Player>
    val type: CellType
    val units: Map<SquadUnit, Long>
}
