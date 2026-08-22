package com.stardew.game.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.stardew.game.StardewGame
import com.stardew.game.entities.Farmer
import com.stardew.game.entities.Merchant
import com.stardew.game.entities.Npc
import com.stardew.game.entities.Player

class GameWorld {
    val WORLD_WIDTH = 20
    val WORLD_HEIGHT = 15
    private val TILE_SIZE = StardewGame.PPM

    private val tiles = Array(WORLD_HEIGHT) { Array(WORLD_WIDTH) { TileType.GRASS } }
    private val npcs = mutableListOf<Npc>()
    private val crops = mutableListOf<Crop>()

    enum class TileType { GRASS, DIRT, WATER, STONE, TREE, FENCE, HOUSE }

    init {
        generateWorld()
        spawnNpcs()
    }

    private fun generateWorld() {
        for (y in 0 until WORLD_HEIGHT) {
            for (x in 0 until WORLD_WIDTH) {
                tiles[y][x] = when {
                    y == 0 || y == WORLD_HEIGHT - 1 || x == 0 || x == WORLD_WIDTH - 1 -> TileType.FENCE
                    x in 3..6 && y in 3..6 -> TileType.DIRT
                    x == 10 && y in 2..5 -> TileType.WATER
                    x == 14 && y == 7 -> TileType.HOUSE
                    Math.random() < 0.05 -> TileType.TREE
                    Math.random() < 0.03 -> TileType.STONE
                    else -> TileType.GRASS
                }
            }
        }
    }

    private fun spawnNpcs() {
        npcs.add(Farmer("Joja", 8f * TILE_SIZE, 8f * TILE_SIZE))
        npcs.add(Merchant(12f * TILE_SIZE, 4f * TILE_SIZE))
    }

    fun render(batch: Batch, camera: OrthographicCamera) {
        val shapeRenderer = ShapeRenderer()
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = camera.combined

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        for (y in 0 until WORLD_HEIGHT) {
            for (x in 0 until WORLD_WIDTH) {
                val tile = tiles[y][x]
                val px = x * TILE_SIZE
                val py = y * TILE_SIZE

                shapeRenderer.color = when (tile) {
                    TileType.GRASS -> Color(0.3f, 0.7f, 0.2f, 1f)
                    TileType.DIRT -> Color(0.6f, 0.4f, 0.2f, 1f)
                    TileType.WATER -> Color(0.2f, 0.4f, 0.9f, 1f)
                    TileType.STONE -> Color(0.5f, 0.5f, 0.5f, 1f)
                    TileType.TREE -> Color(0.1f, 0.5f, 0.1f, 1f)
                    TileType.FENCE -> Color(0.4f, 0.25f, 0.1f, 1f)
                    TileType.HOUSE -> Color(0.6f, 0.3f, 0.2f, 1f)
                }

                shapeRenderer.rect(px, py, TILE_SIZE, TILE_SIZE)

                if (tile == TileType.TREE) {
                    shapeRenderer.color = Color(0.25f, 0.15f, 0.05f, 1f)
                    shapeRenderer.rect(px + TILE_SIZE * 0.35f, py, TILE_SIZE * 0.3f, TILE_SIZE * 0.6f)
                }

                if (tile == TileType.HOUSE) {
                    shapeRenderer.color = Color(0.5f, 0.25f, 0.15f, 1f)
                    shapeRenderer.rect(px + 4f, py + TILE_SIZE - 16f, TILE_SIZE - 8f, 14f)
                }
            }
        }

        for (crop in crops) {
            shapeRenderer.color = Color(0.1f, 0.8f, 0.1f, 1f)
            val size = 6f + crop.growth * 6f
            shapeRenderer.rect(crop.x - size / 2, crop.y - size / 2, size, size)
        }

        for (npc in npcs) {
            shapeRenderer.color = Color(0.9f, 0.6f, 0.2f, 1f)
            shapeRenderer.rect(npc.x - 6f, npc.y - 8f, 12f, 16f)
        }

        shapeRenderer.end()
        shapeRenderer.dispose()
    }

    fun update(delta: Float) {
        for (npc in npcs) {
            npc.update(delta)
        }
        for (crop in crops) {
            crop.update(delta)
        }
    }

    fun isColliding(bounds: Rectangle): Boolean {
        val tileX1 = ((bounds.x) / TILE_SIZE).toInt()
        val tileY1 = ((bounds.y) / TILE_SIZE).toInt()
        val tileX2 = ((bounds.x + bounds.width) / TILE_SIZE).toInt()
        val tileY2 = ((bounds.y + bounds.height) / TILE_SIZE).toInt()

        for (ty in tileY1..tileY2) {
            for (tx in tileX1..tileX2) {
                if (tx < 0 || tx >= WORLD_WIDTH || ty < 0 || ty >= WORLD_HEIGHT) return true
                val tile = tiles[ty][tx]
                if (tile == TileType.WATER || tile == TileType.FENCE || tile == TileType.TREE || tile == TileType.STONE) {
                    return true
                }
            }
        }
        return false
    }

    fun interact(x: Float, y: Float, player: Player) {
        val tileX = (x / TILE_SIZE).toInt()
        val tileY = (y / TILE_SIZE).toInt()

        if (tileX in 0 until WORLD_WIDTH && tileY in 0 until WORLD_HEIGHT) {
            if (tiles[tileY][tileX] == TileType.DIRT) {
                crops.add(Crop(x, y))
            }
        }
    }

    fun getNearbyNpc(px: Float, py: Float): Npc? {
        return npcs.firstOrNull { it.distanceTo(px, py) < 48f }
    }

    fun dispose() {}
}

class Crop(val x: Float, val y: Float) {
    var growth = 0f
    private var growTimer = 0f

    fun update(delta: Float) {
        growTimer += delta
        if (growTimer >= 5f) {
            growTimer = 0f
            growth = (growth + 0.2f).coerceAtMost(1f)
        }
    }
}
