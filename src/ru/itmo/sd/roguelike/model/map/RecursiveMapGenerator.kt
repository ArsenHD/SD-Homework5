package ru.itmo.sd.roguelike.model.map

import org.hexworks.zircon.api.data.Size
import kotlin.random.Random

class RecursiveMapGenerator : MapGenerator {
    override fun generate(size: Size): GameMap {
        val map = GameMap(size)
        generate(map)
        return map
    }

    private fun generate(gameMap: GameMap) {
        val width = gameMap.size.width
        val height = gameMap.size.height


        if (width <= 1 || height <= 1) {
            return
        }

        if (Random.nextDouble() < 0.5) {
            val index = (0 until height).average().toInt()
            gameMap.buildWall(index.toRow())
            generate(gameMap.subMap(0 until index, 0 until width))
            generate(gameMap.subMap(index + 1 until height, 0 until width))
        } else {
            val index = (0 until width).average().toInt()
            gameMap.buildWall(index.toColumn())
            generate(gameMap.subMap(0 until height, 0 until index))
            generate(gameMap.subMap(0 until height, index + 1 until width))
        }
    }
}
