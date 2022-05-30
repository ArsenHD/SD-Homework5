package ru.itmo.sd.roguelike.model.serialize

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import ru.itmo.sd.roguelike.model.map.GameMap
import ru.itmo.sd.roguelike.util.StyleUtils
import ru.itmo.sd.roguelike.util.hasBottomBorder
import ru.itmo.sd.roguelike.util.hasLeftBorder
import ru.itmo.sd.roguelike.util.hasRightBorder
import ru.itmo.sd.roguelike.util.hasTopBorder
import ru.itmo.sd.roguelike.util.runIf

object GameMapSerializer : KSerializer<GameMap> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GameMap") {
        element<Int>("width")
        element<Int>("height")
        element("tileInfo", TileSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: GameMap) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.size.width)
            encodeIntElement(descriptor, 1, value.size.height)
            encodeSerializableElement(descriptor, 2, ListSerializer(ListSerializer(TileSerializer)), value.cells)
        }
    }

    override fun deserialize(decoder: Decoder): GameMap {
        return decoder.decodeStructure(descriptor) {
            var width: Int? = null
            var height: Int? = null
            var cells: List<List<Tile>>? = null
            loop@ while(true) {
                when (decodeElementIndex(descriptor)) {
                    DECODE_DONE -> break@loop
                    0 -> width = decodeIntElement(descriptor, 0)
                    1 -> height = decodeIntElement(descriptor, 1)
                    2 -> cells = decodeSerializableElement(descriptor, 2, ListSerializer(ListSerializer(TileSerializer)))
                }
            }
            val size = Size.create(requireNotNull(width), requireNotNull(height))
            GameMap(size, requireNotNull(cells))
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Tile::class)
object TileSerializer : KSerializer<Tile> {
    override val descriptor: SerialDescriptor = BorderInfo.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Tile) {
        encoder.encodeSerializableValue(BorderInfo.serializer(), value.toBorderInfo())
    }

    override fun deserialize(decoder: Decoder): Tile {
        val (left, top, right, bottom) = decoder.decodeSerializableValue(BorderInfo.serializer())
        var tile = StyleUtils.defaultTile()
        runIf(left) { tile = StyleUtils.addLeftBorder(tile) }
        runIf(top) { tile = StyleUtils.addTopBorder(tile) }
        runIf(right) { tile = StyleUtils.addRightBorder(tile) }
        runIf(bottom) { tile = StyleUtils.addBottomBorder(tile) }
        return tile
    }

    @Serializable
    private data class BorderInfo(
        val left: Boolean,
        val top: Boolean,
        val right: Boolean,
        val bottom: Boolean
    )

    private fun Tile.toBorderInfo(): BorderInfo {
        return BorderInfo(
            left = hasLeftBorder,
            top = hasTopBorder,
            right = hasRightBorder,
            bottom = hasBottomBorder
        )
    }
}
