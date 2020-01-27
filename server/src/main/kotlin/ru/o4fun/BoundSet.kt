package ru.o4fun

open class BoundSet<T, P>(
    private val parent: MutableSet<T> = mutableSetOf(),
    private val owner: P,
    private val opposite: T.() -> MutableSet<P>
) : MutableSet<T> by parent {
    private var modifying = false
    override fun add(element: T) = modify { parent.add(element) && opposite(element).add(owner) }
    override fun remove(element: T) = modify { parent.remove(element) && opposite(element).remove(owner) }
    private inline fun modify(crossinline fn: () -> Boolean): Boolean {
        if (modifying) return false
        modifying = true
        return fn().also { modifying = false }
    }
}