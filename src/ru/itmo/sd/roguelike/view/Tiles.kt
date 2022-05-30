package ru.itmo.sd.roguelike.view

import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Tile

abstract class GameCharacter(val tile: Tile): Tile by tile {
    abstract val maxHealth: Double
    abstract var health: Double
}

class Player : GameCharacter(TileUtils.playerTile) {
    override val maxHealth: Double = 100.0
    override var health: Double = maxHealth
}

internal object TileUtils {
    val playerTile: Tile =
        Tile.empty()
            .withBackgroundColor(TileColor.fromString("#001EFF"))

    @Suppress("unused")
    val enemyTile: Tile =
        Tile.empty()
            .withBackgroundColor(TileColor.fromString("#FF0000"))
}
