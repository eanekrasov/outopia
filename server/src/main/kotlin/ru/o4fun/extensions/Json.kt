@file:Suppress("unused")

package ru.o4fun.extensions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.set
import ru.o4fun.models.Incoming
import ru.o4fun.models.Outgoing
import ru.o4fun.models.Value

private val json = Json(configuration = JsonConfiguration.Default.copy(classDiscriminator = "type"))

fun Outgoing.stringify() = json.stringify(Outgoing.serializer(), this)
fun Incoming.stringify() = json.stringify(Incoming.serializer(), this)
fun Value.stringify() = json.stringify(Value.serializer(), this)
fun Set<Value>.stringify() = json.stringify(Value.serializer().set, this)
fun String.parseAsIncoming() = json.parse(Incoming.serializer(), this)
fun String.parseAsOutgoing() = json.parse(Outgoing.serializer(), this)
fun String.parseAsValue() = json.parse(Outgoing.serializer(), this)
