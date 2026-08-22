package fazendinha.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import fazendinha.game.screens.MenuScreen

class FazendinhaGame : Game() {
    lateinit var batch: SpriteBatch
        private set
    lateinit var shapeRenderer: ShapeRenderer
        private set
    lateinit var defaultFont: BitmapFont
        private set

    var isMobile = false
        private set

    companion object {
        const val TITLE = "Fazendinha"
        const val V_WIDTH = 480f
        const val V_HEIGHT = 320f
    }

    override fun create() {
        try {
            batch = SpriteBatch()
            shapeRenderer = ShapeRenderer()
            defaultFont = BitmapFont()
            defaultFont.color = com.badlogic.gdx.graphics.Color.WHITE

            isMobile = Gdx.app.type == com.badlogic.gdx.Application.ApplicationType.Android ||
                       Gdx.app.type == com.badlogic.gdx.Application.ApplicationType.iOS

            Settings.load()
            setScreen(MenuScreen(this))
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Create error", e)
        }
    }

    override fun dispose() {
        try {
            batch.dispose()
            shapeRenderer.dispose()
            defaultFont.dispose()
            screen?.dispose()
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Dispose error", e)
        }
    }
}
