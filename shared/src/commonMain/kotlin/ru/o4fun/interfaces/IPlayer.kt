package ru.o4fun.interfaces

import ru.o4fun.Resource

interface IPlayer {
    val name: String
    val resources: Map<Resource, Long>
    val owned: Set<ICell>
    val discovered: Set<ICell>
    val parents: Set<IPlayer>
    val children: Set<IPlayer>
}