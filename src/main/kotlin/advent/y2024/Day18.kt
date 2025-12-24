package advent.y2024

import advent.util.AStarPath
import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Node
import advent.util.readAllLines


fun main() {
    val lines = readAllLines("2024/day18_input.txt")
    val day18 = Day18(lines, size = 71)
    println("Part1: path size is ${day18.findPathAfter(1024).size - 1}")
    println("Part2: latest corrupted byte is ${day18.findLatestCorruptedByte()}")
}

class Day18(lines: List<String>, private val size: Int) {

    private var charMap = Map2d((0 until size).map { ".".repeat(size) }) { it }
    private var corruptedBytes = lines.map {
        it.split(",")[0].toInt() to it.split(",")[1].toInt()
    }

    private fun resetMap() {
        charMap = Map2d((0 until size).map { ".".repeat(size) }) { it }
    }

    fun findLatestCorruptedByte(): Pair<Int, Int> {
        for (i in 1024 until corruptedBytes.size) {
            resetMap()
            if (findPathAfter(i).isEmpty()) {
                return corruptedBytes[i-1]
            }
        }
        throw IllegalStateException("No byte prevent us to escape the memory")
    }

    fun findPathAfter(numberOfCorruptedBytes: Int): List<Node> {
        corruptedBytes.subList(0, numberOfCorruptedBytes).forEach {
            charMap.set(it, '#')
        }
        return AStarPath(charMap, 0)
            .getBestPath(
                Node(0, 0, direction = SOUTH),
                Node(size - 1, size - 1, direction = NORTH)
            )
    }
}
