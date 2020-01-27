package ru.o4fun.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import ru.o4fun.exceptions.NotFoundException
import ru.o4fun.map
import ru.o4fun.models.Engine
import ru.o4fun.services.OutopiaService

@Controller
class DefaultController(
    outopiaService: OutopiaService
) {
    private val engine = outopiaService.engine
    @GetMapping("/")
    fun table(model: Model): String {
        model.addAttribute("height", Engine.height)
        model.addAttribute("width", Engine.width)
        model.addAttribute("world", engine)
        return "table"
    }

    @GetMapping("/players")
    fun players(model: Model): String {
        model.addAttribute("players", engine.allPlayers)
        return "players"
    }

    @GetMapping("/squads")
    fun squads(model: Model): String {
        model.addAttribute("squads", engine.allSquads)
        return "squads"
    }

    @GetMapping("/players/{id}")
    fun player(
        @PathVariable("id") id: String,
        model: Model
    ): String {
        val player = engine[id] ?: throw NotFoundException()
        model.addAttribute("player", player)
        return "player"
    }

    @GetMapping("/cells/{x}/{y}")
    fun cell(
        @PathVariable("x") x: Int,
        @PathVariable("y") y: Int,
        model: Model
    ): String {
        model.addAttribute("cell", engine[x, y])
        return "cell"
    }
}
