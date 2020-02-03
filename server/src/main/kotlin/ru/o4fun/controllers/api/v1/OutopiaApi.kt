package ru.o4fun.controllers.api.v1

import kotlinx.serialization.json.Json
import kotlinx.serialization.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.o4fun.models.ValueImpl
import ru.o4fun.services.OutopiaService

@RestController
@RequestMapping("/api/v1/outopia")
class OutopiaApi(
    outopiaService: OutopiaService
) {
    val world = outopiaService.world.private

    @GetMapping("value")
    internal fun value(
        @RequestParam x: Int,
        @RequestParam y: Int
    ) = Json.stringify(ValueImpl.serializer().set, world[x, y].value)
}