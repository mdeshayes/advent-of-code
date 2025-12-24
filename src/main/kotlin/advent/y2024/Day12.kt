package advent.y2024

import advent.util.Map2d
import advent.util.Map2d.Direction
import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.NORTH_EAST
import advent.util.Map2d.Direction.Companion.NORTH_WEST
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.SOUTH_EAST
import advent.util.Map2d.Direction.Companion.WEST
import advent.util.plus
import advent.util.readAllLines
import advent.util.toCurrency


fun main() {
    val lines = readAllLines("2024/day12_input.txt")
    val map = Map2d(lines) { it }
    val day12 = Day12(map)
    println("Part1: Total price is ${toCurrency(day12.getTotalPrice())}")
    println("Part2: Total price after discount is ${toCurrency(day12.getTotalDiscountPrice())}")
}

class Day12(private val charMap: Map2d<Char>) {

    private var regions = mutableListOf<Region>()

    init {
        charMap.forEach { (coord, char) ->
            if (regions.find { it.points.contains(coord) } == null) {
                regions.add(createRegion(coord, char))
            }
        }
    }

    private fun createRegion(initialCoord: Pair<Int, Int>, char: Char): Region {
        val points = mutableListOf<Pair<Int, Int>>()
        findPoints(initialCoord.first, initialCoord.second, points)
        return Region(char, points, charMap)
    }

    private fun findPoints(x: Int, y: Int, knownPoints: MutableList<Pair<Int, Int>>) {
        knownPoints.add(x to y)
        explore(x, y, NORTH, knownPoints)
        explore(x, y, SOUTH, knownPoints)
        explore(x, y, EAST, knownPoints)
        explore(x, y, WEST, knownPoints)
    }

    private fun explore(
        x: Int,
        y: Int,
        direction: Direction,
        knownPoints: MutableList<Pair<Int, Int>>
    ) {
        val char = charMap.get(x, y)
        if (charMap.isValid(x, y, direction) && charMap.getOrNull(x, y, direction) == char) {
            val newPosition = direction + (x to y)
            if (!knownPoints.contains(newPosition)) {
                findPoints(x + direction.deltaX, y + direction.deltaY, knownPoints)
            }
        }
    }

    fun getTotalPrice(): Long = regions.sumOf { it.getArea().toLong() * it.getPerimeter() }
    fun getTotalDiscountPrice(): Long = regions.sumOf { it.getArea().toLong() * it.getSides() }

    class Region(private val char: Char, val points: List<Pair<Int, Int>>, private val charMap: Map2d<Char>) {
        fun getArea(): Int = points.size

        fun getPerimeter(): Int = points.sumOf { countBorders(it.first, it.second) }

        fun getSides(): Int = points.sumOf { countVertices(it.first, it.second) }

        private fun countVertices(x: Int, y: Int) =
            countInnerNorthEastVertex(x, y) +
                    countInnerNorthWestVertex(x, y) +
                    countInnerSouthEastVertex(x, y) +
                    countInnerSouthWestVertex(x, y) +
                    countOuterNorthEastVertex(x, y) +
                    countOuterNorthWestVertex(x, y) +
                    countOuterSouthEastVertex(x, y) +
                    countOuterSouthWestVertex(x, y)

        private fun countBorders(x: Int, y: Int) =
            countNorthBorder(x, y) +
                    countSouthBorder(x, y) +
                    countWestBorder(x, y) +
                    countEastBorder(x, y)

        private fun countNorthBorder(x: Int, y: Int) = if (notInRegion(x, y, NORTH)) 1 else 0
        private fun countSouthBorder(x: Int, y: Int) = if (notInRegion(x, y, SOUTH)) 1 else 0
        private fun countWestBorder(x: Int, y: Int) = if (notInRegion(x, y, WEST)) 1 else 0
        private fun countEastBorder(x: Int, y: Int) = if (notInRegion(x, y, EAST)) 1 else 0

        private fun countOuterSouthWestVertex(x: Int, y: Int) =
            if (notInRegion(x, y, SOUTH) && notInRegion(x, y, WEST)) 1 else 0

        private fun countOuterSouthEastVertex(x: Int, y: Int) =
            if (notInRegion(x, y, SOUTH) && notInRegion(x, y, EAST)) 1 else 0

        private fun countOuterNorthWestVertex(x: Int, y: Int) =
            if (notInRegion(x, y, NORTH) && notInRegion(x, y, WEST)) 1 else 0

        private fun countOuterNorthEastVertex(x: Int, y: Int) =
            if (notInRegion(x, y, NORTH) && notInRegion(x, y, EAST)) 1 else 0

        private fun countInnerSouthWestVertex(x: Int, y: Int) =
            if (notInRegion(x, y, SOUTH + WEST) && inRegion(x, y, SOUTH) && inRegion(x, y, WEST)) 1 else 0

        private fun countInnerSouthEastVertex(x: Int, y: Int) =
            if (notInRegion(x, y, SOUTH_EAST) && inRegion(x, y, SOUTH) && inRegion(x, y, EAST)) 1 else 0

        private fun countInnerNorthWestVertex(x: Int, y: Int) =
            if (notInRegion(x, y, NORTH_WEST) && inRegion(x, y, NORTH) && inRegion(x, y, WEST)) 1 else 0

        private fun countInnerNorthEastVertex(x: Int, y: Int) =
            if (notInRegion(x, y, NORTH_EAST) && inRegion(x, y, NORTH) && inRegion(x, y, EAST)) 1 else 0

        private fun inRegion(x: Int, y: Int, direction: Direction) = !notInRegion(x, y, direction)

        private fun notInRegion(x: Int, y: Int, direction: Direction) =
            !charMap.isValid(x, y, direction) || charMap.getOrNull(x, y, direction) != char
    }

}


