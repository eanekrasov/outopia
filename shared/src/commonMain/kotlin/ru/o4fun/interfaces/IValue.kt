package ru.o4fun.interfaces

import ru.o4fun.Resource

interface IValue {
    val x: Int
    val y: Int
    val level: Int

    interface Building : IValue {
        val building: ru.o4fun.Building
    }

    interface Field : IValue {
        val resource: Resource
    }
}