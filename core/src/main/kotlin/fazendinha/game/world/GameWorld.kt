package fazendinha.game.world

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import fazendinha.game.entities.Farmer
import fazendinha.game.entities.Merchant
import fazendinha.game.entities.Npc

class GameWorld {
    companion object {
        const val WORLD_WIDTH = 20
        const val WORLD_HEIGHT = 15
        const val TILE = 32f
    }

    private val tiles = Array(WORLD_HEIGHT) { Array(WORLD_WIDTH) { TileType.GRASS } }
    private val npcs = mutableListOf<Npc>()
    private val crops = mutableListOf<Crop>()

    enum class TileType { GRASS, DIRT, WATER, STONE, TREE, FENCE, HOUSE }

    init {
        generateWorld()
        npcs.add(Farmer("Joja", 8f * TILE, 8f * TILE))
        npcs.add(Merchant(12f * TILE, 4f * TILE))
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

    fun render(sr: ShapeRenderer) {
        for (y in 0 until WORLD_HEIGHT) {
            for (x in 0 until WORLD_WIDTH) {
                val px = x * TILE
                val py = y * TILE
                sr.color = when (tiles[y][x]) {
                    TileType.GRASS -> Color(0.3f, 0.65f, 0.2f, 1f)
                    TileType.DIRT -> Color(0.55f, 0.4f, 0.2f, 1f)
                    TileType.WATER -> Color(0.2f, 0.4f, 0.85f, 1f)
                    TileType.STONE -> Color(0.5f, 0.5f, 0.5f, 1f)
                    TileType.TREE -> Color(0.15f, 0.55f, 0.15f, 1f)
                    TileType.FENCE -> Color(0.45f, 0.3f, 0.15f, 1f)
                    TileType.HOUSE -> Color(0.65f, 0.35f, 0.2f, 1f)
                }
                sr.rect(px, py, TILE, TILE)

                if (tiles[y][x] == TileType.TREE) {
                    sr.color = Color(0.3f, 0.18f, 0.08f, 1f)
                    sr.rect(px + 12f, py, 8f, 18f)
                }
            }
        }

        for (crop in crops) {
            sr.color = Color(0.2f, 0.8f, 0.15f, 1f)
            val s = 6f + crop.growth * 8f
            sr.rect(crop.x - s / 2, crop.y - s / 2, s, s)
        }

        for (npc in npcs) {
            sr.color = Color(0.85f, 0.55f, 0.2f, 1f)
            sr.rect(npc.x - 6f, npc.y - 8f, 12f, 18f)
            sr.color = Color(0.95f, 0.8f, 0.65f, 1f)
            sr.circle(npc.x, npc.y + 12f, 5f)
        }
    }

    fun update(delta: Float) {
        for (npc in npcs) npc.update(delta)
        for (crop in crops) crop.update(delta)
    }

    fun isColliding(bounds: Rectangle): Boolean {
        val x1 = (bounds.x / TILE).toInt()
        val y1 = (bounds.y / TILE).toInt()
        val x2 = ((bounds.x + bounds.width) / TILE).toInt()
        val y2 = ((bounds.y + bounds.height) / TILE).toInt()

        for (ty in y1..y2) {
            for (tx in x1..x2) {
                if (tx < 0 || tx >= WORLD_WIDTH || ty < 0 || ty >= WORLD_HEIGHT) return true
                val t = tiles[ty][tx]
                if (t != TileType.GRASS && t != TileType.DIRT) return true
            }
        }
        return false
    }

    fun interact(x: Float, y: Float) {
        val tx = (x / TILE).toInt()
        val ty = (y / TILE).toInt()
        if (tx in 0 until WORLD_WIDTH && ty in 0 until WORLD_HEIGHT) {
            if (tiles[ty][tx] == TileType.DIRT) {
                crops.add(Crop(x, y))
            }
        }
    }

    fun getNearbyNpc(px: Float, py: Float): Npc? =
        npcs.firstOrNull { it.distanceTo(px, py) < 48f }
}

class Crop(val x: Float, val y: Float) {
    var growth = 0f
    private var timer = 0f

    fun update(delta: Float) {
        timer += delta
        if (timer >= 5f) {
            timer = 0f
            growth = (growth + 0.2f).coerceAtMost(1f)
        }
    }
}
