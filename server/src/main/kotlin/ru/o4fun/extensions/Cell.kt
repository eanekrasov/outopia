package ru.o4fun.extensions

import ru.o4fun.Building
import ru.o4fun.Resource
import ru.o4fun.interfaces.Cell
import ru.o4fun.models.CellImpl
import ru.o4fun.models.Outgoing
import ru.o4fun.models.SquadUnit
import ru.o4fun.models.ValueImpl
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun CellImpl.sendAll(e: Outgoing, parentsLevel: Int) = discoveredBy.forEach { it.send(e, parentsLevel) }

infix fun Cell.distance(cell: Cell) = sqrt(((x - cell.x) * (x - cell.x) + (y - cell.y) * (y - cell.y)).toDouble()).roundToInt()

fun Cell.hasUnits(which: Map<SquadUnit, Long>) = which.all { units.getOrDefault(it.key, 0) >= it.value }

suspend fun CellImpl.buildingIn(which: Building, callback: suspend (ValueImpl.BuildingImpl) -> Unit) = value.forEach {
    if (it is ValueImpl.BuildingImpl && it.building == which) callback(it)
}

suspend fun CellImpl.fieldIn(which: Resource, callback: suspend (ValueImpl.FieldImpl) -> Unit) = value.forEach {
    if (it is ValueImpl.FieldImpl && it.resource == which) callback(it)
}

suspend fun CellImpl.tryTakeUnits(which: Map<SquadUnit, Long>, callback: suspend () -> Unit) = try {
    if (hasUnits(which)) {
        callback()
        minusUnits(which)
        true
    } else false
} catch (e: Exception) {
    false
}

fun CellImpl.minusUnits(which: Map<SquadUnit, Long>) =
    which.forEach { (unit, amount) -> units[unit] = units.getOrDefault(unit, 0) - amount }

fun CellImpl.addUnits(which: Map<SquadUnit, Long>) =
    which.forEach { (unit, amount) -> units[unit] = units.getOrDefault(unit, 0) + amount }
