package ru.o4fun.models

import ru.o4fun.distance
import ru.o4fun.interfaces.Squad

class SquadImpl(
    override val owner: PlayerImpl,
    override val origin: ValueImpl.BarracksImpl,
    override val target: ValueImpl.BarracksImpl,
    override val units: Map<SquadUnit, Long>,
    override var timeout: Int = origin.cell distance target.cell
) : Squad