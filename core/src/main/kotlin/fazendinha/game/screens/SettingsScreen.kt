package fazendinha.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import fazendinha.game.FazendinhaGame
import fazendinha.game.Settings

class SettingsScreen(private val game: FazendinhaGame) : ScreenAdapter() {
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var font: BitmapFont
    private lateinit var fontBig: BitmapFont

    private var selectedIndex = 0
    private var initialized = false

    override fun show() {
        try {
            camera = OrthographicCamera()
            viewport = FitViewport(FazendinhaGame.V_WIDTH, FazendinhaGame.V_HEIGHT, camera)
            font = BitmapFont()
            font.color = Color.WHITE
            font.data.setScale(0.9f)
            fontBig = BitmapFont()
            fontBig.color = Color.WHITE
            fontBig.data.setScale(1.4f)

            setupInput()
            initialized = true
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Settings init error", e)
        }
    }

    private fun setupInput() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                handleTouch(v.x, v.y)
                return true
            }

            override fun keyDown(keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.UP, Input.Keys.W -> {
                        selectedIndex = (selectedIndex - 1 + 4) % 4
                    }
                    Input.Keys.DOWN, Input.Keys.S -> {
                        selectedIndex = (selectedIndex + 1) % 4
                    }
                    Input.Keys.LEFT, Input.Keys.A -> changeSetting(-1)
                    Input.Keys.RIGHT, Input.Keys.D -> changeSetting(1)
                    Input.Keys.ENTER, Input.Keys.SPACE -> {
                        if (selectedIndex == 3) onBack()
                        else changeSetting(1)
                    }
                    Input.Keys.ESCAPE -> onBack()
                }
                return true
            }
        }
    }

    private fun handleTouch(x: Float, y: Float) {
        val centerX = FazendinhaGame.V_WIDTH / 2f
        val startY = 150f
        val spacing = 35f

        if (x in centerX - 100f..centerX + 100f) {
            for (i in 0..3) {
                val itemY = startY - i * spacing
                if (y in itemY - 12f..itemY + 12f) {
                    if (i == 3) onBack()
                    else {
                        selectedIndex = i
                        if (x < centerX) changeSetting(-1) else changeSetting(1)
                    }
                    return
                }
            }
        }
    }

    private fun changeSetting(dir: Int) {
        when (selectedIndex) {
            0 -> Settings.soundVolume = (Settings.soundVolume + dir * 10).coerceIn(0, 100)
            1 -> Settings.musicVolume = (Settings.musicVolume + dir * 10).coerceIn(0, 100)
            2 -> Settings.showFps = !Settings.showFps
        }
        Settings.save()
    }

    private fun onBack() {
        game.setScreen(MenuScreen(game))
    }

    override fun render(delta: Float) {
        if (!initialized) return

        try {
            Gdx.gl.glClearColor(0.08f, 0.12f, 0.2f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            renderUI()
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Settings render error", e)
        }
    }

    private fun renderUI() {
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()

        fontBig.setColor(Color.WHITE)
        fontBig.draw(b, "OPCOES", FazendinhaGame.V_WIDTH / 2 - 45f, FazendinhaGame.V_HEIGHT - 30f)

        val centerX = FazendinhaGame.V_WIDTH / 2f
        val startY = 160f
        val spacing = 35f

        val labels = arrayOf("Efeitos", "Musica", "Mostrar FPS", "Voltar")
        val values = arrayOf(
            "${Settings.soundVolume}%",
            "${Settings.musicVolume}%",
            if (Settings.showFps) "ON" else "OFF",
            ""
        )

        for (i in labels.indices) {
            val y = startY - i * spacing
            val isSelected = i == selectedIndex

            font.color = if (isSelected) Color.YELLOW else Color.WHITE
            val label = if (isSelected) "> ${labels[i]}" else "  ${labels[i]}"
            font.draw(b, label, centerX - 90f, y)

            if (values[i].isNotEmpty()) {
                font.color = if (isSelected) Color.CYAN else Color.LIGHT_GRAY
                font.draw(b, values[i], centerX + 40f, y)
            }
        }

        font.color = Color.LIGHT_GRAY
        font.data.setScale(0.6f)
        if (game.isMobile) {
            font.draw(b, "Toque L/R: mudar valor", centerX - 60f, 30f)
        } else {
            font.draw(b, "A/D: mudar valor  Enter: confirmar", centerX - 90f, 30f)
        }
        font.data.setScale(0.9f)

        b.end()
    }

    override fun resize(width: Int, height: Int) {
        if (::viewport.isInitialized) viewport.update(width, height)
    }

    override fun dispose() {
        if (::font.isInitialized) font.dispose()
        if (::fontBig.isInitialized) fontBig.dispose()
    }
}
