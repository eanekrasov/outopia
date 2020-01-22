package ru.o4fun.models.incoming

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class IncomingEvent {
    @Serializable
    @SerialName("reveal")
    data class Reveal(
        val x: Int = 0,
        val y: Int = 0
    ) : IncomingEvent()

    @Serializable
    @SerialName("getValue")
    data class GetValue(
        val x: Int = 0,
        val y: Int = 0
    ) : IncomingEvent()
}
