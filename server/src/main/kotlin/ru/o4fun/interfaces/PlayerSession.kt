package ru.o4fun.interfaces

import ru.o4fun.events.Outgoing

interface PlayerSession {
    fun sendMessage(msg: Outgoing)
}