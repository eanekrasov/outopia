package ru.o4fun.interfaces

import ru.o4fun.Resource

interface Value {
    val cell: Cell
    val level: Int

    interface Building : Value {
        val building: ru.o4fun.Building
    }

    interface Field : Value {
        val resource: Resource
    }
}