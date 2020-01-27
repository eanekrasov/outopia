package ru.o4fun.interfaces

import ru.o4fun.Resource
import ru.o4fun.models.SquadUnit

interface Value {
    val cell: Cell

    interface Barracks : Value {
        val level: Int
        val units: Map<SquadUnit, Long>
    }

    interface Field : Value {
        val level: Int
        val resource: Resource
    }
}