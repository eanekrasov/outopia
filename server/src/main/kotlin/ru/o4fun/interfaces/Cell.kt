package ru.o4fun.interfaces

interface Cell {
    val x: Int
    val y: Int
    val owner: Player?
    val value: Set<Value>
    val discoveredBy: Set<Player>
    val type: CellType
}
