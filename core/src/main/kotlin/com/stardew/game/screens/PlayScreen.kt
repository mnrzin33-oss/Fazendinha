package com.stardew.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.stardew.game.StardewGame
import com.stardew.game.entities.Player
import com.stardew.game.world.GameWorld

class PlayScreen(private val game: StardewGame) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val viewport: Viewport = FitViewport(StardewGame.V_WIDTH, StardewGame.V_HEIGHT, camera)
    private val font = BitmapFont()

    private val gameWorld = GameWorld()
    private val player = Player(5f, 5f)

    private var showInventory = false
    private var showDialog = false
    private var dialogText = ""

    // Touch controls
    private val touchPos = Vector2()
    private val joystickCenter = Vector2(60f, 80f)
    private val joystickRadius = 35f
    private var joystickPointer = -1
    private var joystickDir = Vector2.Zero

    private var isMobile = false

    // Button areas (x, y, w, h)
    private val btnTalk = floatArrayOf(400f, 50f, 50f, 30f)
    private val btnUse = floatArrayOf(400f, 90f, 50f, 30f)
    private val btnInv = floatArrayOf(400f, 10f, 50f, 30f)

    override fun show() {
        font.color = Color.WHITE
        font.data.setScale(0.8f)

        isMobile = (Gdx.app.type == com.badlogic.gdx.Application.ApplicationType.Android ||
                    Gdx.app.type == com.badlogic.gdx.Application.ApplicationType.iOS)

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))

                // Joystick area (left side)
                if (v.x < 140f && v.y < 180f) {
                    joystickPointer = pointer
                    updateJoystick(v)
                    return true
                }

                // Buttons (right side)
                if (v.x > 380f) {
                    when {
                        inButton(v.x, v.y, btnTalk) -> onTalk()
                        inButton(v.x, v.y, btnUse) -> onUseTool()
                        inButton(v.x, v.y, btnInv) -> showInventory = !showInventory
                    }
                    return true
                }

                // Tap anywhere to dismiss dialog
                if (showDialog) {
                    showDialog = false
                    return true
                }

                return false
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                if (pointer == joystickPointer) {
                    val v = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
                    updateJoystick(v)
                    return true
                }
                return false
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (pointer == joystickPointer) {
                    joystickPointer = -1
                    joystickDir = Vector2.Zero
                    return true
                }
                return false
            }
        }
    }

    private fun inButton(x: Float, y: Float, btn: FloatArray): Boolean {
        return x >= btn[0] && x <= btn[0] + btn[2] && y >= btn[1] && y <= btn[1] + btn[3]
    }

    private fun updateJoystick(touch: Vector2) {
        joystickDir = Vector2(touch.x - joystickCenter.x, touch.y - joystickCenter.y)
        if (joystickDir.len() > joystickRadius) {
            joystickDir.limit(joystickRadius)
        }
        if (joystickDir.len() > 2f) {
            joystickDir.nor()
        } else {
            joystickDir = Vector2.Zero
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
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        handleInput(delta)
        gameWorld.update(delta)

        camera.position.set(player.x, player.y, 0f)
        camera.update()

        val sr = game.shapeRenderer
        sr.projectionMatrix = camera.combined

        Gdx.gl.glEnable(GL20.GL_BLEND)
        sr.begin(ShapeRenderer.ShapeType.Filled)
        gameWorld.render(sr)
        player.render(sr)
        sr.end()

        renderHUD()
        if (isMobile) renderButtons()
        renderDialog()
    }

    private fun handleInput(delta: Float) {
        if (showDialog) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                showDialog = false
            }
            return
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) showInventory = !showInventory

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) onTalk()

        val speed = 100f * delta
        var dx = joystickDir.x * speed
        var dy = joystickDir.y * speed

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy += speed
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy -= speed
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx -= speed
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += speed

        if (dx != 0f || dy != 0f) player.move(dx, dy, gameWorld)

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) player.useTool(gameWorld)
    }

    private fun renderHUD() {
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()

        font.setColor(Color.WHITE)
        font.draw(b, "Dia 1 - Primavera", viewport.worldWidth - 120f, viewport.worldHeight - 8f)
        font.draw(b, "Estamina: ${player.stamina.toInt()}/100", 8f, viewport.worldHeight - 8f)

        if (!isMobile) {
            font.setColor(Color.LIGHT_GRAY)
            font.draw(b, "WASD:Mover  E:Falar  I:Inv  Space:Usar", 8f, 12f)
        }

        if (showInventory) renderInventory()

        b.end()
    }

    private fun renderInventory() {
        val bw = 150f
        val bh = 100f
        val bx = (viewport.worldWidth - bw) / 2
        val by = (viewport.worldHeight - bh) / 2

        font.setColor(Color.WHITE)
        font.draw(b, "---- INVENTARIO ----", bx + 10f, by + bh - 14f)
        font.draw(b, "Semente de Trigo x5", bx + 10f, by + bh - 36f)
        font.draw(b, "Machado x1", bx + 10f, by + bh - 54f)
        font.draw(b, "100 Moedas", bx + 10f, by + bh - 72f)
        font.setColor(Color.GRAY)
        font.draw(b, "Toque pra fechar", bx + 10f, by + 10f)
    }

    private fun renderButtons() {
        val sr = game.shapeRenderer
        sr.projectionMatrix = viewport.camera.combined

        Gdx.gl.glEnable(GL20.GL_BLEND)
        sr.begin(ShapeRenderer.ShapeType.Filled)

        // Talk button
        sr.color = Color(0.2f, 0.6f, 0.2f, 0.7f)
        sr.rect(btnTalk[0], btnTalk[1], btnTalk[2], btnTalk[3])

        // Use button
        sr.color = Color(0.2f, 0.4f, 0.8f, 0.7f)
        sr.rect(btnUse[0], btnUse[1], btnUse[2], btnUse[3])

        // Inventory button
        sr.color = Color(0.6f, 0.4f, 0.2f, 0.7f)
        sr.rect(btnInv[0], btnInv[1], btnInv[2], btnInv[3])

        // Joystick background
        sr.color = Color(1f, 1f, 1f, 0.15f)
        sr.circle(joystickCenter.x, joystickCenter.y, joystickRadius + 5f)

        // Joystick knob
        if (joystickPointer >= 0) {
            sr.color = Color(1f, 1f, 1f, 0.5f)
        } else {
            sr.color = Color(1f, 1f, 1f, 0.3f)
        }
        val knobX = joystickCenter.x + joystickDir.x * joystickRadius
        val knobY = joystickCenter.y + joystickDir.y * joystickRadius
        sr.circle(knobX, knobY, 12f)

        sr.end()

        // Button labels
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
        if (!showDialog) return
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()
        font.setColor(Color.CYAN)
        font.draw(b, dialogText, 16f, 55f)
        font.setColor(Color.GRAY)
        font.draw(b, "Toque pra continuar", 16f, 28f)
        b.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        font.dispose()
    }
}
