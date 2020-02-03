package ru.o4fun.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.o4fun.Resource
import ru.o4fun.collections.BoundSet
import ru.o4fun.collections.ObservableSet
import ru.o4fun.interfaces.Player
import ru.o4fun.interfaces.PlayerSession

@Serializable
class PlayerImpl(override val id: String) : Player {
    @Transient
    val sessions = mutableSetOf<PlayerSession>()
    @Transient
    override val resources = Resource.values().map { it to 0L }.toMap().toMutableMap()
    @Transient
    override val owned = object : ObservableSet<CellImpl>(mutableSetOf()) {
        override fun afterRemove(element: CellImpl) {
            element.owner = null
        }

        override fun beforeAdd(element: CellImpl) {
            element.owner = this@PlayerImpl
        }
    }
    @Transient
    override val discovered: BoundSet<CellImpl, PlayerImpl> = BoundSet(object : ObservableSet<CellImpl>(mutableSetOf()) {}, this, { discoveredBy })
    @Transient
    override val parents: BoundSet<PlayerImpl, PlayerImpl> = BoundSet(owner = this) { children }
    @Transient
    override val children: BoundSet<PlayerImpl, PlayerImpl> = BoundSet(owner = this) { parents }
}