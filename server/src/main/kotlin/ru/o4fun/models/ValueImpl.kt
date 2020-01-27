package ru.o4fun.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.o4fun.Resource
import ru.o4fun.interfaces.Value

@Serializable
sealed class ValueImpl : Value {
    @Transient
    override lateinit var cell: CellImpl
        protected set

    @Serializable
    @SerialName("barracks")
    class BarracksImpl(
        override var cell: CellImpl,
        override var level: Int = 1,
        override val units: MutableMap<SquadUnit, Long> = mutableMapOf()
    ) : ValueImpl(), Value.Barracks {
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
