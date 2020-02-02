package ru.o4fun.models

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import ru.o4fun.*
import ru.o4fun.annotations.DslScope
import ru.o4fun.events.Incoming
import ru.o4fun.interfaces.*
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

class Engine {

    // region public

    val allPlayers: Collection<Player> get() = players.values

    val allSquads: Set<Squad> get() = squads

    operator fun get(x: Int, y: Int): Cell = if (x in (0 until width) && y in (0 until height)) cells[x][y] else throw Exception()

    operator fun get(id: String): Player? = players[id]

    fun edit(block: WorldScope.() -> Unit) = WorldScope().block()

    fun addSession(id: String, session: PlayerSession): SessionCallback {
        val player = players.getOrPut(id) { PlayerImpl(id) }
        player.sessions.add(session)

        return object : SessionCallback {
            override val player = player

            override fun event(e: Incoming) {
                // println("in: $e")
                player.onIncomingEvent(e)
            }

            override fun remove() {
                player.sessions.remove(session)
            }
        }
    }

    suspend fun startScheduler(verbose: Boolean = false, callback: (Scheduler) -> Unit) {
        var time = 0L
        var countExec: Int
        var count: Int
        val pending = mutableListOf<Task>()
        val scheduler = Scheduler(pending)
        val tasks = mutableListOf(
            Task(1) { callback(scheduler);true },
            Task(1) { updateResources();updateSquads();false }
        )
        while (true) {
            if (tickDelay - time > 0) delay(tickDelay - time)
            countExec = 0
            count = 0
            time = measureTimeMillis {
                with(tasks.listIterator()) {
                    while (hasNext()) {
                        count++
                        val t = next()
                        if (++t.ticks >= t.timeout) {
                            t.ticks = 0
                            countExec++
                            if (t.callback(scheduler)) remove()
                        }
                        pending.forEach { add(it) }
                        pending.clear()
                    }
                }
            }
            if (verbose) println("tick: $countExec/$count tasks in ${time}ms")
        }
    }

    // endregion

    private val cells: Array<Array<CellImpl>> = mapXY { (x, y) -> CellImpl(x, y) }
    private val players = mutableMapOf<String, PlayerImpl>()
    private val squads = mutableSetOf<SquadImpl>()

    private suspend fun updateResources() = coroutineScope {
        players.values.map {
            async {
                it.updateResources()
                it.send(it.resourcesEvent(), false)
            }
        }.awaitAll()
    }

    private suspend fun updateSquads() = coroutineScope {
        squads.forEach { it.timeout-- }
        val processed = squads.filter { it.timeout <= 0 }
        if (processed.isNotEmpty()) {
            squads.removeAll(processed)
            val grouped = processed.groupBy { it.target }
            grouped.map { (cell, list) ->
                async {
                    list.filter { cell.owner == it.owner }.forEach { cell.addUnits(it.units) }
                    val attackers = list.filter { cell.owner != it.owner }
                    attackers.forEach { squad ->
                        println("${squad.units} attacks ${cell.units} ${cell.x} ${cell.y}")
                        val attacker = squad.units.toMutableMap()
                        attacker.strike(cell.units)
                        if (cell.units.hasUnits()) {
                            cell.units.strike(attacker)
                            if (attacker.hasUnits()) {
                                squads.add(SquadImpl(squad.owner, cell, squad.origin, attacker))
                            } else {
                                squad.owner.send(squad.destroyedEvent(), true)
                            }
                        } else {
                            cell.owner?.owned?.remove(cell)
                            squad.owner.owned.add(cell)
                            cell.addUnits(attacker)
                            cell.sendAll(cell.ownedEvent(), true)
                        }
                    }
                }
            }.awaitAll()
        }
    }

    private fun PlayerImpl.onIncomingEvent(e: Incoming): Boolean = try {
        val cell = cells[e.x][e.y]
        when (e) {
            is Incoming.SquadSend -> if (cell.owner == this) cell.tryTakeUnits(e.units) {
                val target = cells[e.tx][e.ty]
                val squad = SquadImpl(this, cell, target, e.units)
                println("$id sending ${squad.units} from ${e.x} ${e.y} to ${e.tx} ${e.ty} (${squad.timeout})")
                squads.add(squad)
                cell.sendAll(squad.sentEvent(), true)
            }
            is Incoming.UnitBuy -> if (cell.owner == this) {
                if (tryTakeResources(e.units.cost) { cell.addUnits(e.units) }) {
                    cell.sendAll(e.boughtEvent(), true)
                }
            }
            is Incoming.Discover -> {
                discovered.add(cell)
                send(cell.discoveredEvent(), true)
            }
            is Incoming.Own -> if (cell.owner == null) {
                owned.add(cell)
                cell.sendAll(cell.ownedEvent(), true)
            }
            is Incoming.BuildingUpgrade -> if (cell.owner == this) cell.buildingIn(e.building) {
                if (tryTakeResources(it.upgradeCost) { it.level++ }) {
                    cell.sendAll(it.upgradedEvent(), true)
                }
            }
            is Incoming.FieldUpgrade -> if (cell.owner == this) cell.fieldIn(e.resource) {
                if (tryTakeResources(it.upgradeCost) { it.level++ }) {
                    cell.sendAll(it.upgradedEvent(), true)
                }
            }
        }
        true
    } catch (e: Exception) {
        false
    }

    // region dsl

    @DslScope
    inner class WorldScope {
        private val r = Random()

        fun placeAt(x: Int = randomGaussian(width), y: Int = randomGaussian(height), block: CellScope.() -> Unit) =
            CellScope(cells[x][y]).apply(block)

        private fun randomGaussian(max: Int): Int {
            var v: Int
            do {
                v = abs(r.nextGaussian() * max).roundToInt()
            } while (v !in 0 until max)
            return v
        }
    }

    @DslScope
    inner class CellScope(
        val cell: CellImpl
    ) {
        fun field(which: Resource, level: Int = 1, callback: (ValueImpl.FieldImpl) -> Unit = {}) =
            call(callback, { it.resource == which }) { ValueImpl.FieldImpl(cell, which, level) }

        fun building(which: Building, level: Int = 1, callback: (ValueImpl.BuildingImpl) -> Unit = {}) =
            call(callback, { it.building == which }) { ValueImpl.BuildingImpl(cell, which, level) }

        private inline fun <reified T : ValueImpl> call(callback: (T) -> Unit, denyIf: (T) -> Boolean = { true }, init: () -> T?) =
            if (!cell.value.any { it is T && denyIf(it) }) init()?.also {
                cell.value.add(it)
                callback(it)
            } else null
    }

    // endregion

    companion object {
        const val parentsLevel = 2
        const val tickDelay = 1000L
        const val width = 512 // TODO: 100
        const val height = 512 // TODO: 100
    }
}
