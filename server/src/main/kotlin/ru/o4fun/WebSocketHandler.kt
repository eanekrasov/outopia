package ru.o4fun

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.o4fun.events.Incoming
import ru.o4fun.events.Outgoing
import ru.o4fun.exceptions.KindUnknownException
import ru.o4fun.interfaces.PlayerSession
import ru.o4fun.interfaces.SessionCallback
import ru.o4fun.services.OutopiaService
import java.util.*

@Service
class SocketHandler(
    outopiaService: OutopiaService
) : WebSocketHandler {
    val engine = outopiaService.engine

    override fun handle(session: WebSocketSession): Mono<Void> {
        val params = session.params()
        return if (params.containsKey("id")) {
            lateinit var callback: SessionCallback
            val sender = Flux.create<WebSocketMessage> {
                callback = engine.addSession(params.getValue("id"), object : PlayerSession {
                    override fun sendMessage(msg: Outgoing) {
                        it.next(session.textMessage(json.stringify(msg)))
                    }
                })
            }
            val receiver = { it: WebSocketMessage ->
                try {
                    callback.event(Json.parse(Incoming.serializer(), it.payloadAsText))
                } catch (e: KindUnknownException) {
                    // you don`t need it in most cases
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Mono.zip(
                session.receive().doOnNext(receiver).doOnComplete { callback.remove() }.doOnError { callback.remove() }.then(),
                session.send(sender)
            ).then()
        } else session.close()
    }

    companion object {
        val json = Json(configuration = JsonConfiguration.Default.copy(classDiscriminator = "type"))

        fun WebSocketSession.params() = if (handshakeInfo.uri.query.isNullOrEmpty()) Collections.emptyMap() else handshakeInfo.uri.query!!.split("&").map {
            val idx = it.indexOf("=")
            (if (idx > 0) it.substring(0, idx) else it) to (if (idx > 0 && it.length > idx + 1) it.substring(idx + 1) else "true")
        }.toMap()
    }
}
