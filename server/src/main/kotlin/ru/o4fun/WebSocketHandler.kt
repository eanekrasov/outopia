package ru.o4fun

import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.o4fun.events.Incoming
import ru.o4fun.events.Outgoing
import ru.o4fun.exceptions.KindUnknownException
import ru.o4fun.interfaces.PlayerSession
import ru.o4fun.interfaces.SessionCallback
import ru.o4fun.services.OutopiaService

@Service
class WebSocketHandler(
    outopiaService: OutopiaService
) : TextWebSocketHandler() {
    val engine = outopiaService.engine
    override fun afterConnectionEstablished(s: WebSocketSession) {
        val params = s.params()
        if (params.containsKey("id")) {
            s.attributes["session"] = engine.addSession(params.getValue("id"), SocketSession(s))
        } else s.close()
    }

    override fun handleTextMessage(s: WebSocketSession, textMsg: TextMessage) {
        try {
            (s.attributes.getValue("session") as SessionCallback).event(Json.parse(Incoming.serializer(), textMsg.payload))
        } catch (e: KindUnknownException) {
            // you don`t need it in most cases
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun afterConnectionClosed(s: WebSocketSession, status: CloseStatus) {
        (s.attributes.remove("session") as SessionCallback).remove()
    }

    class SocketSession(private val s: WebSocketSession) : PlayerSession {
        override fun sendMessage(msg: Outgoing) = s.sendMessage(msg.toMsg())
    }
}
