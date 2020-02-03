package ru.o4fun.collections

abstract class ObservableSet<T>(private val parent: MutableSet<T> = mutableSetOf()) : MutableSet<T> by parent {
    protected open fun beforeAdd(element: T) {}
    override fun add(element: T) = parent.add(element.also { beforeAdd(element) }).also { afterAdd(element) }
    protected open fun afterAdd(element: T) {}

    protected open fun beforeRemove(element: T) {}
    override fun remove(element: T) = parent.remove(element.also { beforeRemove(element) }).also { afterRemove(element) }
    protected open fun afterRemove(element: T) {}
}