package ru.o4fun.controllers.api.v1

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.o4fun.extensions.stringify
import ru.o4fun.services.OutopiaService

@RestController
@RequestMapping("/api/v1/outopia")
class OutopiaApi(
    outopiaService: OutopiaService
) {
    val world = outopiaService.world

    @GetMapping("value")
    internal fun value(
        @RequestParam x: Int,
        @RequestParam y: Int
    ) = world.private[x, y].value.stringify()

    @GetMapping(path = ["/events"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun owned() = world.private.eventsFlow()
}