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
import fazendinha.game.Settings
import fazendinha.game.entities.Player
import fazendinha.game.world.GameWorld

class PlayScreen(private val game: FazendinhaGame) : ScreenAdapter() {
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var font: BitmapFont

    private lateinit var gameWorld: GameWorld
    private lateinit var player: Player

    private var showInventory = false
    private var showDialog = false
    private var dialogText = ""
    private var initialized = false

    private var joystickPointer = -1
    private val joystickCenter = Vector2(60f, 80f)
    private val joystickRadius = 35f
    private var joystickDX = 0f
    private var joystickDY = 0f

    private val btnTalk = floatArrayOf(400f, 50f, 50f, 30f)
    private val btnUse = floatArrayOf(400f, 90f, 50f, 30f)
    private val btnInv = floatArrayOf(400f, 10f, 50f, 30f)

    override fun show() {
        try {
            camera = OrthographicCamera()
            viewport = FitViewport(FazendinhaGame.V_WIDTH, FazendinhaGame.V_HEIGHT, camera)
            font = BitmapFont()
            font.color = Color.WHITE
            font.data.setScale(0.8f)

            gameWorld = GameWorld()
            player = Player(5f, 5f)

            setupInput()
            initialized = true
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Init error", e)
        }
    }

    private fun setupInput() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                try {
                    val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))

                    if (v.x < 140f && v.y < 180f) {
                        joystickPointer = pointer
                        updateJoystick(v.x, v.y)
                        return true
                    }

                    if (v.x > 380f) {
                        if (v.y in btnTalk[1]..btnTalk[1] + btnTalk[3]) onTalk()
                        else if (v.y in btnUse[1]..btnUse[1] + btnUse[3]) onUseTool()
                        else if (v.y in btnInv[1]..btnInv[1] + btnInv[3]) showInventory = !showInventory
                        return true
                    }

                    if (showDialog) {
                        showDialog = false
                        return true
                    }
                } catch (e: Exception) {
                    Gdx.app.error("Fazendinha", "Touch error", e)
                }
                return false
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                if (pointer == joystickPointer) {
                    val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                    updateJoystick(v.x, v.y)
                    return true
                }
                return false
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (pointer == joystickPointer) {
                    joystickPointer = -1
                    joystickDX = 0f
                    joystickDY = 0f
                    return true
                }
                return false
            }
        }
    }

    private fun updateJoystick(touchX: Float, touchY: Float) {
        var dx = touchX - joystickCenter.x
        var dy = touchY - joystickCenter.y
        val len = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        if (len > joystickRadius) {
            dx = dx / len * joystickRadius
            dy = dy / len * joystickRadius
        }
        if (len > 2f) {
            joystickDX = dx / joystickRadius
            joystickDY = dy / joystickRadius
        } else {
            joystickDX = 0f
            joystickDY = 0f
        }
    }

    private fun onTalk() {
        if (showDialog) {
            showDialog = false
            return
        }
        val npc = gameWorld.getNearbyNpc(player.x, player.y)
        if (npc != null) {
            dialogText = npc.talk()
            showDialog = true
        }
    }

    private fun onUseTool() {
        if (showDialog) {
            showDialog = false
            return
        }
        player.useTool(gameWorld)
    }

    override fun render(delta: Float) {
        if (!initialized) return

        try {
            Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            handleInput(delta)
            gameWorld.update(delta)

            camera.position.set(player.x, player.y, 0f)
            camera.update()

            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

            renderWorld()
            renderHUD()
            if (game.isMobile) renderButtons()
            if (showDialog) renderDialog()
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Render error", e)
        }
    }

    private fun renderWorld() {
        val sr = game.shapeRenderer
        sr.projectionMatrix = camera.combined
        sr.begin(ShapeRenderer.ShapeType.Filled)
        gameWorld.render(sr)
        player.render(sr)
        sr.end()
    }

    private fun renderHUD() {
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()
        font.setColor(Color.WHITE)
        font.draw(b, "Dia 1 - Primavera", viewport.worldWidth - 120f, viewport.worldHeight - 8f)
        font.draw(b, "Estamina: ${player.stamina.toInt()}/100", 8f, viewport.worldHeight - 8f)

        if (Settings.showFps) {
            font.setColor(Color.GREEN)
            font.draw(b, "FPS: ${Gdx.graphics.framesPerSecond}", viewport.worldWidth - 80f, 12f)
        }

        if (showInventory) {
            font.setColor(Color.WHITE)
            font.draw(b, "---- INVENTARIO ----", viewport.worldWidth / 2 - 50f, viewport.worldHeight / 2 + 40f)
            font.draw(b, "Semente de Trigo x5", viewport.worldWidth / 2 - 50f, viewport.worldHeight / 2 + 20f)
            font.draw(b, "Machado x1", viewport.worldWidth / 2 - 50f, viewport.worldHeight / 2f)
            font.draw(b, "100 Moedas", viewport.worldWidth / 2 - 50f, viewport.worldHeight / 2 - 20f)
        }
        b.end()
    }

    private fun renderButtons() {
        val sr = game.shapeRenderer
        sr.projectionMatrix = viewport.camera.combined
        sr.begin(ShapeRenderer.ShapeType.Filled)

        sr.color = Color(0.2f, 0.6f, 0.2f, 0.6f)
        sr.rect(btnTalk[0], btnTalk[1], btnTalk[2], btnTalk[3])
        sr.color = Color(0.2f, 0.4f, 0.8f, 0.6f)
        sr.rect(btnUse[0], btnUse[1], btnUse[2], btnUse[3])
        sr.color = Color(0.6f, 0.4f, 0.2f, 0.6f)
        sr.rect(btnInv[0], btnInv[1], btnInv[2], btnInv[3])

        sr.color = Color(1f, 1f, 1f, 0.15f)
        sr.circle(joystickCenter.x, joystickCenter.y, joystickRadius + 5f)
        sr.color = if (joystickPointer >= 0) Color(1f, 1f, 1f, 0.5f) else Color(1f, 1f, 1f, 0.3f)
        sr.circle(joystickCenter.x + joystickDX * joystickRadius, joystickCenter.y + joystickDY * joystickRadius, 12f)

        sr.end()

        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()
        font.setColor(Color.WHITE)
        font.draw(b, "F", btnTalk[0] + 18f, btnTalk[1] + 20f)
        font.draw(b, "U", btnUse[0] + 18f, btnUse[1] + 20f)
        font.draw(b, "I", btnInv[0] + 18f, btnInv[1] + 20f)
        b.end()
    }

    private fun renderDialog() {
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()
        font.setColor(Color.CYAN)
        font.draw(b, dialogText, 16f, 55f)
        font.setColor(Color.GRAY)
        font.draw(b, "Toque pra continuar", 16f, 28f)
        b.end()
    }

    private fun handleInput(delta: Float) {
        if (showDialog) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                showDialog = false
            }
            return
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(MenuScreen(game))
            return
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) showInventory = !showInventory
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) onTalk()

        val speed = 100f * delta
        var dx = joystickDX * speed
        var dy = joystickDY * speed

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy += speed
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy -= speed
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx -= speed
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += speed

        if (dx != 0f || dy != 0f) player.move(dx, dy, gameWorld)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) player.useTool(gameWorld)
    }

    override fun resize(width: Int, height: Int) {
        if (::viewport.isInitialized) viewport.update(width, height)
    }

    override fun dispose() {
        if (::font.isInitialized) font.dispose()
    }
}
