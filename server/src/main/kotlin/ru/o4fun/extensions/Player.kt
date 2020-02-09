package ru.o4fun.extensions

import ru.o4fun.Resource
import ru.o4fun.interfaces.IPlayer
import ru.o4fun.models.Outgoing
import ru.o4fun.models.Player
import ru.o4fun.models.Value

fun Player.send(e: Outgoing, parentsLevel: Int) {
    sessions.forEach {
        try {
            it.sendMessage(e)
        } catch (e: Exception) {
        }
    }
    if (parentsLevel > 0) parents.flatWalk(parentsLevel - 1, { it.parents }) {
        sessions.forEach {
            try {
                it.sendMessage(e)
            } catch (e: Exception) {
            }
        }
    }
}

fun IPlayer.hasResources(which: Map<Resource, Long>) =
    which.all { resources.getOrDefault(it.key, 0) >= it.value }

fun Player.tryTakeResources(which: Map<Resource, Long>, callback: () -> Unit) = try {
    if (hasResources(which)) {
        callback()
        minusResources(which)
        true
    } else false
} catch (e: Exception) {
    false
}

fun Player.minusResources(which: Map<Resource, Long>) =
    which.forEach { resources[it.key] = resources.getOrDefault(it.key, 0) - it.value }

fun Player.updateResources() = owned.forEach { cell ->
    cell.value.forEach {
        when (it) {
            is Value.Field -> {
                resources[it.resource] = resources.getOrDefault(it.resource, 0) + it.level
            }
        }
    }
}
