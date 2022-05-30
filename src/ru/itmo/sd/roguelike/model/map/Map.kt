package ru.itmo.sd.roguelike.model.map

import kotlinx.serialization.Serializable
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.modifier.BorderPosition
import ru.itmo.sd.roguelike.util.exception.IllegalMapCoordinatesException
import ru.itmo.sd.roguelike.model.serialize.GameMapSerializer
import ru.itmo.sd.roguelike.util.StyleUtils

@Serializable(with = GameMapSerializer::class)
class GameMap {
    val size: Size
    internal val cells: List<MutableList<Tile>>

    constructor(size: Size) {
        this.size = size
        cells = List(size.height) {
            MutableList(size.width) { StyleUtils.defaultTile() }
        }
    }

    private constructor(gameMap: GameMap, rows: IntRange, columns: IntRange) {
        size = Size.create(columns.length, rows.length)
        cells = rows.map { row ->
            gameMap.cells[row].subList(columns.first, columns.last + 1)
        }
    }

    internal constructor(size: Size, cells: List<List<Tile>>) {
        this.size = size
        this.cells = cells.map { it.toMutableList() }
    }

    /**
     * @param x - horizontal axis coordinate
     * @param y - vertical axis coordinate
     *
     * @return cell at the given coordinates on the map
     */
    fun get(x: Int, y: Int): Tile {
        checkCoordinates(x, y)
        return cells[y][x]
    }

    /**
     * @param x - horizontal axis coordinate
     * @param y - vertical axis coordinate
     *
     * put the given [tile] at the given coordinates on the map
     */
    fun set(x: Int, y: Int, tile: Tile) {
        checkCoordinates(x, y)
        cells[y][x] = tile
    }

    /**
     * @return tile at the given [position] on the map
     */
    operator fun get(position: Position): Tile = get(position.x, position.y)

    /**
     * put the given [tile] at the given [position] on the map
     */
    operator fun set(position: Position, tile: Tile) = set(position.x, position.y, tile)

    /**
     * Build horizontal wall at [row] in the given [range]
     */
    fun buildWall(row: Row) {
        val range = 0 until size.width
        val middle = range.average().toInt()
        val doorIndex = range.random().let { if (it == middle) it + 1 else it }
        for (column in range) {
            if (column == doorIndex) continue
            val cell = cells[row.index][column]
            cells[row.index][column] = StyleUtils.addBorder(cell, BorderPosition.BOTTOM)
        }
    }

    /**
     * Build vertical wall at [column] in the given [range]
     */
    fun buildWall(column: Column) {
        val range = 0 until size.height
        val middle = range.average().toInt()
        val doorIndex = range.random().let { if (it == middle) it + 1 else it }
        for (row in range) {
            if (row == doorIndex) continue
            val cell = cells[row][column.index]
            cells[row][column.index] = StyleUtils.addBorder(cell, BorderPosition.RIGHT)
        }
    }

    private fun checkCoordinates(x: Int, y: Int) {
        if (x !in 0 until size.width || y !in 0 until size.height) {
            throw IllegalMapCoordinatesException(x, y, size)
        }
    }

    fun subMap(rows: IntRange, columns: IntRange): GameMap =
        GameMap(this, rows, columns)

    @JvmInline
    value class Row(val index: Int)

    @JvmInline
    value class Column(val index: Int)

    companion object {
        private val IntRange.length: Int
            get() = last - first + 1
    }
}

internal fun Int.toRow() = GameMap.Row(this)
internal fun Int.toColumn() = GameMap.Column(this)
