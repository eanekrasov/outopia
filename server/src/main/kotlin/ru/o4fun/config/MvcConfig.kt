package ru.o4fun.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.o4fun.properties.AppProperties

@Configuration
class MvcConfig(
    private val props: AppProperties
) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file://${props.paths.images}/")
    }

    @Bean
    fun json() = Json(
        configuration = JsonConfiguration.Default.copy(classDiscriminator = "type")
    )
}