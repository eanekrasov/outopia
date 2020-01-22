package ru.o4fun.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.o4fun.properties.AppProperties

@Controller
@RequestMapping("", "/")
class DefaultController(
    private val props: AppProperties
) {
    @GetMapping("", "/")
    fun home(model: Model): String {
        model.addAttribute("height", props.world.height)
        model.addAttribute("width", props.world.width)
        return "home"
    }
}
