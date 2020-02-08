package ru.o4fun.models

import ru.o4fun.extensions.distance
import ru.o4fun.interfaces.ISquad

class SquadImpl(
    override val owner: PlayerImpl,
    override val origin: CellImpl,
    override val target: CellImpl,
    override val units: Map<SquadUnit, Long>,
    override var timeout: Int = origin distance target
) : ISquad