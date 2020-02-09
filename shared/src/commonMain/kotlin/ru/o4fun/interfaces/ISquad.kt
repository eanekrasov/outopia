package ru.o4fun.interfaces

import ru.o4fun.enums.SquadUnit

interface ISquad {
    val owner: IPlayer
    val origin: ICell
    val target: ICell
    val units: Map<SquadUnit, Long>
    val timeout: Int
}