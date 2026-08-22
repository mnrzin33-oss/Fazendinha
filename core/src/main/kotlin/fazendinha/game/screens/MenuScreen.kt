package fazendinha.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import fazendinha.game.FazendinhaGame

class MenuScreen(private val game: FazendinhaGame) : ScreenAdapter() {
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var font: BitmapFont
    private lateinit var fontBig: BitmapFont

    private var selectedIndex = 0
    private val menuItems = arrayOf("Jogar", "Opcoes", "Sair")

    private var isMobile = false
    private var initialized = false

    private var titlePulse = 0f

    override fun show() {
        try {
            camera = OrthographicCamera()
            viewport = FitViewport(FazendinhaGame.V_WIDTH, FazendinhaGame.V_HEIGHT, camera)
            font = BitmapFont()
            font.color = Color.WHITE
            font.data.setScale(0.9f)
            fontBig = BitmapFont()
            fontBig.color = Color.WHITE
            fontBig.data.setScale(1.8f)

            isMobile = (Gdx.app.type == com.badlogic.gdx.Application.ApplicationType.Android ||
                        Gdx.app.type == com.badlogic.gdx.Application.ApplicationType.iOS)

            setupInput()
            initialized = true
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Menu init error", e)
        }
    }

    private fun setupInput() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                val centerX = FazendinhaGame.V_WIDTH / 2f
                val startY = 120f
                val spacing = 40f

                for (i in menuItems.indices) {
                    val y = startY - i * spacing
                    if (v.x in centerX - 80f..centerX + 80f && v.y in y - 12f..y + 12f) {
                        onMenuSelect(i)
                        return true
                    }
                }
                return true
            }

            override fun keyDown(keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.UP, Input.Keys.W -> {
                        selectedIndex = (selectedIndex - 1 + menuItems.size) % menuItems.size
                    }
                    Input.Keys.DOWN, Input.Keys.S -> {
                        selectedIndex = (selectedIndex + 1) % menuItems.size
                    }
                    Input.Keys.ENTER, Input.Keys.SPACE -> {
                        onMenuSelect(selectedIndex)
                    }
                }
                return true
            }
        }
    }

    private fun onMenuSelect(index: Int) {
        when (index) {
            0 -> game.setScreen(PlayScreen(game))
            1 -> game.setScreen(SettingsScreen(game))
            2 -> Gdx.app.exit()
        }
    }

    override fun render(delta: Float) {
        if (!initialized) return

        try {
            titlePulse += delta * 2f

            Gdx.gl.glClearColor(0.05f, 0.15f, 0.05f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

            renderBackground()
            renderMenu()
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Menu render error", e)
        }
    }

    private fun renderBackground() {
        val sr = game.shapeRenderer
        sr.projectionMatrix = viewport.camera.combined
        sr.begin(ShapeRenderer.ShapeType.Filled)

        sr.color = Color(0.1f, 0.35f, 0.1f, 1f)
        sr.rect(0f, 0f, FazendinhaGame.V_WIDTH, FazendinhaGame.V_HEIGHT / 3)

        sr.color = Color(0.15f, 0.5f, 0.15f, 1f)
        for (i in 0..5) {
            val x = i * 100f + 20f
            sr.rect(x, FazendinhaGame.V_HEIGHT / 3 - 2f, 60f, 4f)
        }

        sr.color = Color(0.3f, 0.2f, 0.1f, 1f)
        sr.rect(FazendinhaGame.V_WIDTH / 2 - 50f, FazendinhaGame.V_HEIGHT / 3, 100f, 80f)
        sr.color = Color(0.6f, 0.2f, 0.1f, 1f)
        sr.rect(FazendinhaGame.V_WIDTH / 2 - 55f, FazendinhaGame.V_HEIGHT / 3 + 78f, 110f, 15f)

        sr.end()
    }

    private fun renderMenu() {
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()

        val titleAlpha = 0.7f + 0.3f * Math.sin(titlePulse.toDouble()).toFloat()
        fontBig.color = Color(1f, 0.9f, 0.5f, titleAlpha)
        fontBig.draw(b, "FAZENDINHA", FazendinhaGame.V_WIDTH / 2 - 90f, FazendinhaGame.V_HEIGHT - 40f)

        val centerX = FazendinhaGame.V_WIDTH / 2f
        val startY = 140f
        val spacing = 40f

        for (i in menuItems.indices) {
            val y = startY - i * spacing
            val isSelected = i == selectedIndex

            font.color = if (isSelected) Color.YELLOW else Color.WHITE
            val text = if (isSelected) "> ${menuItems[i]} <" else menuItems[i]
            val textWidth = text.length * 7f
            font.draw(b, text, centerX - textWidth / 2, y)
        }

        font.color = Color.LIGHT_GRAY
        font.data.setScale(0.6f)
        if (isMobile) {
            font.draw(b, "Toque para selecionar", FazendinhaGame.V_WIDTH / 2 - 60f, 30f)
        } else {
            font.draw(b, "W/S: Mover  Enter: Selecionar", FazendinhaGame.V_WIDTH / 2 - 80f, 30f)
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
