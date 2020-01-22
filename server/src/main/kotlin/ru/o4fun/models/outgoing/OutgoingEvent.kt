package ru.o4fun.models.outgoing

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class OutgoingEvent {
    @Serializable
    @SerialName("getValue")
    data class GetValue(
        val id: String = "",
        val x: Int = 0,
        val y: Int = 0,
        val value: String
    ) : OutgoingEvent()

    @Serializable
    @SerialName("reveal")
    data class Reveal(
        val id: String = "",
        val x: Int = 0,
        val y: Int = 0,
        val value: Boolean
    ) : OutgoingEvent()
}
