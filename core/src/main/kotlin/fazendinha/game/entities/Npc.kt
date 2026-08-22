package fazendinha.game.entities

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

abstract class Npc(
    val name: String,
    startX: Float,
    startY: Float
) {
    var x = startX
    var y = startY
    private var targetX = startX
    private var targetY = startY
    private var moveTimer = 0f
    private var moveInterval = 3f

    abstract fun talk(): String

    open fun update(delta: Float) {
        moveTimer += delta
        if (moveTimer >= moveInterval) {
            moveTimer = 0f
            moveInterval = MathUtils.random(2f, 6f)
            targetX = x + MathUtils.random(-2f, 2f) * 32f
            targetY = y + MathUtils.random(-2f, 2f) * 32f
        }
        val dir = Vector2(targetX - x, targetY - y)
        if (dir.len() > 1f) {
            dir.nor().scl(20f * delta)
            x += dir.x
            y += dir.y
        }
    }

    fun distanceTo(px: Float, py: Float): Float = Vector2.dst(x, y, px, py)
}

class Farmer(name: String, x: Float, y: Float) : Npc(name, x, y) {
    private val lines = listOf(
        "Ola! Precisa de sementes?",
        "A colheita esta indo bem!",
        "O clima esta perfeito pra plantar.",
        "Nao esqueca de regar!"
    )
    override fun talk(): String = lines[MathUtils.random(0, lines.size - 1)]
}

class Merchant(x: Float, y: Float) : Npc("Viajante", x, y) {
    private val lines = listOf(
        "Tenho itens raros!",
        "Viajei de longe pra chegar aqui.",
        "Compro seus produtos!",
        "As montanhas sao perigosas..."
    )
    override fun talk(): String = lines[MathUtils.random(0, lines.size - 1)]
}
