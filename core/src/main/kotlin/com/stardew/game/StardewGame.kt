package com.stardew.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.stardew.game.screens.PlayScreen

class StardewGame : Game() {
    lateinit var batch: SpriteBatch
        private set
    lateinit var shapeRenderer: ShapeRenderer
        private set

    companion object {
        const val TITLE = "Fazendinha"
        const val V_WIDTH = 480f
        const val V_HEIGHT = 320f
    }

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        setScreen(PlayScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        screen?.dispose()
    }
}
