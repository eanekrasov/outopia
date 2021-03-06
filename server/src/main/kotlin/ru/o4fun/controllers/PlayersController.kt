package ru.o4fun.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import ru.o4fun.exceptions.NotFoundException
import ru.o4fun.services.OutopiaService

@Controller
@RequestMapping("players")
class PlayersController(
    private val outopiaService: OutopiaService
) {
    @GetMapping("")
    fun players(model: Model): String {
        model.addAttribute("players", ReactiveDataDriverContextVariable(Flux.fromIterable(outopiaService.world.allPlayers), 1))
        return "players"
    }

    @GetMapping("{id}")
    fun player(
        @PathVariable("id") id: String,
        model: Model
    ): String {
        model.addAttribute("player", outopiaService.world[id] ?: throw NotFoundException())
        return "player"
    }
}
