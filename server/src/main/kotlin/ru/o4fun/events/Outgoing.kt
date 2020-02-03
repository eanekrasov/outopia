package ru.o4fun.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.o4fun.Building
import ru.o4fun.Resource
import ru.o4fun.models.PlayerImpl
import ru.o4fun.models.SquadUnit
import ru.o4fun.models.ValueImpl

@Serializable
sealed class Outgoing {
    @Serializable
    @SerialName("unitBought")
    class UnitBought(
        val x: Int = 0,
        val y: Int = 0,
        val units: Map<SquadUnit, Long>
    ) : Outgoing()

    @Serializable
    @SerialName("squadSent")
    class SquadSent(
        val x: Int = 0,
        val y: Int = 0,
        val tx: Int = 0,
        val ty: Int = 0,
        val units: Map<SquadUnit, Long>
    ) : Outgoing()

    @Serializable
    @SerialName("squadDestroyed")
    class SquadDestroyed(
        val x: Int = 0,
        val y: Int = 0,
        val tx: Int = 0,
        val ty: Int = 0,
        val units: Map<SquadUnit, Long>
    ) : Outgoing()

    @Serializable
    @SerialName("discovered")
    class Discovered(
        val x: Int = 0,
        val y: Int = 0,
        val value: Set<ValueImpl>
    ) : Outgoing()

    @Serializable
    @SerialName("owned")
    class Owned(
        val x: Int = 0,
        val y: Int = 0,
        val owner: PlayerImpl?
    ) : Outgoing()

    @Serializable
    @SerialName("resources")
    class Resources(
        val value: Map<Resource, Long> = mapOf()
    ) : Outgoing()

    @Serializable
    @SerialName("buildingUpgraded")
    class BuildingUpgraded(
        val x: Int = 0,
        val y: Int = 0,
        val building: Building = Building.Barracks,
        val level: Int = 0
    ) : Outgoing()

    @Serializable
    @SerialName("fieldUpgraded")
    class FieldUpgraded(
        val x: Int = 0,
        val y: Int = 0,
        val resource: Resource = Resource.GOLD,
        val level: Int = 0
    ) : Outgoing()
}
