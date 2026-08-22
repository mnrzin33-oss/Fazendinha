package fazendinha.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

object Settings {
    var soundVolume = 80
    var musicVolume = 60
    var showFps = false

    private const val PREF_NAME = "fazendinha_settings"

    fun load() {
        try {
            val prefs = Gdx.app.getPreferences(PREF_NAME)
            soundVolume = prefs.getInteger("sound", 80)
            musicVolume = prefs.getInteger("music", 60)
            showFps = prefs.getBoolean("fps", false)
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Settings load error", e)
        }
    }

    fun save() {
        try {
            val prefs = Gdx.app.getPreferences(PREF_NAME)
            prefs.putInteger("sound", soundVolume)
            prefs.putInteger("music", musicVolume)
            prefs.putBoolean("fps", showFps)
            prefs.flush()
        } catch (e: Exception) {
            Gdx.app.error("Fazendinha", "Settings save error", e)
        }
    }
}
