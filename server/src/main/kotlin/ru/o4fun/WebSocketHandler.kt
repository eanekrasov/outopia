package ru.o4fun

import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.o4fun.exceptions.KindUnknownException
import ru.o4fun.models.incoming.IncomingEvent
import ru.o4fun.models.outgoing.OutgoingEvent
import java.util.*

@Service
class WebSocketHandler(
    private val json: Json,
    private val eventPublisher: ApplicationEventPublisher
) : TextWebSocketHandler() {
    override fun afterConnectionEstablished(s: WebSocketSession) {
        val params = s.params()
        if (params.containsKey("id")) {
            val session = SocketSession(s, params)
            s.attributes["session"] = session
            eventPublisher.publishEvent(ConnectedEvent(session))
        } else s.close()
    }

    override fun handleTextMessage(s: WebSocketSession, textMsg: TextMessage) {
        try {
            val session = s.attributes.getValue("session") as PlayerSession
            val event = json.parse(IncomingEvent.serializer(), textMsg.payload)
            println("in: $event")
            eventPublisher.publishEvent(MessageEvent(session, event))
        } catch (e: KindUnknownException) {
            // you don`t need it in most cases
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun afterConnectionClosed(s: WebSocketSession, status: CloseStatus) {
        if (s.attributes.containsKey("session")) {
            val session = s.attributes.remove("session") as PlayerSession
            eventPublisher.publishEvent(DisconnectedEvent(session, status))
        }
    }

    inner class SocketSession(
        private val s: WebSocketSession,
        private val params: Map<String, String>
    ) : PlayerSession {
        override val id by lazy { params.getValue("id") }

        override fun send(event: OutgoingEvent) {
            println("out: $event")
            s.sendMessage(TextMessage(json.stringify(event)))
        }
    }

    inner class MessageEvent(val s: PlayerSession, val event: IncomingEvent)
    inner class DisconnectedEvent(val s: PlayerSession, val r: CloseStatus?)
    inner class ConnectedEvent(val s: PlayerSession)

    companion object {
        fun WebSocketSession.params() = if (uri!!.query.isNullOrEmpty()) Collections.emptyMap() else uri!!.query!!.split("&").map {
            val idx = it.indexOf("=")
            (if (idx > 0) it.substring(0, idx) else it) to (if (idx > 0 && it.length > idx + 1) it.substring(idx + 1) else "true")
        }.toMap()
    }
}