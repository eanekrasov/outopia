package ru.o4fun.controllers.api.v1

import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.o4fun.services.OutopiaService

@RestController
@RequestMapping("/api/v1/outopia")
class OutopiaApi(
    private val outopiaService: OutopiaService,
    private val json: Json
) {
    @GetMapping("getValue")
    internal fun getValue(
        @RequestParam id: String,
        @RequestParam x: Int,
        @RequestParam y: Int
    ) = json.stringify(outopiaService.getValue(id, x, y))

    @GetMapping("reveal")
    internal fun reveal(
        @RequestParam id: String,
        @RequestParam x: Int,
        @RequestParam y: Int
    ) = json.stringify(outopiaService.reveal(id, x, y))
}