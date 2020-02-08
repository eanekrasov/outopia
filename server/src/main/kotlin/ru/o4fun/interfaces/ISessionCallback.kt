package ru.o4fun.interfaces

import ru.o4fun.models.Incoming

interface ISessionCallback {
    val player: IPlayer
    fun event(e: Incoming)
    fun remove()
}