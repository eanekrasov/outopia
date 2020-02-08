package ru.o4fun.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.reactive.result.view.Rendering
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import ru.o4fun.services.OutopiaService

@Controller
class DefaultController(
    outopiaService: OutopiaService
) {
    private val world = outopiaService.world
    @GetMapping("/")
    fun default() = Rendering.view("default")
        .build()

    @GetMapping("/table")
    fun table() = Rendering.view("table")
        .modelAttribute("height", world.props.height)
        .modelAttribute("width", world.props.width)
        .modelAttribute("world", world)
        .build()

    @GetMapping("/squads")
    fun squads() = Rendering.view("squads")
        .modelAttribute("squads", ReactiveDataDriverContextVariable(Flux.fromIterable(world.allSquads), 1))
        .build()

    @GetMapping("/cells/{x}/{y}")
    fun cell(
        @PathVariable("x") x: Int,
        @PathVariable("y") y: Int
    ) = Rendering.view("cell")
        .modelAttribute("cell", world[x, y])
        .build()
}
