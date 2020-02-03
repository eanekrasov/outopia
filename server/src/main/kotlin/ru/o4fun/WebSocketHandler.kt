package ru.o4fun

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import ru.o4fun.events.Incoming
import ru.o4fun.events.Outgoing
import ru.o4fun.exceptions.KindUnknownException
import ru.o4fun.interfaces.PlayerSession
import ru.o4fun.services.OutopiaService
import java.util.*

@Service
class SocketHandler(
    outopiaService: OutopiaService
) : WebSocketHandler {
    val engine = outopiaService.engine

    override fun handle(session: WebSocketSession) = with(session.params()) {
        if (containsKey("id")) SocketSession(session, getValue("id")).process() else session.close()
    }

    inner class SocketSession(
        private val session: WebSocketSession,
        id: String
    ) : PlayerSession {
        private val callback = engine.addSession(id, this)

        fun process() = session.receive()
            .doOnNext {
                try {
                    callback.event(Json.parse(Incoming.serializer(), it.payloadAsText))
                } catch (e: KindUnknownException) {
                    // you don`t need it in most cases
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .doOnComplete {
                callback.remove()
            }
            .doOnError {
                callback.remove()
            }.then()

        override fun sendMessage(msg: Outgoing) {
            session.send(Flux.just(session.textMessage(json.stringify(msg))))
        }
    }

    companion object {
        val json = Json(configuration = JsonConfiguration.Default.copy(classDiscriminator = "type"))

        fun WebSocketSession.params() = if (handshakeInfo.uri.query.isNullOrEmpty()) Collections.emptyMap() else handshakeInfo.uri.query!!.split("&").map {
            val idx = it.indexOf("=")
            (if (idx > 0) it.substring(0, idx) else it) to (if (idx > 0 && it.length > idx + 1) it.substring(idx + 1) else "true")
        }.toMap()
    }
}
