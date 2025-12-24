package advent.y2024

import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.WEST
import advent.util.plus
import advent.util.readAllLines


fun main() {
    val lines = readAllLines("2024/day10_input.txt")
    val day10 = Day10(lines)
    println("Part1: score is ${day10.getScore()}")
    println("Part2: rating is ${day10.getRating()}")
}

class Day10(lines: List<String>) {

    private val intMap = Map2d(lines, Char::digitToInt)

    fun getScore() = findAllStartingPoints().map { getAllTrailEnds(it).toSet() }.sumOf { it.size }

    fun getRating() = findAllStartingPoints().map { getAllTrailEnds(it) }.sumOf { it.size }

    private fun getAllTrailEnds(startingPoint: Pair<Int, Int>): List<Pair<Int, Int>> {
        val (x, y) = startingPoint
        val height = intMap.get(x, y)
        if (height == 9) {
            return listOf(x to y)
        }
        val northScore = if (isNorthPossible(x, y)) getAllTrailEnds(NORTH + startingPoint) else emptyList()
        val southScore = if (isSouthPossible(x, y)) getAllTrailEnds(SOUTH + startingPoint) else emptyList()
        val westScore = if (isWestPossible(x, y)) getAllTrailEnds(WEST + startingPoint) else emptyList()
        val eastScore = if (isEastPossible(x, y)) getAllTrailEnds(EAST + startingPoint) else emptyList()
        return northScore + southScore + westScore + eastScore
    }

    private fun isEastPossible(x: Int, y: Int): Boolean {
        return isPossible(x, y, EAST)
    }

    private fun isWestPossible(x: Int, y: Int): Boolean {
        return isPossible(x, y, WEST)
    }

    private fun isSouthPossible(x: Int, y: Int): Boolean {
        return isPossible(x, y, SOUTH)
    }

    private fun isNorthPossible(x: Int, y: Int): Boolean {
        return isPossible(x, y, NORTH)
    }

    private fun isPossible(x: Int, y: Int, direction: Map2d.Direction): Boolean {
        return intMap.isValid(x, y, direction) && doesHeightIncreaseByOne(x, y, direction)
    }

    private fun doesHeightIncreaseByOne(x: Int, y: Int, direction: Map2d.Direction) =
        intMap.getOrNull(direction + (x to y)) == intMap.get(x, y) + 1


    private fun findAllStartingPoints(): List<Pair<Int, Int>> =
        intMap.filter { (_, value) -> value == 0 }.map { it.first }

}