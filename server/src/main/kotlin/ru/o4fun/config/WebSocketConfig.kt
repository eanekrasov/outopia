package ru.o4fun.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import ru.o4fun.components.SocketHandler

@Configuration
class WebSocketConfig(
    private val socketHandler: SocketHandler
) {
    @Bean
    fun handlerMapping(): HandlerMapping = SimpleUrlHandlerMapping().apply {
        urlMap = mapOf("/ws/client" to socketHandler)
        order = Ordered.HIGHEST_PRECEDENCE
    }

    @Bean
    fun webSocketHandlerAdapter() = WebSocketHandlerAdapter()
}