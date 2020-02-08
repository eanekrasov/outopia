package ru.o4fun.extensions

import ru.o4fun.Resource
import ru.o4fun.interfaces.ICell
import ru.o4fun.models.World
import ru.o4fun.models.SquadUnit
import kotlin.math.min

val Map<SquadUnit, Long>.cost: Map<Resource, Long>
    get() {
        val total = mutableMapOf<Resource, Long>()
        forEach { (unit, amount) ->
            unit.cost.forEach { (resource, value) ->
                val v = total.getOrDefault(resource, 0)
                total[resource] = v + amount * value
            }
        }
        return total
    }

fun MutableMap<SquadUnit, Long>.strike(units: MutableMap<SquadUnit, Long>) {
    var damage = map { (unit, amount) -> unit.damage * amount }.sum()
    SquadUnit.values().forEach { unit ->
        if (damage > 0) {
            val before = units.getOrDefault(unit, 0)
            val killed = min(before, damage / unit.health)
            units[unit] = before - killed
            damage -= killed * unit.health
        }
    }
}

fun Map<SquadUnit, Long>.hasUnits() = any { unit -> unit.value > 0 }

inline fun <reified T> World.map(transform: (ICell) -> T): Array<Array<T>> = mapXY { (x, y) -> transform(get(x, y)) }

inline fun <reified T> World.mapXY(transform: (Pair<Int, Int>) -> T): Array<Array<T>> =
    (0 until props.width).map { x -> (0 until props.height).map { y -> transform(x to y) }.toTypedArray() }.toTypedArray()
