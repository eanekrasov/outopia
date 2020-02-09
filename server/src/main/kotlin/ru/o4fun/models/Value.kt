package ru.o4fun.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.o4fun.Resource
import ru.o4fun.interfaces.IValue

@Serializable
sealed class Value : IValue {
    @Serializable
    @SerialName("building")
    class Building(
        override var x: Int,
        override var y: Int,
        override var building: ru.o4fun.Building = ru.o4fun.Building.Barracks,
        override var level: Int = 1
    ) : Value(), IValue.Building {
        val upgradeCost get() = mapOf(Resource.GOLD to level * 100L)
    }

    @Serializable
    @SerialName("field")
    class Field(
        override var x: Int,
        override var y: Int,
        override var resource: Resource = Resource.GOLD,
        override var level: Int = 1
    ) : Value(), IValue.Field {
        val upgradeCost
            get() = when (resource) {
                Resource.GOLD -> mapOf(Resource.IRON to level * 10L, Resource.COPPER to level * 10L)
                Resource.IRON -> mapOf(Resource.WOOD to level * 10L)
                Resource.COPPER -> mapOf(Resource.WOOD to level * 10L)
                Resource.WOOD -> mapOf(Resource.FOOD to level * 10L, Resource.WATER to level * 10L)
                Resource.FOOD -> mapOf(Resource.GOLD to level * 10L)
                Resource.WATER -> mapOf(Resource.GOLD to level * 10L)
            }
    }
}
