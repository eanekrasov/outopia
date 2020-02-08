package ru.o4fun.extensions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import ru.o4fun.models.Incoming
import ru.o4fun.models.Outgoing

private val json = Json(configuration = JsonConfiguration.Default.copy(classDiscriminator = "type"))

fun Outgoing.stringify() = json.stringify(Outgoing.serializer(), this)
fun Incoming.stringify() = json.stringify(Incoming.serializer(), this)
fun String.parseIncoming() = json.parse(Incoming.serializer(), this)
fun String.parseOutgoing() = json.parse(Outgoing.serializer(), this)
