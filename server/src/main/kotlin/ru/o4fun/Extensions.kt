package ru.o4fun

import org.springframework.web.socket.WebSocketSession
import ru.o4fun.events.Incoming
import ru.o4fun.events.Outgoing
import ru.o4fun.interfaces.Cell
import ru.o4fun.interfaces.Player
import ru.o4fun.interfaces.Value
import ru.o4fun.models.*
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun PlayerImpl.send(e: Outgoing, withParents: Boolean) {
    sessions.forEach {
        try {
            it.sendMessage(e)
        } catch (e: Exception) {
        }
    }
    if (withParents) parents.flatWalk(Engine.parentsLevel, { it.parents }) {
        sessions.forEach {
            try {
                it.sendMessage(e)
            } catch (e: Exception) {
            }
        }
    }
}

fun CellImpl.sendAll(e: Outgoing, withParents: Boolean) = discoveredBy.forEach { it.send(e, withParents) }

fun <T> Iterable<T>.flatWalk(times: Int, transform: (T) -> Iterable<T>, action: T.() -> Unit) {
    forEach {
        it.action()
        if (times > 0) transform(it).flatWalk(times - 1, transform, action) else it.action()
    }
}

fun WebSocketSession.params() = if (uri!!.query.isNullOrEmpty()) Collections.emptyMap() else uri!!.query!!.split("&").map {
    val idx = it.indexOf("=")
    (if (idx > 0) it.substring(0, idx) else it) to (if (idx > 0 && it.length > idx + 1) it.substring(idx + 1) else "true")
}.toMap()

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

infix fun Cell.distance(cell: Cell) = sqrt(((x - cell.x) * (x - cell.x) + (y - cell.y) * (y - cell.y)).toDouble()).roundToInt()

fun Player.hasResources(which: Map<Resource, Long>) = which.all { resources.getOrDefault(it.key, 0) >= it.value }

fun Value.Barracks.hasUnits(which: Map<SquadUnit, Long>) = which.all { units.getOrDefault(it.key, 0) >= it.value }

fun Cell.cityIn(callback: (ValueImpl.BarracksImpl) -> Unit) = value.forEach {
    if (it is ValueImpl.BarracksImpl) callback(it)
}

fun Cell.fieldIn(which: Resource, callback: (ValueImpl.FieldImpl) -> Unit) = value.forEach {
    if (it is ValueImpl.FieldImpl && it.resource == which) callback(it)
}

fun PlayerImpl.tryTakeResources(which: Map<Resource, Long>, callback: () -> Unit) = try {
    if (hasResources(which)) {
        callback()
        minusResources(which)
        true
    } else false
} catch (e: Exception) {
    false
}

fun ValueImpl.BarracksImpl.tryTakeUnits(which: Map<SquadUnit, Long>, callback: () -> Unit) = try {
    if (hasUnits(which)) {
        callback()
        minusUnits(which)
        true
    } else false
} catch (e: Exception) {
    false
}

fun PlayerImpl.minusResources(which: Map<Resource, Long>) =
    which.forEach { resources[it.key] = resources.getOrDefault(it.key, 0) - it.value }

fun ValueImpl.BarracksImpl.minusUnits(which: Map<SquadUnit, Long>) =
    which.forEach { (unit, amount) -> units[unit] = units.getOrDefault(unit, 0) - amount }

fun ValueImpl.BarracksImpl.addUnits(which: Map<SquadUnit, Long>) =
    which.forEach { (unit, amount) -> units[unit] = units.getOrDefault(unit, 0) + amount }

fun Map<SquadUnit, Long>.hasUnits() = any { unit -> unit.value > 0 }

inline fun <reified T> Engine.map(transform: (Cell) -> T): Array<Array<T>> = mapXY { (x, y) -> transform(get(x, y)) }

inline fun <reified T> mapXY(transform: (Pair<Int, Int>) -> T): Array<Array<T>> =
    (0 until Engine.width).map { x -> (0 until Engine.height).map { y -> transform(x to y) }.toTypedArray() }.toTypedArray()

fun PlayerImpl.updateResources() = owned.forEach { cell ->
    cell.value.forEach {
        when (it) {
            is ValueImpl.FieldImpl -> {
                resources[it.resource] = resources.getOrDefault(it.resource, 0) + it.level
            }
        }
    }
}

fun SquadImpl.destroyedEvent() = Outgoing.SquadDestroyed(origin.cell.x, origin.cell.y, target.cell.x, target.cell.y, units)
fun SquadImpl.sentEvent() = Outgoing.SquadSent(origin.cell.x, origin.cell.y, target.cell.x, target.cell.y, units)
fun ValueImpl.BarracksImpl.upgradedEvent() = Outgoing.BarracksUpgraded(cell.x, cell.y, level)
fun ValueImpl.FieldImpl.upgradedEvent() = Outgoing.FieldUpgraded(cell.x, cell.y, resource, level)
fun CellImpl.ownedEvent() = Outgoing.Owned(x, y, owner)
fun CellImpl.discoveredEvent() = Outgoing.Discovered(x, y, value)
fun Incoming.UnitBuy.boughtEvent() = Outgoing.UnitBought(x, y, units)
fun PlayerImpl.resourcesEvent() = Outgoing.Resources(resources)