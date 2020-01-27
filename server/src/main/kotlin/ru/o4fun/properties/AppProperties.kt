package ru.o4fun.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
class AppProperties {
    val paths = Paths()

    class Paths {
        var images: String = "/images"
    }
}