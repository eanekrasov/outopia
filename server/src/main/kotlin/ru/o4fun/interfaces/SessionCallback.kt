package ru.o4fun.interfaces

import ru.o4fun.events.Incoming

interface SessionCallback {
    val player: Player
    fun event(e: Incoming)
    fun remove()
}