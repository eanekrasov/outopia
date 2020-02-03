package ru.o4fun.components

import org.springframework.boot.actuate.health.Health

import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class CustomHealthIndicator : HealthIndicator {
    override fun health(): Health {
        val errorCode = performHealthCheck()
        return if (errorCode != 0) {
            Health.down().withDetail("Error Code", errorCode).build()
        } else Health.up().build()
    }

    // You can call Health check related code like below and that method can return the appropriate code.
    fun performHealthCheck() = 0
}