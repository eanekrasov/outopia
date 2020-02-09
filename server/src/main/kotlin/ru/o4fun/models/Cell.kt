package ru.o4fun.models

import ru.o4fun.enums.CellType
import ru.o4fun.collections.BoundSet
import ru.o4fun.collections.ObservableSet
import ru.o4fun.enums.SquadUnit
import ru.o4fun.interfaces.ICell
import kotlin.random.Random

class Cell(
    override val x: Int = 0,
    override val y: Int = 0,
    // never set it by hand, use Player.owned instead
    override var owner: Player? = null
) : ICell {
    override val units: MutableMap<SquadUnit, Long> = mutableMapOf()
    override var type: CellType = if (Random.nextFloat() > 0.9) CellType.Forest else CellType.Default
    override val value = object : ObservableSet<Value>() {}
    override val discoveredBy: BoundSet<Player, Cell> = BoundSet(mutableSetOf(), this, { discovered })
}
