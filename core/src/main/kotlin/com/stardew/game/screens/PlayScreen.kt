package com.stardew.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.stardew.game.StardewGame
import com.stardew.game.entities.Player
import com.stardew.game.world.GameWorld

class PlayScreen(private val game: StardewGame) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val viewport: Viewport = FitViewport(StardewGame.V_WIDTH, StardewGame.V_HEIGHT, camera)
    private val font = BitmapFont()
    private val shapeRenderer = ShapeRenderer()

    private val gameWorld = GameWorld()
    private val player = Player(5f, 5f)

    private var showInventory = false
    private var showDialog = false
    private var dialogText = ""

    override fun show() {
        font.color = Color.WHITE
        font.data.setScale(0.8f)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        handleInput(delta)
        update(delta)

        camera.position.set(player.x, player.y, 0f)
        camera.update()

        game.batch.projectionMatrix = camera.combined

        gameWorld.render(game.batch, camera)
        player.render(game.batch)

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            showInventory = !showInventory
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            val npc = gameWorld.getNearbyNpc(player.x, player.y)
            if (npc != null) {
                dialogText = npc.talk()
                showDialog = true
            }
        }

        val speed = 80f * delta
        var dx = 0f
        var dy = 0f

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy += speed
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy -= speed
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx -= speed
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += speed

        if (dx != 0f || dy != 0f) {
            player.move(dx, dy, gameWorld)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.useTool(gameWorld)
        }
    }

    private fun update(delta: Float) {
        gameWorld.update(delta)
    }

    private fun renderHUD() {
        game.batch.projectionMatrix = viewport.camera.combined
        game.batch.begin()

        font.draw(game.batch, "WASD: Mover | E: Falar | I: Inventário | Espaço: Usar", 5f, viewport.worldHeight - 5f)

        font.draw(game.batch, "Dia 1 - Primavera", viewport.worldWidth - 150f, viewport.worldHeight - 5f)

        font.draw(game.batch, "Estamina: ${player.stamina.toInt()}/100", 5f, 15f)

        if (showInventory) {
            renderInventory()
        }

        game.batch.end()
    }

    private fun renderInventory() {
        val boxWidth = 120f
        val boxHeight = 80f
        val x = (viewport.worldWidth - boxWidth) / 2
        val y = (viewport.worldHeight - boxHeight) / 2

        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.8f)
        shapeRenderer.rect(x, y, boxWidth, boxHeight)
        shapeRenderer.end()

        font.draw(game.batch, "--- INVENTÁRIO ---", x + 10f, y + boxHeight - 10f)
        font.draw(game.batch, "Semente de Trigo x5", x + 10f, y + boxHeight - 30f)
        font.draw(game.batch, "Machado x1", x + 10f, y + boxHeight - 45f)
        font.draw(game.batch, "100 Moedas", x + 10f, y + boxHeight - 60f)
    }

    private fun renderDialog() {
        if (!showDialog) return

        val boxWidth = viewport.worldWidth - 20f
        val boxHeight = 40f
        val x = 10f
        val y = 10f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0.3f, 0.9f)
        shapeRenderer.rect(x, y, boxWidth, boxHeight)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(x, y, boxWidth, 1f)
        shapeRenderer.rect(x, y + boxHeight, boxWidth, 1f)
        shapeRenderer.rect(x, y, 1f, boxHeight)
        shapeRenderer.rect(x + boxWidth, y, 1f, boxHeight)
        shapeRenderer.end()

        game.batch.projectionMatrix = viewport.camera.combined
        game.batch.begin()
        font.draw(game.batch, dialogText, x + 10f, y + boxHeight - 12f)
        font.setColor(Color.GRAY)
        font.draw(game.batch, "[Espaço para continuar]", x + 10f, y + 10f)
        font.setColor(Color.WHITE)
        game.batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        font.dispose()
        shapeRenderer.dispose()
        gameWorld.dispose()
    }
}
