package ru.o4fun

import ru.o4fun.models.outgoing.OutgoingEvent

interface PlayerSession {
    val id: String

    fun send(event: OutgoingEvent)
}