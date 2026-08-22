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

class MenuScreen(private val game: FazendinhaGame) : ScreenAdapter() {
    private var camera = OrthographicCamera()
    private var viewport: Viewport = FitViewport(FazendinhaGame.V_WIDTH, FazendinhaGame.V_HEIGHT, camera)
    private var font: BitmapFont = game.defaultFont
    private var bigFont = BitmapFont()
    private var selectedIndex = 0
    private var initialized = false
    private var pulse = 0f

    private val items = arrayOf("Jogar", "Opcoes", "Sair")

    override fun show() {
        try {
            bigFont.data.setScale(1.8f)
            bigFont.color = Color.WHITE
            font.data.setScale(0.9f)

            Gdx.input.inputProcessor = object : InputAdapter() {
                override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                    val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                    val cx = FazendinhaGame.V_WIDTH / 2f
                    for (i in items.indices) {
                        val y = 140f - i * 40f
                        if (v.x in cx - 80f..cx + 80f && v.y in y - 15f..y + 15f) {
                            select(i)
                            return true
                        }
                    }
                    return true
                }

                override fun keyDown(keycode: Int): Boolean {
                    when (keycode) {
                        Input.Keys.UP, Input.Keys.W -> selectedIndex = (selectedIndex - 1 + items.size) % items.size
                        Input.Keys.DOWN, Input.Keys.S -> selectedIndex = (selectedIndex + 1) % items.size
                        Input.Keys.ENTER, Input.Keys.SPACE -> select(selectedIndex)
                    }
                    return true
                }
            }
            initialized = true
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Menu show error", e)
        }
    }

    private fun select(index: Int) {
        when (index) {
            0 -> game.setScreen(PlayScreen(game))
            1 -> game.setScreen(SettingsScreen(game))
            2 -> Gdx.app.exit()
        }
    }

    override fun render(delta: Float) {
        if (!initialized) return

        Gdx.gl.glClearColor(0.05f, 0.15f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        pulse += delta * 2f

        val sr = game.shapeRenderer
        sr.projectionMatrix = viewport.camera.combined
        sr.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0.1f, 0.35f, 0.1f, 1f)
        sr.rect(0f, 0f, FazendinhaGame.V_WIDTH, FazendinhaGame.V_HEIGHT / 3)
        sr.color = Color(0.3f, 0.2f, 0.1f, 1f)
        sr.rect(FazendinhaGame.V_WIDTH / 2 - 50f, FazendinhaGame.V_HEIGHT / 3, 100f, 80f)
        sr.color = Color(0.6f, 0.2f, 0.1f, 1f)
        sr.rect(FazendinhaGame.V_WIDTH / 2 - 55f, FazendinhaGame.V_HEIGHT / 3 + 78f, 110f, 15f)
        sr.end()

        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()

        val alpha = 0.7f + 0.3f * Math.sin(pulse.toDouble()).toFloat()
        bigFont.color = Color(1f, 0.9f, 0.5f, alpha)
        bigFont.draw(b, "FAZENDINHA", FazendinhaGame.V_WIDTH / 2 - 90f, FazendinhaGame.V_HEIGHT - 40f)

        val cx = FazendinhaGame.V_WIDTH / 2f
        for (i in items.indices) {
            val y = 140f - i * 40f
            font.color = if (i == selectedIndex) Color.YELLOW else Color.WHITE
            val text = if (i == selectedIndex) "> ${items[i]} <" else items[i]
            font.draw(b, text, cx - text.length * 4f, y)
        }

        font.setColor(Color.LIGHT_GRAY)
        font.data.setScale(0.6f)
        val hint = if (game.isMobile) "Toque para selecionar" else "W/S:Mover  Enter:Selecionar"
        font.draw(b, hint, cx - 70f, 25f)
        font.data.setScale(0.9f)

        b.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        bigFont.dispose()
    }
}
