package ru.o4fun.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
class AppProperties {
    val bots = Bots()
    val world = World()
    val map = Map()

    class World {
        var verbose = false
        var parentsLevel = 2
        var tickDelay = 1000L
        var width = 512
        var height = 512
    }

    class Bots {
        var verbose = false
    }

    class Map {
        var cache = false
    }
}