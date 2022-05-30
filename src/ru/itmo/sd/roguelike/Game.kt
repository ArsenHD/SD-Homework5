package ru.itmo.sd.roguelike

import org.hexworks.zircon.api.data.Size
import ru.itmo.sd.roguelike.view.GameView

private fun launchGame() {
    val size = Size.create(35, 35)
    val view = GameView(size)
    view.displayMainMenu()
}

fun main() {
    launchGame()
}
