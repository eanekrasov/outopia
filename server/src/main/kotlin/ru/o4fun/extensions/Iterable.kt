package ru.o4fun.extensions

fun <T> Iterable<T>.flatWalk(times: Int, transform: (T) -> Iterable<T>, action: T.() -> Unit) {
    forEach {
        it.action()
        if (times > 0) transform(it).flatWalk(times - 1, transform, action) else it.action()
    }
}
