package ru.o4fun.services

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.o4fun.WebSocketHandler
import ru.o4fun.models.incoming.IncomingEvent
import ru.o4fun.models.outgoing.OutgoingEvent

/**
 * cities in cells
 *
 * fog of war with events in their cells
 *
 * friends with sharing events
 */
@Service
class OutopiaService(
    private val world: WorldService,
    private val players: PlayersService
) {
    fun getValue(id: String, x: Int, y: Int) = OutgoingEvent.GetValue(id, x, y, world.getValue(x, y))
    fun reveal(id: String, x: Int, y: Int) = OutgoingEvent.Reveal(id, x, y, players[id].reveal(x, y))

    @EventListener
    fun onMessage(e: WebSocketHandler.MessageEvent) = when (e.event) {
        is IncomingEvent.GetValue -> e.s.send(getValue(e.s.id, e.event.x, e.event.y))
        is IncomingEvent.Reveal -> e.s.send(reveal(e.s.id, e.event.x, e.event.y))
    }

    @EventListener
    fun onClientConnected(e: WebSocketHandler.ConnectedEvent) {
    }

    @EventListener
    fun onClientDisconnected(e: WebSocketHandler.DisconnectedEvent) {
    }
}

