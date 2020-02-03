package ru.o4fun.interfaces

import ru.o4fun.models.Incoming

interface SessionCallback {
    val player: Player
    fun event(e: Incoming)
    fun remove()
}