package ru.o4fun.services

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.o4fun.PlayerSession
import ru.o4fun.WebSocketHandler
import ru.o4fun.properties.AppProperties

@Service
class PlayersService(
    private val props: AppProperties
) {
    private val players = mutableMapOf<String, Player>()

    operator fun get(key: String) = players.getOrPut(key) { Player() }

    @EventListener
    fun onClientConnected(e: WebSocketHandler.ConnectedEvent) {
        this[e.s.id].addSession(e.s)
    }

    @EventListener
    fun onClientDisconnected(e: WebSocketHandler.DisconnectedEvent) {
        this[e.s.id].removeSession(e.s)
    }

    inner class Player {
        private val revealed = mutableMapOf<Int, Boolean>()
        private val sessions = mutableSetOf<PlayerSession>()

        fun isRevealed(x: Int, y: Int) = revealed.getOrDefault(y * props.world.height + x, false)

        fun reveal(x: Int, y: Int) : Boolean {
            revealed[y * props.world.height + x] = true
            // TODO: send event
            return true
        }

        fun addSession(s: PlayerSession) {
            sessions.add(s)
        }

        fun removeSession(s: PlayerSession) {
            sessions.remove(s)
        }
    }
}
