package ru.itmo.sd.roguelike.view

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.KeyboardEventType
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.internal.fragment.impl.VerticalScrollableList
import ru.itmo.sd.roguelike.controller.GameController
import ru.itmo.sd.roguelike.model.map.GameMap
import ru.itmo.sd.roguelike.util.KeyInput
import ru.itmo.sd.roguelike.util.PlayerHandle

class GameView(size: Size) {
    private val controller = GameController(this)

    private val tileGrid = SwingApplications.startTileGrid(
        AppConfig.newBuilder()
            .withDefaultTileset(CP437TilesetResources.rexPaint20x20())
            .withSize(size)
            .build()
    )

    private lateinit var gameScreen: Screen
    private lateinit var mainMenuScreen: Screen
    private val pauseManuScreen = createPauseMenu()

    fun displayMainMenu() {
        mainMenuScreen = createMainMenuScreen()
        mainMenuScreen.display()
    }

    fun displayGameMap(map: GameMap) {
        gameScreen = createGameScreen(map)
        gameScreen.display()
    }

    private fun createMainMenuScreen(): Screen {
        val screen = Screen.create(tileGrid)
        screen.theme = ColorThemes.solarizedDarkOrange()

        // main menu title
        Components.logArea()
            .withPreferredSize(Size.create(10, 1))
            .withAlignmentWithin(screen, ComponentAlignment.TOP_CENTER)
            .build()
            .apply {
                addInlineText("Main Menu")
                commitInlineElements()
            }.also {
                screen.addComponent(it)
            }

        val mapOptions: List<String> = buildList {
            this += GENERATE_NEW_MAP
            this += controller.getAllMaps().map { "Load $it" }
        }
        // list of existing maps and an option to generate a new one
        val savedMaps = VerticalScrollableList(
            Size.create(20, 15),
            Position.create(5, 5),
            mapOptions,
            onItemActivated = { item, _ ->
                when (item) {
                    GENERATE_NEW_MAP -> controller.generateNewMap(screen.size)
                    else -> {
                        val title = item.substringAfterLast(" ")
                        controller.loadMapByTitle(title)
                    }
                }
            }
        )
        screen.addFragment(savedMaps)

        return screen
    }

    private fun createGameScreen(map: GameMap): Screen {
        val screen = Screen.create(tileGrid)

        screen.size.fetchPositions().forEach { position ->
            val tile = map[position]
            screen.draw(tile, position)
        }

        val player = Player()
        val playerLayer = screen.addLayer(
            Layer.newBuilder()
                .withOffset(Position.create(screen.width - 1, 0))
                .withSize(Size.one())
                .withFiller(player.tile)
                .build()
        )

        screen.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event, _ ->
            if (event.code == KeyCode.ESCAPE) {
                openPauseMenu()
            }
            Processed
        }

        screen.setupPlayerEvents(PlayerHandle(playerLayer))
        return screen
    }

    private fun createPauseMenu(): Screen {
        val screen = Screen.create(tileGrid)
        val toMainMenuButton = Components.button()
            .withAlignmentWithin(screen, ComponentAlignment.TOP_CENTER)
            .withPreferredSize(Size.create(12, 1))
            .withText("To Main Menu")
            .build()

        toMainMenuButton.handleMouseEvents(MouseEventType.MOUSE_RELEASED) { _, _ ->
            controller.goToMainMenu()
            Processed
        }

        screen.addComponent(toMainMenuButton)

        screen.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event, _ ->
            if (event.code == KeyCode.ESCAPE) {
                closePauseMenu()
            }
            Processed
        }

        return screen
    }

    private fun Screen.setupPlayerEvents(player: PlayerHandle) {
        handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event, _ ->
            val keyInput = KeyInput.fromKeyCode(event.code)
            keyInput.handle(this, player)
        }
    }

    private fun openPauseMenu() {
        pauseManuScreen.display()
    }

    private fun closePauseMenu() {
        gameScreen.display()
    }

    companion object {
        private const val GENERATE_NEW_MAP = "Generate New Map"
    }
}
