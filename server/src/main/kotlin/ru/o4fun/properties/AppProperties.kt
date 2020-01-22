package ru.o4fun.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
class AppProperties {
    val paths = Paths()
    val world = World()

    class Paths {
        var images: String = "/images"
    }

    class World {
        var width: Int = 10 // TODO: 100
        var height: Int = 10 // TODO: 100
    }
}