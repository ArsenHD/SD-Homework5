package ru.itmo.sd.roguelike.model

import kotlinx.serialization.json.Json
import ru.itmo.sd.roguelike.model.map.GameMap
import ru.itmo.sd.roguelike.view.GameView
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * This class represents information about the current
 * state of the game. At this point the only useful information
 * is the currently selected [GameMap], but there may be
 * more information later as the game development progresses.
 */
class GameModel(private val view: GameView) {
    /**
     * Currently used map. Is null when a player is in the main menu.
     */
    private var gameMap: GameMap? = null

    fun selectNewMap(newMap: GameMap) {
        gameMap = newMap
        view.displayGameMap(newMap)
    }

    fun discardMap() {
        gameMap = null
        view.displayMainMenu()
    }

    fun saveMap(map: GameMap) {
        val savedMapsDir = File(SAVED_MAPS_DIRECTORY)
        val mapIndex = savedMapsDir.listFiles()?.size ?: 0
        val saveFile = File(savedMapsDir, "Map$mapIndex$JSON_SUFFIX")
        if (!saveFile.createNewFile()) {
            throw IOException("Failed to save map #$mapIndex")
        }
        saveFile.writeText(Json.encodeToString(GameMap.serializer(), map))
    }

    fun loadMap(title: String): GameMap {
        val saveFile = File(SAVED_MAPS_DIRECTORY, title + JSON_SUFFIX)
        if (!saveFile.exists()) {
            throw FileNotFoundException("Failed to load")
        }
        return Json.decodeFromString(GameMap.serializer(), saveFile.readText())
    }

    fun getAllMapTitles(): List<String> {
        return File(SAVED_MAPS_DIRECTORY)
            .listFiles()
            ?.map { it.name }
            ?.map { it.substringBefore(JSON_SUFFIX) }
            ?: emptyList()
    }

    companion object {
        private const val JSON_SUFFIX = ".json"
        private const val SAVED_MAPS_DIRECTORY = "resources/maps"
    }
}
