package ru.o4fun.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.o4fun.enums.Resource
import ru.o4fun.interfaces.IValue

@Serializable
sealed class Value : IValue {
    @Serializable
    @SerialName("building")
    class Building(
        override var x: Int,
        override var y: Int,
        override var building: ru.o4fun.enums.Building = ru.o4fun.enums.Building.Barracks,
        override var level: Int = 1
    ) : Value(), IValue.Building {
        val upgradeCost get() = mapOf(Resource.Gold to level * 100L)
    }

    @Serializable
    @SerialName("field")
    class Field(
        override var x: Int,
        override var y: Int,
        override var resource: Resource = Resource.Gold,
        override var level: Int = 1
    ) : Value(), IValue.Field {
        val upgradeCost
            get() = when (resource) {
                Resource.Gold -> mapOf(Resource.Iron to level * 10L, Resource.Copper  to level * 10L)
                Resource.Iron -> mapOf(Resource.Wood to level * 10L)
                Resource.Copper -> mapOf(Resource.Wood to level * 10L)
                Resource.Wood-> mapOf(Resource.Food to level * 10L, Resource.Water to level * 10L)
                Resource.Food -> mapOf(Resource.Gold to level * 10L)
                Resource.Water -> mapOf(Resource.Gold to level * 10L)
            }
    }
}
