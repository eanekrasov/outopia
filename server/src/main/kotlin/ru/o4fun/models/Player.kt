package ru.o4fun.models

import ru.o4fun.enums.Resource
import ru.o4fun.collections.BoundSet
import ru.o4fun.collections.ObservableSet
import ru.o4fun.interfaces.IPlayer
import ru.o4fun.interfaces.IPlayerSession

class Player(
    override var name: String
) : IPlayer {
    var id: Long? = null
    val incomingQueue = mutableListOf<Incoming>()
    val sessions = mutableSetOf<IPlayerSession>()
    override val resources = Resource.values().map { it to 0L }.toMap().toMutableMap()
    override val owned = object : ObservableSet<Cell>(mutableSetOf()) {
        override fun afterRemove(element: Cell) {
            element.owner = null
        }

        override fun beforeAdd(element: Cell) {
            element.owner = this@Player
        }
    }
    override val discovered: BoundSet<Cell, Player> =
        BoundSet(owner = this) { discoveredBy }
    override val parents: BoundSet<Player, Player> =
        BoundSet(owner = this) { children }
    override val children: BoundSet<Player, Player> =
        BoundSet(owner = this) { parents }
}