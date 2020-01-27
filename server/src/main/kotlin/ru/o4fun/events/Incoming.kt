package ru.o4fun.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.o4fun.Resource
import ru.o4fun.models.SquadUnit

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
    @SerialName("barracksUpgrade")
    class BarracksUpgrade(
        override val x: Int = 0,
        override val y: Int = 0
    ) : Incoming()

    @Serializable
    @SerialName("fieldUpgrade")
    class FieldUpgrade(
        override val x: Int = 0,
        override val y: Int = 0,
        val resource: Resource
    ) : Incoming()
}