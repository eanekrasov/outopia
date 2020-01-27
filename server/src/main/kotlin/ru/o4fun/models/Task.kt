package ru.o4fun.models

class Task(
    val timeout: Int,
    val callback: suspend Scheduler.() -> Boolean
) {
    var ticks = 0
}