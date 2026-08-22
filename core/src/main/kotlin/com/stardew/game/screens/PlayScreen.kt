package com.stardew.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
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

    override fun show() {
        font.color = Color.WHITE
        font.data.setScale(0.9f)
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            val npc = gameWorld.getNearbyNpc(player.x, player.y)
            if (npc != null) {
                dialogText = npc.talk()
                showDialog = true
            }
        }

        val speed = 100f * delta
        var dx = 0f
        var dy = 0f

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
        font.draw(b, "WASD:Mover  E:Falar  I:Inventario Espaco:Usar", 8f, viewport.worldHeight - 8f)
        font.draw(b, "Dia 1 - Primavera", viewport.worldWidth - 140f, viewport.worldHeight - 8f)
        font.draw(b, "Estamina: ${player.stamina.toInt()}/100", 8f, 18f)

        if (showInventory) {
            val bw = 140f
            val bh = 90f
            val bx = (viewport.worldWidth - bw) / 2
            val by = (viewport.worldHeight - bh) / 2
            font.draw(b, "--- INVENTARIO ---", bx + 10f, by + bh - 12f)
            font.draw(b, "Semente de Trigo x5", bx + 10f, by + bh - 32f)
            font.draw(b, "Machado x1", bx + 10f, by + bh - 48f)
            font.draw(b, "100 Moedas", bx + 10f, by + bh - 64f)
        }
        b.end()
    }

    private fun renderDialog() {
        if (!showDialog) return
        val b = game.batch
        b.projectionMatrix = viewport.camera.combined
        b.begin()
        font.setColor(Color.CYAN)
        font.draw(b, dialogText, 16f, 50f)
        font.setColor(Color.GRAY)
        font.draw(b, "[Espaco]", 16f, 24f)
        font.setColor(Color.WHITE)
        b.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        font.dispose()
    }
}
