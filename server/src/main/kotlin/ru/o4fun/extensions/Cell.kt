package ru.o4fun.extensions

import ru.o4fun.enums.Building
import ru.o4fun.enums.Resource
import ru.o4fun.interfaces.ICell
import ru.o4fun.models.Cell
import ru.o4fun.models.Outgoing
import ru.o4fun.enums.SquadUnit
import ru.o4fun.models.Value
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun Cell.sendAll(e: Outgoing, parentsLevel: Int) = discoveredBy.forEach { it.send(e, parentsLevel) }

infix fun ICell.distance(cell: ICell) = sqrt(((x - cell.x) * (x - cell.x) + (y - cell.y) * (y - cell.y)).toDouble()).roundToInt()

fun ICell.hasUnits(which: Map<SquadUnit, Long>) = which.all { units.getOrDefault(it.key, 0) >= it.value }

suspend fun Cell.buildingIn(which: Building, callback: suspend (Value.Building) -> Unit) = value.forEach {
    if (it is Value.Building && it.building == which) callback(it)
}

suspend fun Cell.fieldIn(which: Resource, callback: suspend (Value.Field) -> Unit) = value.forEach {
    if (it is Value.Field && it.resource == which) callback(it)
}

suspend fun Cell.tryTakeUnits(which: Map<SquadUnit, Long>, callback: suspend () -> Unit) = try {
    if (hasUnits(which)) {
        callback()
        minusUnits(which)
        true
    } else false
} catch (e: Exception) {
    false
}

fun Cell.minusUnits(which: Map<SquadUnit, Long>) =
    which.forEach { (unit, amount) -> units[unit] = units.getOrDefault(unit, 0) - amount }

fun Cell.addUnits(which: Map<SquadUnit, Long>) =
    which.forEach { (unit, amount) -> units[unit] = units.getOrDefault(unit, 0) + amount }
