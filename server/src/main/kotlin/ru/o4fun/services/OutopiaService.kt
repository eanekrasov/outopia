package ru.o4fun.services

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.o4fun.events.SchedulerReady
import ru.o4fun.models.World
import ru.o4fun.properties.AppProperties

@Service
class OutopiaService(
    private val eventPublisher: ApplicationEventPublisher,
    props: AppProperties
) {
    val world = World(props.world)

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() = world.startScheduler(true) {
        eventPublisher.publishEvent(SchedulerReady(it))
    }
}
