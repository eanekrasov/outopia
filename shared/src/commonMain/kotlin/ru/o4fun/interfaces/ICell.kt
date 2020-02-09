package ru.o4fun.interfaces

import ru.o4fun.CellType
import ru.o4fun.models.SquadUnit

interface ICell {
    val x: Int
    val y: Int
    val owner: IPlayer?
    val value: Set<IValue>
    val discoveredBy: Set<IPlayer>
    val type: CellType
    val units: Map<SquadUnit, Long>
}
