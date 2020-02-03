package ru.o4fun.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.reactive.result.view.Rendering
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import ru.o4fun.exceptions.NotFoundException
import ru.o4fun.models.Engine
import ru.o4fun.services.OutopiaService

@Controller
class DefaultController(
    outopiaService: OutopiaService
) {
    private val engine = outopiaService.engine
    @GetMapping("/")
    fun table() = Rendering.view("table")
        .modelAttribute("height", Engine.height)
        .modelAttribute("width", Engine.width)
        .modelAttribute("world", engine)
        .build()

    @GetMapping("/players")
    fun players() = Rendering.view("players")
        .modelAttribute("players", ReactiveDataDriverContextVariable(Flux.fromIterable(engine.allPlayers), 1))
        .build()

    @GetMapping("/squads")
    fun squads() = Rendering.view("squads")
        .modelAttribute("squads", ReactiveDataDriverContextVariable(Flux.fromIterable(engine.allSquads), 1))
        .build()

    @GetMapping("/players/{id}")
    fun player(
        @PathVariable("id") id: String
    ) = Rendering.view("player")
        .modelAttribute("player", engine[id] ?: throw NotFoundException())
        .build()

    @GetMapping("/cells/{x}/{y}")
    fun cell(
        @PathVariable("x") x: Int,
        @PathVariable("y") y: Int
    ) = Rendering.view("cell")
        .modelAttribute("cell", engine[x, y])
        .build()
}
