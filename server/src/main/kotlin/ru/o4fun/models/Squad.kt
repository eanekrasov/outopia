package ru.o4fun.models

import ru.o4fun.extensions.distance
import ru.o4fun.interfaces.ISquad

class Squad(
    override val owner: Player,
    override val origin: Cell,
    override val target: Cell,
    override val units: Map<SquadUnit, Long>,
    override var timeout: Int = origin distance target
) : ISquad