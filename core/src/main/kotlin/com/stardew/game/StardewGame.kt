package com.stardew.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.stardew.game.screens.PlayScreen

class StardewGame : Game() {
    lateinit var batch: SpriteBatch
        private set

    companion object {
        const val TITLE = "Fazendinha"
        const val V_WIDTH = 320f
        const val V_HEIGHT = 180f
        const val PPM = 32f
    }

    override fun create() {
        batch = SpriteBatch()
        setScreen(PlayScreen(this))
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        batch.dispose()
        screen?.dispose()
    }
}
