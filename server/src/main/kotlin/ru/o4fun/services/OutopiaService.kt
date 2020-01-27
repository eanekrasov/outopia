package ru.o4fun.services

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.o4fun.events.SchedulerReady
import ru.o4fun.models.Engine

@Service
class OutopiaService(
    private val eventPublisher: ApplicationEventPublisher
) {
    val engine = Engine()

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() = GlobalScope.launch {
        engine.startScheduler(true) {
            eventPublisher.publishEvent(SchedulerReady(it))
        }
    }
}
