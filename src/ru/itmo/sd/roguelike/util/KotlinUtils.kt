package ru.itmo.sd.roguelike.util

internal fun runIf(value: Boolean, block: () -> Unit) {
    if (value) {
        block()
    }
}
