package ru.o4fun.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.reactive.result.view.Rendering
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import ru.o4fun.entities.CellEntity
import ru.o4fun.repositories.CellRepository
import java.time.Duration

@Controller
class FluxController(
    private val cellRepo: CellRepository
) {
    @GetMapping("flux")
    fun index() = Rendering.view("index").apply {
        modelAttribute("message", CellEntity("hello, la-la!"))
        modelAttribute("messages", ReactiveDataDriverContextVariable(Flux.zip(
            Flux.interval(Duration.ofSeconds(1)),
            cellRepo.findAll()
        ).map { it.t2 }, 1))
    }.build()


    @GetMapping("flux/findAll")
    @ResponseBody
    fun findAll() = cellRepo.findAll()

    @GetMapping("flux/save")
    @ResponseBody
    fun save() = cellRepo.save(CellEntity("test"))
}