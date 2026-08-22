package com.stardew.game.entities

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

abstract class Npc(
    val name: String,
    startX: Float,
    startY: Float
) {
    var x = startX
    var y = startY
    protected var targetX = startX
    protected var targetY = startY
    protected var moveTimer = 0f
    protected var moveInterval = 3f

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

    fun distanceTo(px: Float, py: Float): Float {
        return Vector2.dst(x, y, px, py)
    }
}

class Farmer(name: String, x: Float, y: Float) : Npc(name, x, y) {
    private val dialogues = listOf(
        "Olá! Precisa de sementes?",
        "A colheita está indo bem este ano!",
        "O clima está perfeito para plantar.",
        "Não esqueça de regar suas plantações!"
    )

    override fun talk(): String {
        return dialogues[MathUtils.random(0, dialogues.size - 1)]
    }
}

class Merchant(x: Float, y: Float) : Npc("Viajante", x, y) {
    private val dialogues = listOf(
        "Tenho itens raros à venda!",
        "Viajei de longe para chegar aqui.",
        "Posso comprar seus produtos por um bom preço.",
        "As montanhas ao norte são perigosas..."
    )

    override fun talk(): String {
        return dialogues[MathUtils.random(0, dialogues.size - 1)]
    }
}
