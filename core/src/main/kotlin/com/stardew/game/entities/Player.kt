package com.stardew.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.stardew.game.StardewGame
import com.stardew.game.world.GameWorld

class Player(startX: Float, startY: Float) {
    var x = startX * StardewGame.PPM
    var y = startY * StardewGame.PPM
    val width = 12f
    val height = 16f
    var stamina = 100f
    var speed = 80f

    private var facing = Direction.DOWN
    private var animTimer = 0f
    private var isMoving = false

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    val bounds: Rectangle
        get() = Rectangle(x - width / 2, y - height / 2, width, height)

    fun move(dx: Float, dy: Float, world: GameWorld) {
        val newX = x + dx
        val newY = y + dy

        val testBounds = Rectangle(newX - width / 2, newY - height / 2, width, height)

        if (!world.isColliding(testBounds)) {
            x = newX
            y = newY
        }

        if (dx > 0) facing = Direction.RIGHT
        else if (dx < 0) facing = Direction.LEFT
        else if (dy > 0) facing = Direction.UP
        else if (dy < 0) facing = Direction.DOWN

        isMoving = true
    }

    fun useTool(world: GameWorld) {
        val targetX = x + when (facing) {
            Direction.RIGHT -> 20f
            Direction.LEFT -> -20f
            else -> 0f
        }
        val targetY = y + when (facing) {
            Direction.UP -> 20f
            Direction.DOWN -> -20f
            else -> 0f
        }

        world.interact(targetX, targetY, this)
        stamina = (stamina - 1f).coerceAtLeast(0f)
    }

    fun render(renderer: ShapeRenderer) {
        renderer.color = Color(0.2f, 0.6f, 1f, 1f)
        renderer.rect(x - width / 2, y - height / 2, width, height)

        renderer.color = Color(1f, 0.8f, 0.6f, 1f)
        val headSize = 8f
        renderer.rect(x - headSize / 2, y + height / 2 - 2f, headSize, headSize)
    }

    fun render(batch: com.badlogic.gdx.graphics.g2d.Batch) {
        val shapeRenderer = ShapeRenderer()
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND)
        render(shapeRenderer)
        shapeRenderer.dispose()
    }

    fun recharge(amount: Float) {
        stamina = (stamina + amount).coerceAtMost(100f)
    }
}
