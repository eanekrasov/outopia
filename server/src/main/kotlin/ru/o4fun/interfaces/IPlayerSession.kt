package ru.o4fun.interfaces

import ru.o4fun.models.Outgoing

interface IPlayerSession {
    fun sendMessage(msg: Outgoing)
}