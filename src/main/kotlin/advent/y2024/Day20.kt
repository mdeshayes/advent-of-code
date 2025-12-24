package advent.y2024

import advent.util.AStarPath
import advent.util.Map2d
import advent.util.Node
import advent.util.readAllLines
import kotlin.math.abs

fun main() {
    val lines = readAllLines("2024/day20_input.txt")
    val day20 = Day20(lines)
    println("Part1: ${day20.countCheatingCombinations()} cheats will save us at least 100 picoseconds")
    println("Part2: ${day20.countCheatingCombinations(100, 20)} cheats will save us at least 100 picoseconds")
}

class Day20(private val lines: List<String>) {

    private var charMap = Map2d(lines) { it }
    private val startPosition = findStartPosition()
    private val endPosition = findEndPosition()
    private val memory = mutableMapOf<Pair<Pair<Int, Int>, Pair<Int, Int>>, List<Node>>()

    private fun getPath(): List<Node> = AStarPath(charMap, 0).getBestPath(startPosition, endPosition)

    private fun findStartPosition(): Pair<Int, Int> = charMap.find { (_, char) -> char == 'S' }!!.first

    private fun findEndPosition(): Pair<Int, Int> = charMap.find { (_, char) -> char == 'E' }!!.first

    private fun resetMap() {
        charMap = Map2d(lines) { it }
    }

    fun countCheatingCombinations(minSaving: Int, cheatingDuration: Int): Int {
        resetMap()
        val path = getPath()
        val bestScore = path.size - 1
        return path.sumOf { node ->
            getPointsInRadius(node.x to node.y, cheatingDuration).count { point ->
                val currentCost = node.costFromStart
                val additionalCost = abs(node.x - point.first) + abs(node.y - point.second)
                var finalPath = memory[point to endPosition]
                if (finalPath == null) {
                    finalPath = AStarPath(charMap, 0).getBestPath(point, endPosition)
                    memory[point to endPosition] = finalPath
                }
                if (finalPath.isEmpty()) {
                    return@count false
                }
                val finalCost = currentCost + additionalCost + finalPath.last().costFromStart
                bestScore - finalCost > minSaving
            }
        }
    }

    private fun getPointsInRadius(point: Pair<Int, Int>, radius: Int): List<Pair<Int, Int>> {
        return charMap.filter { (coord, char) -> char != '#' && abs(coord.first - point.first) + abs(coord.second - point.second) <= radius }
            .map { it.first }
    }

    fun countCheatingCombinations(): Int {
        val scoreWithoutCheating = getPath().size - 1
        var count = 0
        return getAllWalls().count { coord ->
            count++
            resetMap()
            if (coord != startPosition && coord != endPosition) {
                charMap.set(coord, '.')
            }
            return@count scoreWithoutCheating - (getPath().size - 1) >= 100
        }
    }

    private fun getAllWalls(): List<Pair<Int, Int>> {
        return charMap.filter { (_, char) -> char == '#' }.map { (coord, _) -> coord }
    }

}