package ru.itmo.sd.roguelike.util

import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.modifier.Border
import org.hexworks.zircon.api.modifier.BorderPosition

object StyleUtils {
    val sandStyle: StyleSet =
        StyleSet.empty()
            .withBackgroundColor(TileColor.fromString("#FF9A00"))

    fun defaultTile(): Tile = Tile.newBuilder()
        .withStyleSet(sandStyle)
        .build()

    fun addBorder(tile: Tile, position: BorderPosition): Tile =
        tile.withModifiers(
            Border.newBuilder()
                .withBorderWidth(10)
                .withBorderColor(TileColor.fromString("#54524E"))
                .withBorderPositions(position)
                .build()
        )

    fun addLeftBorder(tile: Tile): Tile = addBorder(tile, BorderPosition.LEFT)

    fun addTopBorder(tile: Tile): Tile = addBorder(tile, BorderPosition.TOP)

    fun addRightBorder(tile: Tile): Tile = addBorder(tile, BorderPosition.RIGHT)

    fun addBottomBorder(tile: Tile): Tile = addBorder(tile, BorderPosition.BOTTOM)
}
