package ru.o4fun.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import ru.o4fun.services.OutopiaService

@Controller
class DefaultController(
    outopiaService: OutopiaService
) {
    private val world = outopiaService.world

    @GetMapping("/")
    fun default() = "default"

    @GetMapping("/table")
    fun table(model: Model): String {
        model.addAttribute("height", world.props.height)
        model.addAttribute("width", world.props.width)
        model.addAttribute("world", world)
        return "table"
    }

    @GetMapping("/squads")
    fun squads(model: Model): String {
        model.addAttribute("squads", ReactiveDataDriverContextVariable(Flux.fromIterable(world.allSquads), 1))
        return "squads"
    }

    @GetMapping("/cells/{x}/{y}")
    fun cell(
        @PathVariable("x") x: Int,
        @PathVariable("y") y: Int,
        model: Model
    ): String {
        model.addAttribute("cell", world[x, y])
        return "cell"
    }
}
