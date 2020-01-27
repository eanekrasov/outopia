package ru.o4fun.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import org.springframework.web.socket.TextMessage
import ru.o4fun.Resource
import ru.o4fun.models.*

@Serializable
sealed class Outgoing {
    fun toMsg() = TextMessage(Json(JsonConfiguration.Default).stringify(this))
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
    @SerialName("barracksUpgraded")
    class BarracksUpgraded(
        val x: Int = 0,
        val y: Int = 0,
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
