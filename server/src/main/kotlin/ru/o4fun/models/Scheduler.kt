package ru.o4fun.models

class Scheduler(private val list: MutableList<Task>) {
    fun schedule(timeout: Int, callback: suspend Scheduler.() -> Boolean) {
        list.add(Task(timeout, callback))
    }
}