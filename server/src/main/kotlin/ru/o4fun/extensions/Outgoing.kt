package ru.o4fun.extensions

import ru.o4fun.models.Cell
import ru.o4fun.models.Player
import ru.o4fun.models.Squad
import ru.o4fun.models.*

fun Squad.destroyedEvent() = Outgoing.SquadDestroyed(origin.x, origin.y, target.x, target.y, units)
fun Squad.sentEvent() = Outgoing.SquadSent(origin.x, origin.y, target.x, target.y, units)
fun Value.Building.upgradedEvent() = Outgoing.BuildingUpgraded(x, y, building, level)
fun Value.Field.upgradedEvent() = Outgoing.FieldUpgraded(x, y, resource, level)
fun Cell.ownedEvent() = Outgoing.Owned(x, y, owner)
fun Cell.discoveredEvent() = Outgoing.Discovered(x, y, value)
fun Incoming.UnitBuy.boughtEvent() = Outgoing.UnitBought(x, y, units)
fun Player.resourcesEvent() = Outgoing.Resources(resources)