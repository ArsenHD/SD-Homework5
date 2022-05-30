package ru.itmo.sd.roguelike.util.exception

import org.hexworks.zircon.api.data.Size

class IllegalMapCoordinatesException(x: Int, y: Int, mapSize: Size) :
    Exception("Coordinates ($x, $y) are out of bound for map size (${mapSize.width}, ${mapSize.height}")
