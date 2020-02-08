package ru.o4fun.components

import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import ru.o4fun.extensions.parseIncoming
import ru.o4fun.extensions.stringify
import ru.o4fun.interfaces.PlayerSession
import ru.o4fun.models.Outgoing
import ru.o4fun.services.OutopiaService
import java.util.*

@Component
class SocketHandler(
    outopiaService: OutopiaService
) : WebSocketHandler {
    val world = outopiaService.world

    override fun handle(session: WebSocketSession) = with(session.params()) {
        if (containsKey("id")) SocketSession(session, getValue("id")).process() else session.close()
    }

    inner class SocketSession(
        private val session: WebSocketSession,
        id: String
    ) : PlayerSession {
        private val callback = world.addSession(id, this)

        fun process() = session.receive().doOnNext {
            callback.event(it.payloadAsText.parseIncoming())
        }.doFinally {
            callback.remove()
        }.then()

        override fun sendMessage(msg: Outgoing) {
            session.send(Flux.just(session.textMessage(msg.stringify())))
        }
    }

    companion object {
        fun WebSocketSession.params() = if (handshakeInfo.uri.query.isNullOrEmpty()) Collections.emptyMap() else handshakeInfo.uri.query!!.split("&").map {
            val idx = it.indexOf("=")
            (if (idx > 0) it.substring(0, idx) else it) to (if (idx > 0 && it.length > idx + 1) it.substring(idx + 1) else "true")
        }.toMap()
    }
}
