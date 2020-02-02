package ru.o4fun.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.o4fun.Building
import ru.o4fun.Resource
import ru.o4fun.interfaces.Value

@Serializable
sealed class ValueImpl : Value {
    @Serializable
    @SerialName("building")
    class BuildingImpl(
        override var cell: CellImpl,
        override var building: Building = Building.Barracks,
        override var level: Int = 1
    ) : ValueImpl(), Value.Building {
        val upgradeCost get() = mapOf(Resource.GOLD to level * 100L)
    }

    @Serializable
    @SerialName("field")
    class FieldImpl(
        override var cell: CellImpl,
        override var resource: Resource = Resource.GOLD,
        override var level: Int = 1
    ) : ValueImpl(), Value.Field {
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
