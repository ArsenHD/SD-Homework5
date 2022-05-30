package ru.itmo.sd.roguelike.model.map

import org.hexworks.zircon.api.data.Size

interface MapGenerator {
    fun generate(size: Size): GameMap
}