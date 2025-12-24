package advent.y2024

import advent.util.AStarPath
import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Node
import advent.util.readAllLines

fun main() {
    val lines = readAllLines("2024/day16_input.txt")
    val day16 = Day16(lines)
    println("Part1: Lowest path score possible is: ${day16.bestPath().last().costFromStart}")
    println("Part2: There are ${day16.getAllBestPathPositions().size} positions on the best paths")
}

class Day16(lines: List<String>) {

    private val charMap = Map2d(lines) { it }
    private val reindeerPosition = findReindeer()
    private val endPosition = findEnd()

    private fun findReindeer(): Pair<Int, Int> =
        charMap.find { (_, char) -> char == 'S' }?.first!!

    private fun findEnd(): Pair<Int, Int> =
        charMap.find { (_, char) -> char == 'E' }?.first!!

    private fun getAllBestPaths(): MutableSet<List<Node>> {
        return AStarPath(charMap).getAllBestPaths(
            Node(
                reindeerPosition.first,
                reindeerPosition.second,
                direction = EAST
            ),
            Node(
                endPosition.first,
                endPosition.second,
                direction = NORTH
            )
        )
    }

    fun getAllBestPathPositions(): Set<Pair<Int, Int>> {
        val allPaths = getAllBestPaths()
        val allBestPathPositions = allPaths
            .flatMap { path -> path.map { node -> node.x to node.y } }
            .toSet()
        return allBestPathPositions
    }

    fun bestPath(): List<Node> {
        return AStarPath(
            charMap
        ).getBestPath(
            Node(
                reindeerPosition.first,
                reindeerPosition.second,
                direction = EAST
            ),
            Node(
                endPosition.first,
                endPosition.second,
                direction = NORTH
            ),
        )
    }
}