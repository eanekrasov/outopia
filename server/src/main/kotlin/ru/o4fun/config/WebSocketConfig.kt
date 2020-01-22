package ru.o4fun.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import ru.o4fun.WebSocketHandler

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val handler: WebSocketHandler
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(handler, "/ws/client").setAllowedOrigins("*")
    }
}