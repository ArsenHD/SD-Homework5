package ru.itmo.sd.roguelike.util

import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.LayerHandle
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.modifier.BorderPosition
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.Pass
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.api.uievent.UIEventResponse

abstract class KeyInput(val keyCode: KeyCode) {
    fun handle(grid: TileGrid, player: PlayerHandle): UIEventResponse {
        if (!canHandle(grid, player)) {
            return Pass
        }
        doHandle(grid, player)
        return Processed
    }

    protected abstract fun canHandle(grid: TileGrid, player: PlayerHandle): Boolean

    protected abstract fun doHandle(grid: TileGrid, player: PlayerHandle)

    protected fun getPlayerTile(grid: TileGrid, player: PlayerHandle): Maybe<Tile> =
        getTileByPosition(grid, player.position)

    protected fun getTileByPosition(grid: TileGrid, position: Position): Maybe<Tile> =
        Maybe.ofNullable(grid.tiles[position])


    companion object {
        fun fromKeyCode(keyCode: KeyCode): KeyInput =
            when (keyCode) {
                KeyCode.LEFT -> LeftKeyInput
                KeyCode.UP -> UpKeyInput
                KeyCode.RIGHT -> RightKeyInput
                KeyCode.DOWN -> DownKeyInput
                else -> error("Unsupported key code $keyCode")
            }
    }
}

object LeftKeyInput : KeyInput(KeyCode.LEFT) {
    override fun canHandle(grid: TileGrid, player: PlayerHandle): Boolean {
        val position = player.position
        if (position.x == 0) {
            return false
        }

        val playerTile = getPlayerTile(grid, player)
        val leftTile = getTileByPosition(grid, player.position.safeWithRelativeX(grid, -1))

        return when {
            playerTile.map(Tile::hasLeftBorder).orElse(false) -> false
            leftTile.map(Tile::hasRightBorder).orElse(false) -> false
            else -> true
        }
    }

    override fun doHandle(grid: TileGrid, player: PlayerHandle) {
        player.handle.moveLeftBy(1)
    }
}

object UpKeyInput : KeyInput(KeyCode.UP) {
    override fun canHandle(grid: TileGrid, player: PlayerHandle): Boolean {
        val position = player.position
        if (position.y == 0) {
            return false
        }

        val playerTile = getPlayerTile(grid, player)
        val topTile = getTileByPosition(grid, player.position.safeWithRelativeY(grid, -1))

        return when {
            playerTile.map(Tile::hasTopBorder).orElse(false) -> false
            topTile.map(Tile::hasBottomBorder).orElse(false) -> false
            else -> true
        }
    }

    override fun doHandle(grid: TileGrid, player: PlayerHandle) {
        player.handle.moveUpBy(1)
    }
}

object RightKeyInput : KeyInput(KeyCode.RIGHT) {
    override fun canHandle(grid: TileGrid, player: PlayerHandle): Boolean {
        val position = player.position
        if (position.x == grid.width - 1) {
            return false
        }

        val playerTile = getPlayerTile(grid, player)
        val rightTile = getTileByPosition(grid, player.position.safeWithRelativeX(grid, 1))

        return when {
            playerTile.map(Tile::hasRightBorder).orElse(false) -> false
            rightTile.map(Tile::hasLeftBorder).orElse(false) -> false
            else -> true
        }
    }

    override fun doHandle(grid: TileGrid, player: PlayerHandle) {
        player.handle.moveRightBy(1)
    }
}

object DownKeyInput : KeyInput(KeyCode.DOWN) {
    override fun canHandle(grid: TileGrid, player: PlayerHandle): Boolean {
        val position = player.position
        if (position.y == grid.height - 1) {
            return false
        }

        val playerTile = getPlayerTile(grid, player)
        val bottomTile = getTileByPosition(grid, player.position.safeWithRelativeY(grid, 1))

        return when {
            playerTile.map(Tile::hasBottomBorder).orElse(false) -> false
            bottomTile.map(Tile::hasTopBorder).orElse(false) -> false
            else -> true
        }
    }

    override fun doHandle(grid: TileGrid, player: PlayerHandle) {
        player.handle.moveDownBy(1)
    }
}

@JvmInline
value class PlayerHandle(val handle: LayerHandle) {
    val position: Position
        get() = handle.position
}

internal fun Tile.hasBorderAt(position: BorderPosition): Boolean =
    fetchBorderData().any { border ->
        position in border.borderPositions
    }

internal val Tile.hasLeftBorder: Boolean
    get() = hasBorderAt(BorderPosition.LEFT)

internal val Tile.hasTopBorder: Boolean
    get() = hasBorderAt(BorderPosition.TOP)

internal val Tile.hasRightBorder: Boolean
    get() = hasBorderAt(BorderPosition.RIGHT)

internal val Tile.hasBottomBorder: Boolean
    get() = hasBorderAt(BorderPosition.BOTTOM)

internal fun Position.safeWithRelativeX(grid: TileGrid, delta: Int): Position =
    when {
        x == 0 && delta < 0 -> this
        x == grid.width - 1 && delta > 0 -> this
        else -> withRelativeX(delta)
    }

internal fun Position.safeWithRelativeY(grid: TileGrid, delta: Int): Position =
    when {
        y == 0 && delta < 0 -> this
        y == grid.height - 1 && delta > 0 -> this
        else -> withRelativeY(delta)
    }
