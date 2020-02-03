package ru.o4fun.interfaces

import ru.o4fun.models.Outgoing

interface PlayerSession {
    fun sendMessage(msg: Outgoing)
}