package ru.o4fun.interfaces

import ru.o4fun.Resource

interface Player {
    val id: String
    val resources: Map<Resource, Long>
    val owned: Set<Cell>
    val discovered: Set<Cell>
    val parents: Set<Player>
    val children: Set<Player>
}