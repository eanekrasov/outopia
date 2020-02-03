package ru.o4fun

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.o4fun.properties.AppProperties

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
