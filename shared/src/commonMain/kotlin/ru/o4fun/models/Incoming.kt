package ru.o4fun.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.o4fun.enums.Building
import ru.o4fun.enums.Resource
import ru.o4fun.enums.SquadUnit

@Serializable
sealed class Incoming {
    @Transient
    open val x: Int = 0
    @Transient
    open val y: Int = 0

    @Serializable
    @SerialName("unitBuy")
    class UnitBuy(
        override val x: Int = 0,
        override val y: Int = 0,
        val units: Map<SquadUnit, Long> = mapOf()
    ) : Incoming()

    @Serializable
    @SerialName("squadSend")
    class SquadSend(
        override val x: Int = 0,
        override val y: Int = 0,
        val tx: Int = 0,
        val ty: Int = 0,
        val units: Map<SquadUnit, Long> = mapOf()
    ) : Incoming()

    @Serializable
    @SerialName("own")
    class Own(
        override val x: Int = 0,
        override val y: Int = 0
    ) : Incoming()

    @Serializable
    @SerialName("discover")
    class Discover(
        override val x: Int = 0,
        override val y: Int = 0
    ) : Incoming()

    @Serializable
    @SerialName("buildingUpgrade")
    class BuildingUpgrade(
        override val x: Int = 0,
        override val y: Int = 0,
        val building: Building = Building.Barracks
    ) : Incoming()

    @Serializable
    @SerialName("fieldUpgrade")
    class FieldUpgrade(
        override val x: Int = 0,
        override val y: Int = 0,
        val resource: Resource
    ) : Incoming()
}
