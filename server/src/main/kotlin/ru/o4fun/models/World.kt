package ru.o4fun.models

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import ru.o4fun.annotations.DslScope
import ru.o4fun.enums.Building
import ru.o4fun.enums.Resource
import ru.o4fun.extensions.*
import ru.o4fun.interfaces.*
import ru.o4fun.properties.AppProperties
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

class World(
    val props: AppProperties.World
) : CoroutineScope {
    private val job = Job()

    override val coroutineContext = job + Dispatchers.IO

    // region public

    val allPlayers: Collection<IPlayer> get() = players.values

    val allSquads: Set<ISquad> get() = squads

    operator fun get(x: Int, y: Int): ICell = if (x in (0 until props.width) && y in (0 until props.height)) cells[x][y] else throw Exception()

    operator fun get(id: String): IPlayer? = players[id]

    fun edit(block: WorldScope.() -> Unit) = WorldScope().block()

    fun addSession(name: String, session: IPlayerSession): ISessionCallback {
        val player = players.getOrPut(name) { Player(name) }
        player.sessions.add(session)

        return object : ISessionCallback {
            override val player = player

            override fun event(e: Incoming) {
                player.incomingQueue.add(e)
            }

            override fun remove() {
                player.sessions.remove(session)
            }
        }
    }

    fun startScheduler(verbose: Boolean = false, callback: (Scheduler) -> Unit) = launch {
        var time = 0L
        var countExec: Int
        var count: Int
        val pending = mutableListOf<Task>()
        val scheduler = Scheduler(pending)
        val tasks = mutableListOf(
            Task(1) {
                callback(scheduler)
                true
            },
            Task(1) {
                players.values.map {
                    async {
                        it.processIncoming()
                        it.updateResources()
                        it.sendAndBroadcast(it.resourcesEvent(), false)
                    }
                }.awaitAll()
                val processed = squads.filter { it.timeout-- <= 0 }
                squads.removeAll(processed)
                processed.groupBy { it.target }.map {
                    async {
                        it.key.updateSquads(it.value)
                    }
                }.awaitAll()
                false
            }
        )
        while (true) {
            if (props.tickDelay - time > 0) delay(props.tickDelay - time)
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

    val private = Private()

    @Suppress("unused")
    inner class Private {
        val allPlayers: Collection<Player> get() = players.values

        val allSquads: Set<Squad> get() = squads

        operator fun get(x: Int, y: Int): Cell = if (x in (0 until props.width) && y in (0 until props.height)) cells[x][y] else throw Exception()

        operator fun get(id: String): Player? = players[id]

        fun eventsFlow() = eventsChannel.asFlow()
    }

    private val cells: Array<Array<Cell>> = mapXY { (x, y) -> Cell(x, y) }
    private val players = mutableMapOf<String, Player>()
    private val squads = mutableSetOf<Squad>()

    private val eventsChannel = BroadcastChannel<Outgoing>(1)

    private suspend fun Cell.sendAllAndBroadcast(it: Outgoing) {
        sendAll(it, props.parentsLevel)
        eventsChannel.send(it)
    }

    private suspend fun Player.sendAndBroadcast(it: Outgoing, parents: Boolean = true) {
        send(it, if (parents) props.parentsLevel else 0)
        eventsChannel.send(it)
    }

    private suspend fun Cell.updateSquads(list: List<Squad>) {
        list.filter { owner == it.owner }.forEach { addUnits(it.units) }
        val attackers = list.filter { owner != it.owner }
        attackers.forEach { squad ->
            if (props.verbose) println("${squad.units} attacks $units $x $y")
            val attacker = squad.units.toMutableMap()
            attacker.strike(units)
            if (units.hasUnits()) {
                units.strike(attacker)
                if (attacker.hasUnits()) {
                    squads.add(Squad(squad.owner, this, squad.origin, attacker))
                } else {
                    squad.owner.sendAndBroadcast(squad.destroyedEvent())
                }
            } else {
                owner?.owned?.remove(this)
                squad.owner.owned.add(this)
                addUnits(attacker)
                sendAllAndBroadcast(ownedEvent())
            }
        }
    }

    private suspend fun Player.processIncoming() {
        val incoming = incomingQueue.toList()
        incomingQueue.clear()
        incoming.forEach { e ->
            try {
                val cell = cells[e.x][e.y]
                when (e) {
                    is Incoming.SquadSend -> if (cell.owner == this) cell.tryTakeUnits(e.units) {
                        val target = cells[e.tx][e.ty]
                        val squad = Squad(this, cell, target, e.units)
                        if (props.verbose) println("$id sending ${squad.units} from ${e.x} ${e.y} to ${e.tx} ${e.ty} (${squad.timeout})")
                        squads.add(squad)
                        cell.sendAllAndBroadcast(squad.sentEvent())
                    }
                    is Incoming.UnitBuy -> if (cell.owner == this) {
                        if (tryTakeResources(e.units.cost) { cell.addUnits(e.units) }) {
                            cell.sendAllAndBroadcast(e.boughtEvent())
                        }
                    }
                    is Incoming.Discover -> {
                        discovered.add(cell)
                        sendAndBroadcast(cell.discoveredEvent())
                    }
                    is Incoming.Own -> if (cell.owner == this) {
                        owned.add(cell)
                        cell.sendAllAndBroadcast(cell.ownedEvent())
                    }
                    is Incoming.BuildingUpgrade -> if (cell.owner == this) cell.buildingIn(e.building) {
                        if (tryTakeResources(it.upgradeCost) { it.level++ }) {
                            cell.sendAllAndBroadcast(it.upgradedEvent())
                        }
                    }
                    is Incoming.FieldUpgrade -> if (cell.owner == this) cell.fieldIn(e.resource) {
                        if (tryTakeResources(it.upgradeCost) { it.level++ }) {
                            cell.sendAllAndBroadcast(it.upgradedEvent())
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    // region dsl

    @DslScope
    inner class WorldScope {
        private val r = Random()

        fun placeAt(x: Int = randomGaussian(props.width), y: Int = randomGaussian(props.height), block: CellScope.() -> Unit) =
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
        val cell: Cell
    ) {
        fun field(which: Resource, level: Int = 1, callback: (Value.Field) -> Unit = {}) =
            call(callback, { it.resource == which }) { Value.Field(cell.x, cell.y, which, level) }

        fun building(which: Building, level: Int = 1, callback: (Value.Building) -> Unit = {}) =
            call(callback, { it.building == which }) { Value.Building(cell.x, cell.y, which, level) }

        private inline fun <reified T : Value> call(callback: (T) -> Unit, denyIf: (T) -> Boolean = { true }, init: () -> T?) =
            if (!cell.value.any { it is T && denyIf(it) }) init()?.also {
                cell.value.add(it)
                callback(it)
            } else null
    }

    // endregion

}
