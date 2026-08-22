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
    private var camera = OrthographicCamera()
    private var viewport: Viewport = FitViewport(FazendinhaGame.V_WIDTH, FazendinhaGame.V_HEIGHT, camera)
    private var font: BitmapFont = game.defaultFont
    private var bigFont = BitmapFont()
    private var selectedIndex = 0
    private var initialized = false

    override fun show() {
        try {
            bigFont.data.setScale(1.4f)
            bigFont.color = Color.WHITE
            font.data.setScale(0.9f)

            Gdx.input.inputProcessor = object : InputAdapter() {
                override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                    val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                    val cx = FazendinhaGame.V_WIDTH / 2f
                    for (i in 0..3) {
                        val y = 150f - i * 35f
                        if (v.x in cx - 100f..cx + 100f && v.y in y - 15f..y + 15f) {
                            if (i == 3) game.setScreen(MenuScreen(game))
                            else { selectedIndex = i; change(if (v.x < cx) -1 else 1) }
                            return true
                        }
                    }
                    return true
                }

                override fun keyDown(keycode: Int): Boolean {
                    when (keycode) {
                        Input.Keys.UP, Input.Keys.W -> selectedIndex = (selectedIndex - 1 + 4) % 4
                        Input.Keys.DOWN, Input.Keys.S -> selectedIndex = (selectedIndex + 1) % 4
                        Input.Keys.LEFT, Input.Keys.A -> change(-1)
                        Input.Keys.RIGHT, Input.Keys.D -> change(1)
                        Input.Keys.ENTER, Input.Keys.SPACE -> if (selectedIndex == 3) game.setScreen(MenuScreen(game)) else change(1)
                        Input.Keys.ESCAPE -> game.setScreen(MenuScreen(game))
                    }
                    return true
                }
            }
            initialized = true
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Settings show error", e)
        }
    }

    private fun change(dir: Int) {
        when (selectedIndex) {
            0 -> Settings.soundVolume = (Settings.soundVolume + dir * 10).coerceIn(0, 100)
            1 -> Settings.musicVolume = (Settings.musicVolume + dir * 10).coerceIn(0, 100)
            2 -> Settings.showFps = !Settings.showFps
        }
        Settings.save()
    }

    override fun render(delta: Float) {
        if (!initialized) return

        Gdx.gl.glClearColor(0.08f, 0.12f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()

        bigFont.setColor(Color.WHITE)
        bigFont.draw(b, "OPCOES", FazendinhaGame.V_WIDTH / 2 - 40f, FazendinhaGame.V_HEIGHT - 30f)

        val cx = FazendinhaGame.V_WIDTH / 2f
        val labels = arrayOf("Efeitos", "Musica", "FPS", "Voltar")
        val values = arrayOf("${Settings.soundVolume}%", "${Settings.musicVolume}%", if (Settings.showFps) "ON" else "OFF", "")

        for (i in labels.indices) {
            val y = 150f - i * 35f
            font.color = if (i == selectedIndex) Color.YELLOW else Color.WHITE
            font.draw(b, labels[i], cx - 80f, y)
            if (values[i].isNotEmpty()) {
                font.color = if (i == selectedIndex) Color.CYAN else Color.LIGHT_GRAY
                font.draw(b, values[i], cx + 30f, y)
            }
        }

        font.setColor(Color.LIGHT_GRAY)
        font.data.setScale(0.6f)
        val hint = if (game.isMobile) "Toque L/R: mudar" else "A/D: mudar  Enter: voltar"
        font.draw(b, hint, cx - 70f, 25f)
        font.data.setScale(0.9f)

        b.end()
    }

    override fun resize(width: Int, height: Int) { viewport.update(width, height) }
    override fun dispose() { bigFont.dispose() }
}
