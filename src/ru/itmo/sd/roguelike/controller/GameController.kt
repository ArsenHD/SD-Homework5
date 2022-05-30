package ru.itmo.sd.roguelike.controller

import org.hexworks.zircon.api.data.Size
import ru.itmo.sd.roguelike.model.map.MapGenerator
import ru.itmo.sd.roguelike.model.map.RecursiveMapGenerator
import ru.itmo.sd.roguelike.model.GameModel
import ru.itmo.sd.roguelike.view.GameView

class GameController(view: GameView) {
    private val model = GameModel(view)

    private val mapGenerator: MapGenerator = RecursiveMapGenerator()

    fun generateNewMap(size: Size) {
        val newMap = mapGenerator.generate(size)
        model.saveMap(newMap)
        model.selectNewMap(newMap)
    }

    fun loadMapByTitle(title: String) {
        val map = model.loadMap(title)
        model.selectNewMap(map)
    }

    fun getAllMaps(): List<String> {
        return model.getAllMapTitles()
    }

    fun goToMainMenu() {
        model.discardMap()
    }
}
