package fazendinha.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import fazendinha.game.screens.MenuScreen

class FazendinhaGame : Game() {
    lateinit var batch: SpriteBatch
        private set
    lateinit var shapeRenderer: ShapeRenderer
        private set

    val isMobile: Boolean by lazy {
        Gdx.app.type == com.badlogic.gdx.Application.ApplicationType.Android ||
        Gdx.app.type == com.badlogic.gdx.Application.ApplicationType.iOS
    }

    companion object {
        const val TITLE = "Fazendinha"
        const val V_WIDTH = 480f
        const val V_HEIGHT = 320f
    }

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        Settings.load()
        setScreen(MenuScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        screen?.dispose()
    }
}
