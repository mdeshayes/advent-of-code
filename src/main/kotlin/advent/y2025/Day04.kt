package advent.y2025

import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.NORTH_EAST
import advent.util.Map2d.Direction.Companion.NORTH_WEST
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.SOUTH_EAST
import advent.util.Map2d.Direction.Companion.SOUTH_WEST
import advent.util.Map2d.Direction.Companion.WEST
import advent.util.plus
import advent.util.readAllLines

fun main() {
    val allLines = readAllLines("2025/day4_input.txt")
    val day04 = Day04(allLines)
    println("Number of immediately accessible papers: ${day04.getImmediatelyAccessiblePapers().size}")
    println("Number of accessible papers: ${day04.getNumberOfAccessiblePapers()}")
}

class Day04(lines: List<String>) {

    val grid = Map2d(lines) { it }

    fun getImmediatelyAccessiblePapers(): List<Pair<Int, Int>> {
        return grid.filter { (position, _) -> isAccessible(position.first, position.second) }.map { it.first }
    }

    private fun isAccessible(x: Int, y: Int): Boolean {
        if (grid.get(x, y) != '@') return false
        val neighbors = listOf(
            NORTH + (x to y),
            NORTH_WEST + (x to y),
            NORTH_EAST + (x to y),
            WEST + (x to y),
            EAST + (x to y),
            SOUTH + (x to y),
            SOUTH_WEST + (x to y),
            SOUTH_EAST + (x to y),
        ).count { grid.getOrNull(it) == '@' }
        return neighbors < 4
    }

    fun getNumberOfAccessiblePapers(): Long {
        var totalNumberOfAccessiblePapers = 0L
        while (true) {
            val accessiblePapers = getImmediatelyAccessiblePapers()
            if (accessiblePapers.isEmpty()) return totalNumberOfAccessiblePapers
            totalNumberOfAccessiblePapers += accessiblePapers.size.toLong()
            accessiblePapers.forEach { grid.set(it, '.') }
        }
    }


}

