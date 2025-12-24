package advent.y2025

import advent.util.readAllLines
import kotlin.math.abs

fun main() {
    val allLines = readAllLines("2025/day9_input.txt")
    val day09 = Day09(allLines)
    println("Largest rectangle: ${day09.getLargestRectangleArea()}")
    println("Largest rectangle with green and red tiles: ${day09.getLargestRectangleAreaWithGreenAndRedTilesOnly()}")
}

data class Position(val x: Int, val y: Int)

class Day09(lines: List<String>) {

    private val redTilePositions: List<Pair<Int, Int>> = lines.map {
        it.split(",")[0].toInt() to it.split(",")[1].toInt()
    }

    var redTiles: MutableList<Position> = mutableListOf()

    var switchableTiles: MutableList<Position> = mutableListOf()

    var tile1: Position? = null
    var tile2: Position? = null

    init {
        redTiles.add(Position(redTilePositions[0].first, redTilePositions[0].second))
        for (index in 1 until redTilePositions.size - 1) {
            val tilePosition = redTilePositions[index]
            redTiles.add(Position(tilePosition.first, tilePosition.second))
        }
        redTiles.add(Position(redTilePositions.last().first, redTilePositions.last().second))
        for (i in 0 until redTiles.size - 1) {
            val redTile = redTiles[i]
            var nextRedTile = redTiles[i + 1]
            if (redTile.x == nextRedTile.x) {
                if (redTile.y > nextRedTile.y) {
                    switchableTiles.addAll((nextRedTile.y..redTile.y).map { Position(redTile.x, it) })
                } else {
                    switchableTiles.addAll((redTile.y..nextRedTile.y).map { Position(redTile.x, it) })
                }
            } else {
                if (redTile.x > nextRedTile.x) {
                    switchableTiles.addAll((nextRedTile.x..redTile.x).map { Position(it, redTile.y) })
                } else {
                    switchableTiles.addAll((redTile.x..nextRedTile.x).map { Position(it, redTile.y) })
                }
            }
        }
    }

    fun getLargestRectangleArea(): Long {
        var largestArea = 0L
        for (i in 0 until redTiles.size) {
            for (j in i + 1 until redTiles.size) {
                val tile1 = redTiles[i]
                val tile2 = redTiles[j]
                val area = (abs(tile1.x - tile2.x).toLong() + 1) * (abs(tile1.y - tile2.y).toLong() + 1)
                if (area > largestArea) {
                    largestArea = area
                }
            }
        }
        return largestArea
    }

    fun getLargestRectangleAreaWithGreenAndRedTilesOnly(): Long {
        var largestArea = 0L
        for (i in 0 until redTiles.size) {
            for (j in 0 until redTiles.size) {
                val tile1 = redTiles[i]
                val tile2 = redTiles[j]
                val area = (abs(tile1.x - tile2.x) + 1).toLong() * (abs(tile1.y - tile2.y).toLong() + 1)
                if (tile1.x == tile2.x || tile1.y == tile2.y) continue
                if (area > largestArea) {
                    if (allTilesAreSwitchable(tile1, tile2)) {
                        this.tile1 = tile1
                        this.tile2 = tile2
                        largestArea = area
                    }
                }
            }
        }
        return largestArea
    }

    private fun allTilesAreSwitchable(tile1: Position, tile2: Position): Boolean {
        val corner1 = tile1.x to tile1.y
        val corner2 = tile2.x to tile2.y
        val corner3 = tile1.x to tile2.y
        val corner4 = tile2.x to tile1.y
        redTiles.forEach {
            if (isPointBetween(
                    x = it.x,
                    y = it.y,
                    corner1 = corner1,
                    corner2 = corner2,
                    corner3 = corner3,
                    corner4 = corner4
                )
            ) return false
        }
        switchableTiles.forEach {
            if (isPointBetween(
                    x = it.x,
                    y = it.y,
                    corner1 = corner1,
                    corner2 = corner2,
                    corner3 = corner3,
                    corner4 = corner4
                )
            ) return false
        }
        return true
    }

    private fun isPointBetween(
        x: Int,
        y: Int,
        corner1: Pair<Int, Int>,
        corner2: Pair<Int, Int>,
        corner3: Pair<Int, Int>,
        corner4: Pair<Int, Int>,
    ): Boolean = x > listOf(corner1.first, corner2.first, corner3.first, corner4.first).min() &&
            x < listOf(corner1.first, corner2.first, corner3.first, corner4.first).max() &&
            y > listOf(corner1.second, corner2.second, corner3.second, corner4.second).min() &&
            y < listOf(corner1.second, corner2.second, corner3.second, corner4.second).max()
}



