package ru.o4fun.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.Rendering
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import java.time.Duration

data class Message(val payload: String)

@Controller
class FluxController {
    @GetMapping("flux")
    fun index() = Rendering.view("index")
        .modelAttribute("message", Message("hello, la-la!"))
        .modelAttribute(
            "messages", ReactiveDataDriverContextVariable(
                Flux.zip(
                    Flux.interval(Duration.ofSeconds(1)),
                    Flux.just(
                        Message("and one"),
                        Message("and two"),
                        Message("and three"),
                        Message("and four!")
                    )
                ).map { it.t2 },
                1
            )
        )
        .build()
}