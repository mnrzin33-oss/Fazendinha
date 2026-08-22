package com.stardew.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.stardew.game.world.GameWorld

class Player(startX: Float, startY: Float) {
    var x = startX * 32f
    var y = startY * 32f
    val width = 14f
    val height = 20f
    var stamina = 100f

    private var facing = Direction.DOWN

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
        if (dy > 0) facing = Direction.UP
        else if (dy < 0) facing = Direction.DOWN
    }

    fun useTool(world: GameWorld) {
        val targetX = x + when (facing) {
            Direction.RIGHT -> 24f
            Direction.LEFT -> -24f
            else -> 0f
        }
        val targetY = y + when (facing) {
            Direction.UP -> 24f
            Direction.DOWN -> -24f
            else -> 0f
        }
        world.interact(targetX, targetY)
        stamina = (stamina - 1f).coerceAtLeast(0f)
    }

    fun render(sr: ShapeRenderer) {
        sr.color = Color(0.2f, 0.5f, 0.9f, 1f)
        sr.rect(x - width / 2, y - height / 2, width, height)

        sr.color = Color(0.95f, 0.8f, 0.65f, 1f)
        sr.circle(x, y + height / 2 + 4f, 6f)

        sr.color = Color(0.4f, 0.25f, 0.1f, 1f)
        val hatW = 10f
        sr.rect(x - hatW / 2, y + height / 2 + 8f, hatW, 3f)
    }
}
