package ru.o4fun.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
class WebSecurityConfig {
    @Bean
    fun security(http: ServerHttpSecurity): SecurityWebFilterChain = http
        .authorizeExchange {
            it.pathMatchers("/**").permitAll()//.hasAuthority("SCOPE_message.read")
        }
        .build()
}