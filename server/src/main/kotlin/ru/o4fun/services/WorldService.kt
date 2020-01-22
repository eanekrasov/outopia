package ru.o4fun.services

import org.springframework.stereotype.Service
import ru.o4fun.properties.AppProperties

@Service
class WorldService(
    private val props: AppProperties
) {
    private val world = (0..props.world.width).map { x -> (0..props.world.height).map { y -> Cell(x, y, "$x $y") }.toTypedArray() }.toTypedArray()

    fun getValue(x: Int, y: Int) = world[x][y].value

    inner class Cell(
        val x: Int = 0,
        val y: Int = 0,
        val value: String = ""
    )
}
