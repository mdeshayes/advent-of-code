package advent.y2025

import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.SOUTH_EAST
import advent.util.Map2d.Direction.Companion.SOUTH_WEST
import advent.util.plus
import advent.util.readAllLines

fun main() {
    val allLines = readAllLines("2025/day7_input.txt")
    val day07 = Day07(allLines)
    println("Number of splits: ${day07.getNumberOfSplits()}")
    println("Number of timelines: ${day07.getNumberOfTimelines()}")
}

class Day07(private val lines: List<String>) {

    val cache: MutableMap<Pair<Int, Int>, Long> = mutableMapOf()

    fun getNumberOfSplits(): Long {
        val tachyonMap: Map2d<Char> = Map2d(lines) { it }
        var nbSplits = 0L
        (0 until tachyonMap.getHeight()).forEach { row ->
            val tachyons = getTachyons(tachyonMap, row)
            tachyons.forEach { (position, _) ->
                nbSplits += moveTachyonAndCountSplits(tachyonMap, position)
            }
        }
        return nbSplits
    }

    fun getNumberOfTimelines(): Long {
        val initialMap = Map2d(lines) { it }
        val initialTachyon = getTachyons(initialMap, 0).single().first
        return getNumberOfTimelines(initialMap, initialTachyon)
    }

    private fun moveTachyonAndCountSplits(
        tachyonMap: Map2d<Char>,
        position: Pair<Int, Int>,
    ): Long {
        val south = tachyonMap.getOrNull(SOUTH + position)
        when (south) {
            '^' -> {
                tachyonMap.set(SOUTH_WEST + position, 'S')
                tachyonMap.set(SOUTH_EAST + position, 'S')
                return 1
            }

            '.' -> {
                tachyonMap.set(SOUTH + position, 'S')
            }
        }
        return 0
    }

    private fun getTachyons(tachyonMap: Map2d<Char>, row: Int) =
        tachyonMap.filter { (position, char) -> position.second == row && char == 'S' }

    private fun getNumberOfSplits(position: Pair<Int, Int>): Long {
        val tachyonMap: Map2d<Char> = Map2d(lines) { it }
        val south = tachyonMap.getOrNull(SOUTH + position)
        return when (south) {
            '^' -> 1 + getNumberOfSplits(SOUTH_EAST + position) + getNumberOfSplits(SOUTH_WEST + position)
            '.' -> getNumberOfSplits(SOUTH + position)
            else -> 0
        }
    }

    private fun getNumberOfTimelines(
        initialMap: Map2d<Char>,
        position: Pair<Int, Int>,
    ): Long {
        if (cache.containsKey(position)) {
            return cache[position]!!
        }
        val south = initialMap.getOrNull(SOUTH + position)
        val numberOfTimelines = when (south) {
            '^' -> getNumberOfTimelines(initialMap, SOUTH_WEST + position) +
                    getNumberOfTimelines(initialMap, SOUTH_EAST + position)

            '.' -> getNumberOfTimelines(initialMap, SOUTH + position)
            else -> 1
        }
        cache[position] = numberOfTimelines
        return numberOfTimelines
    }

}



