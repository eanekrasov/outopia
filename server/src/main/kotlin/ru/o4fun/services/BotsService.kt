package ru.o4fun.services

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.o4fun.Resource
import ru.o4fun.cost
import ru.o4fun.events.Incoming
import ru.o4fun.events.Outgoing
import ru.o4fun.events.SchedulerReady
import ru.o4fun.hasResources
import ru.o4fun.hasUnits
import ru.o4fun.interfaces.Cell
import ru.o4fun.interfaces.PlayerSession
import ru.o4fun.models.*
import kotlin.random.Random

@Service
class BotsService(
    outopiaService: OutopiaService
) {
    private final val engine = outopiaService.engine

    @EventListener
    fun onSchedulerReady(e: SchedulerReady) {
//        val red = Bot("red")
//        val blue = Bot("blue")
//         val bots = listOf(red, blue)
        val bots = (1..1000).map { i -> Bot("bot-$i") }

        engine.edit {
             bots.forEach { placeAt { initCell(it) } }
//            placeAt(2, 2) { initCell(red) }
//            placeAt(7, 7) { initCell(blue) }
        }
        val spiral = spiral()
        e.scheduler.schedule(1) {
            val (dx, dy) = spiral.next()
            bots.forEach { bot ->
                bot.session.player.owned.forEach { bot.discover(it.x + dx, it.y + dy) }
            }
            false
        }
        e.scheduler.schedule(1) {
            bots.forEach { bot -> bot.tick() }
            false
        }
    }

    fun Engine.CellScope.initCell(bot: Bot) {
        bot.discover(cell)
        bot.own(cell)
        city()
        Resource.values().forEach {
            field(it, Random.nextInt(10))
        }
    }

    inner class Bot(id: String) {
        val session = engine.addSession(id, BotSession())
        private val foes = mutableMapOf<Pair<Int, Int>, PlayerImpl>()

        fun tick() {
            session.player.owned.forEach { cell ->
                cell.value.forEach {
                    when (it) {
                        is ValueImpl.FieldImpl -> {
                            if (session.player.hasResources(it.upgradeCost)) upgradeField(cell, it.resource)
                        }
                        is ValueImpl.BarracksImpl -> {
                            if (session.player.hasResources(it.upgradeCost)) upgradeBarracks(cell)
                            val units = mapOf(SquadUnit.INFANTRY to 1L, SquadUnit.GUNNER to 1L)
                            if (session.player.hasResources(units.cost)) buyUnits(cell, units)
                            if (it.units.hasUnits()) foes.entries.firstOrNull { (_, owner) -> owner != session.player }?.key?.let { (x, y) ->
                                sendSquad(cell, x, y, it.units)
                            }
                        }
                    }
                }
            }
        }

        private fun onDiscoveredEvent(msg: Outgoing.Discovered) = msg.value.forEach {
            if (it is ValueImpl.BarracksImpl && it.cell.owner != session.player) {
                println("${session.player.id} found ${it.cell.owner?.id} at ${it.cell.x} ${it.cell.y}")
                foes[it.cell.x to it.cell.y] = it.cell.owner!!
            }
        }

        private fun onOwnedEvent(msg: Outgoing.Owned) {
            println("${msg.owner?.id} owned ${msg.x} ${msg.y}")
            foes[msg.x to msg.y] = msg.owner!!
        }

        fun onOutgoingEvent(msg: Outgoing) {
            when (msg) {
                is Outgoing.Owned -> onOwnedEvent(msg)
                is Outgoing.Discovered -> onDiscoveredEvent(msg)
            }
        }

        fun discover(cell: Cell) = session.event(Incoming.Discover(cell.x, cell.y))
        fun discover(x: Int, y: Int) = session.event(Incoming.Discover(x, y))
        fun own(cell: Cell) = session.event(Incoming.Own(cell.x, cell.y))
        fun upgradeField(cell: Cell, resource: Resource) = session.event(Incoming.FieldUpgrade(cell.x, cell.y, resource))
        fun upgradeBarracks(cell: Cell) = session.event(Incoming.BarracksUpgrade(cell.x, cell.y))
        fun buyUnits(cell: Cell, units: Map<SquadUnit, Long>) = session.event(Incoming.UnitBuy(cell.x, cell.y, units))
        fun sendSquad(cell: Cell, x: Int, y: Int, units: Map<SquadUnit, Long>) = session.event(Incoming.SquadSend(cell.x, cell.y, x, y, units.toMap()))

        inner class BotSession : PlayerSession {
            override fun sendMessage(msg: Outgoing) =  onOutgoingEvent(msg)
        }
    }

    companion object {
        fun spiral() = sequence {
            var v = -1
            var row = 0
            while (true) {
                if (++v == row * 8) {
                    row++
                    v = 0
                }
                yield(
                    when (v) {
                        in 0 until row * 2 -> (v - row + 1) to row
                        in row * 2 until row * 4 -> row to -(v - row * 3 + 1)
                        in row * 4 until row * 6 -> -(v - row * 5 + 1) to -row
                        else -> -row to (v - row * 7 + 1)
                    }
                )
            }
        }.iterator()
    }
}