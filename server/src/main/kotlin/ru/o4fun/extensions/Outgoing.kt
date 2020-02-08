package ru.o4fun.extensions

import ru.o4fun.models.*

fun SquadImpl.destroyedEvent() = Outgoing.SquadDestroyed(origin.x, origin.y, target.x, target.y, units)
fun SquadImpl.sentEvent() = Outgoing.SquadSent(origin.x, origin.y, target.x, target.y, units)
fun ValueImpl.BuildingImpl.upgradedEvent() = Outgoing.BuildingUpgraded(cell.x, cell.y, building, level)
fun ValueImpl.FieldImpl.upgradedEvent() = Outgoing.FieldUpgraded(cell.x, cell.y, resource, level)
fun CellImpl.ownedEvent() = Outgoing.Owned(x, y, owner?.id)
fun CellImpl.discoveredEvent() = Outgoing.Discovered(x, y, value)
fun Incoming.UnitBuy.boughtEvent() = Outgoing.UnitBought(x, y, units)
fun PlayerImpl.resourcesEvent() = Outgoing.Resources(resources)