package ru.o4fun.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.o4fun.BoundSet
import ru.o4fun.ObservableSet
import ru.o4fun.interfaces.Cell
import ru.o4fun.interfaces.CellType
import kotlin.random.Random

@Serializable
class CellImpl(
    override val x: Int = 0,
    override val y: Int = 0,
    // never set it by hand, use Player.owned instead
    override var owner: PlayerImpl? = null
) : Cell {
    override val units: MutableMap<SquadUnit, Long> = mutableMapOf()
    @Transient
    override var type: CellType = if (Random.nextFloat() > 0.9) CellType.FOREST else CellType.DEFAULT
    @Transient
    override val value = object : ObservableSet<ValueImpl>() {}
    @Transient
    override val discoveredBy: BoundSet<PlayerImpl, CellImpl> = BoundSet(mutableSetOf(), this, { discovered })
}