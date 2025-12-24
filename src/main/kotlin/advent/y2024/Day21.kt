package advent.y2024

import advent.util.AStarPath
import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.WEST
import advent.util.readAllLines

fun main() {
    val lines = readAllLines("2024/day21_input.txt")
    val day21 = Day21(lines)
    println("Part1: complexity score is ${day21.getComplexityScore(2)}")
    println("Part2: complexity score is ${day21.getComplexityScore(25)}")
}

class Day21(private val codes: List<String>) {

    private val numericKeypad = Map2d(listOf("789", "456", "123", "#0A")) { it }
    private val directionalKeypad = Map2d(listOf("#^A", "<v>")) { it }
    private val memory = mutableMapOf<Pair<List<List<Char>>, Int>, Long>()

    fun getComplexityScore(levels: Int) = codes
        .map { code ->
            code to getSize(code, levels)
        }.sumOf { (code, pathSize) ->
            pathSize * code.substring(0, code.length - 1).toLong()
        }

    private fun getSize(code: String, levels: Int): Long {
        return getNumericKeypadDirections(code, getPosition('A', numericKeypad)).sumOf { possibleCombinations ->
            getSizeLevel(possibleCombinations, levels - 1)
        }
    }

    private fun getSizeLevel(possibleCombinations: List<List<Char>>, level: Int): Long {
        if (memory.containsKey(possibleCombinations to level)) {
            return memory[possibleCombinations to level]!!
        }
        var bestSize = Long.MAX_VALUE
        for (possibleCombination in possibleCombinations) {
            var segmentSize = 0L
            for (i in possibleCombination.indices) {
                val allPossibleDirectionInNextLevel = getAllPossibleDirectionsBetween(
                    directionalKeypad,
                    getPosition(if (i == 0) 'A' else possibleCombination[i - 1], directionalKeypad),
                    getPosition(possibleCombination[i], directionalKeypad)
                )
                segmentSize += if (level > 0) {
                    getSizeLevel(allPossibleDirectionInNextLevel, level - 1)
                } else {
                    allPossibleDirectionInNextLevel.minOf { it.size }.toLong()
                }
            }
            if (segmentSize < bestSize) {
                bestSize = segmentSize
            }
        }
        memory[possibleCombinations to level] = bestSize
        return bestSize
    }

    private fun getNumericKeypadDirections(code: String, position: Pair<Int, Int>): List<List<List<Char>>> {
        if (code.isEmpty()) {
            return emptyList()
        }
        val nextPosition = numericKeypad.find { (_, char) -> char == code[0] }!!.first
        return mutableListOf(getAllPossibleDirectionsBetween(numericKeypad, position, nextPosition)) +
                getNumericKeypadDirections(code.substring(1), nextPosition)
    }

    private fun getAllPossibleDirectionsBetween(
        map: Map2d<Char>,
        initialPosition: Pair<Int, Int>,
        nextPosition: Pair<Int, Int>
    ): List<List<Char>> {
        return AStarPath(map, 0).getAllBestPaths(initialPosition, nextPosition).map { directions ->
            directions.drop(1).map { it.direction }.map {
                when (it) {
                    NORTH -> '^'
                    SOUTH -> 'v'
                    EAST -> '>'
                    WEST -> '<'
                    else -> throw IllegalStateException("Unknown direction $it")
                }
            } + 'A'
        }
    }

    private fun getPosition(charToFind: Char, map: Map2d<Char>) = map.find { (_, char) -> char == charToFind }!!.first

}