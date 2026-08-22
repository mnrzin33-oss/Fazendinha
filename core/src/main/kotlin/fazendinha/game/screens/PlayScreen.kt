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
import fazendinha.game.entities.Player
import fazendinha.game.world.GameWorld

class PlayScreen(private val game: FazendinhaGame) : ScreenAdapter() {
    private var camera = OrthographicCamera()
    private var viewport: Viewport = FitViewport(FazendinhaGame.V_WIDTH, FazendinhaGame.V_HEIGHT, camera)
    private var font: BitmapFont = game.defaultFont

    private var gameWorld = GameWorld()
    private var player = Player(5f, 5f)

    private var showInventory = false
    private var showDialog = false
    private var dialogText = ""
    private var initialized = false

    private var joyPointer = -1
    private val joyCenter = Vector2(60f, 80f)
    private val joyRadius = 35f
    private var joyDX = 0f
    private var joyDY = 0f

    override fun show() {
        try {
            font.data.setScale(0.8f)

            Gdx.input.inputProcessor = object : InputAdapter() {
                override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                    val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                    if (v.x < 140f && v.y < 180f) {
                        joyPointer = pointer
                        updateJoy(v.x, v.y)
                        return true
                    }
                    if (v.x > 380f) {
                        if (v.y in 50f..80f) onTalk()
                        else if (v.y in 90f..120f) onUse()
                        else if (v.y in 10f..40f) showInventory = !showInventory
                        return true
                    }
                    if (showDialog) { showDialog = false; return true }
                    return false
                }

                override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                    if (pointer == joyPointer) {
                        val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                        updateJoy(v.x, v.y)
                        return true
                    }
                    return false
                }

                override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                    if (pointer == joyPointer) { joyPointer = -1; joyDX = 0f; joyDY = 0f; return true }
                    return false
                }
            }
            initialized = true
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Play show error", e)
        }
    }

    private fun updateJoy(tx: Float, ty: Float) {
        var dx = tx - joyCenter.x
        var dy = ty - joyCenter.y
        val len = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        if (len > joyRadius) { dx = dx / len * joyRadius; dy = dy / len * joyRadius }
        if (len > 2f) { joyDX = dx / joyRadius; joyDY = dy / joyRadius }
        else { joyDX = 0f; joyDY = 0f }
    }

    private fun onTalk() {
        if (showDialog) { showDialog = false; return }
        val npc = gameWorld.getNearbyNpc(player.x, player.y)
        if (npc != null) { dialogText = npc.talk(); showDialog = true }
    }

    private fun onUse() {
        if (showDialog) { showDialog = false; return }
        player.useTool(gameWorld)
    }

    override fun render(delta: Float) {
        if (!initialized) return

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        handleInput(delta)
        gameWorld.update(delta)
        camera.position.set(player.x, player.y, 0f)
        camera.update()

        val sr = game.shapeRenderer
        sr.projectionMatrix = camera.combined
        sr.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled)
        gameWorld.render(sr)
        player.render(sr)
        sr.end()

        renderHUD()
        if (game.isMobile) renderButtons()
        if (showDialog) renderDialog()
    }

    private fun renderHUD() {
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()
        font.setColor(Color.WHITE)
        font.draw(b, "Dia 1", viewport.worldWidth - 40f, viewport.worldHeight - 8f)
        font.draw(b, "HP: ${player.stamina.toInt()}", 8f, viewport.worldHeight - 8f)
        if (Settings.showFps) {
            font.setColor(Color.GREEN)
            font.draw(b, "FPS:${Gdx.graphics.framesPerSecond}", viewport.worldWidth - 60f, 12f)
        }
        if (showInventory) {
            font.setColor(Color.WHITE)
            font.draw(b, "- INVENTARIO -", viewport.worldWidth / 2 - 40f, viewport.worldHeight / 2 + 30f)
            font.draw(b, "Trigo x5", viewport.worldWidth / 2 - 30f, viewport.worldHeight / 2 + 10f)
            font.draw(b, "Machado x1", viewport.worldWidth / 2 - 35f, viewport.worldHeight / 2 - 10f)
            font.draw(b, "100 Moedas", viewport.worldWidth / 2 - 35f, viewport.worldHeight / 2 - 30f)
        }
        b.end()
    }

    private fun renderButtons() {
        val sr = game.shapeRenderer
        sr.projectionMatrix = viewport.camera.combined
        sr.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled)
        sr.color = Color(0.2f, 0.6f, 0.2f, 0.6f); sr.rect(400f, 50f, 50f, 30f)
        sr.color = Color(0.2f, 0.4f, 0.8f, 0.6f); sr.rect(400f, 90f, 50f, 30f)
        sr.color = Color(0.6f, 0.4f, 0.2f, 0.6f); sr.rect(400f, 10f, 50f, 30f)
        sr.color = Color(1f, 1f, 1f, 0.15f); sr.circle(joyCenter.x, joyCenter.y, joyRadius + 5f)
        sr.color = if (joyPointer >= 0) Color(1f, 1f, 1f, 0.5f) else Color(1f, 1f, 1f, 0.3f)
        sr.circle(joyCenter.x + joyDX * joyRadius, joyCenter.y + joyDY * joyRadius, 12f)
        sr.end()

        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()
        font.setColor(Color.WHITE)
        font.draw(b, "F", 418f, 70f)
        font.draw(b, "U", 418f, 110f)
        font.draw(b, "I", 418f, 30f)
        b.end()
    }

    private fun renderDialog() {
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()
        font.setColor(Color.CYAN)
        font.draw(b, dialogText, 16f, 50f)
        font.setColor(Color.GRAY)
        font.data.setScale(0.6f)
        font.draw(b, "Toque", 16f, 25f)
        font.data.setScale(0.8f)
        b.end()
    }

    private fun handleInput(delta: Float) {
        if (showDialog) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) showDialog = false
            return
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { game.setScreen(MenuScreen(game)); return }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) showInventory = !showInventory
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) onTalk()

        val spd = 100f * delta
        var dx = joyDX * spd
        var dy = joyDY * spd
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy += spd
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy -= spd
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx -= spd
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += spd
        if (dx != 0f || dy != 0f) player.move(dx, dy, gameWorld)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) player.useTool(gameWorld)
    }

    override fun resize(width: Int, height: Int) { viewport.update(width, height) }
    override fun dispose() {}
}
