package ru.o4fun.services

import org.springframework.stereotype.Service
import ru.o4fun.PlayerSession
import ru.o4fun.models.outgoing.OutgoingEvent

@Service
class BotsService(
    private val world: WorldService,
    private val players: PlayersService
) {
    inner class BotSession(
        override val id: String
    ) : PlayerSession {
        override fun send(event: OutgoingEvent) {
            println("out: $event")
        }
    }

    companion object {
        const val COUNT = 100
    }
}