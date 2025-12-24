package advent.y2024

import advent.util.Map2d
import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.WEST
import advent.util.plus
import advent.util.readAllLines


private const val UP_CHAR = '^'
private const val RIGHT_CHAR = '>'
private const val DOWN_CHAR = 'v'
private const val LEFT_CHAR = '<'
private const val OBSTACLE_CHAR = '#'

fun main() {
    val lines = readAllLines("2024/day6_input.txt")
    val day06 = Day06(lines)
    val startPart1 = System.currentTimeMillis()
    val countGuardPosition = day06.countGuardPosition()
    println("Part1: $countGuardPosition positions found (${System.currentTimeMillis() - startPart1}ms)")
    val startPart2 = System.currentTimeMillis()
    val countObstaclesForLoop = day06.countObstaclesForLoop()
    println("Part2: $countObstaclesForLoop obstacles positions found (${System.currentTimeMillis() - startPart2}ms)")
}

class Day06(private val lines: List<String>) {

    private var charMap: Map2d<Char> = Map2d(lines) { it }
    private var guardInitialPath = mutableListOf<Pair<Int, Int>>()
    private var guardPosition = findGuard()

    private fun resetMap() {
        charMap = Map2d(lines) { it }
        guardPosition = findGuard()
    }

    private fun findGuard(): Pair<Int, Int>? =
        charMap.find { (_, char) -> listOf(UP_CHAR, RIGHT_CHAR, DOWN_CHAR, LEFT_CHAR).contains(char) }?.first

    private fun updateCharMap(alreadyVisited: MutableList<String>) {
        val nonNullGuardPosition = guardPosition!!
        when (val guardChar = charMap.get(nonNullGuardPosition)) {
            UP_CHAR -> move(alreadyVisited, nonNullGuardPosition, UP_CHAR, RIGHT_CHAR)
            DOWN_CHAR -> move(alreadyVisited, nonNullGuardPosition, DOWN_CHAR, LEFT_CHAR)
            RIGHT_CHAR -> move(alreadyVisited, nonNullGuardPosition, RIGHT_CHAR, DOWN_CHAR)
            LEFT_CHAR -> move(alreadyVisited, nonNullGuardPosition, LEFT_CHAR, UP_CHAR)
            else -> throw IllegalStateException("Unknown guard char $guardChar")
        }
    }


    private fun move(
        alreadyVisited: MutableList<String>,
        position: Pair<Int, Int>,
        currentChar: Char,
        turnRightChar: Char
    ) {
        val x = position.first
        val y = position.second
        val direction = getDirection(currentChar)
        try {
            val nextChar = charMap.getOrNull(x, y, direction)
            if (nextChar == OBSTACLE_CHAR) {
                charMap.set(position, turnRightChar)
            } else {
                val newPosition = direction + position
                if (isAlreadyVisited(
                        alreadyVisited,
                        currentChar,
                        newPosition
                    )
                ) throw InfiniteLoopException()
                if (charMap.isValid(newPosition.first, newPosition.second)) {
                    charMap.set(newPosition, currentChar)
                    guardPosition = newPosition
                    markAsVisited(alreadyVisited, currentChar, newPosition)
                } else {
                    guardPosition = null
                }
                charMap.set(position, '.')
            }
        } catch (_: IndexOutOfBoundsException) {
            charMap.set(position, getVisitedChar(currentChar))
        }
    }

    private fun markAsVisited(alreadyVisited: MutableList<String>, char: Char, coord: Pair<Int, Int>) =
        alreadyVisited.add(getVisitedString(char, coord))

    private fun isAlreadyVisited(alreadyVisited: MutableList<String>, char: Char, coord: Pair<Int, Int>): Boolean =
        alreadyVisited.contains(getVisitedString(char, coord))

    private fun getVisitedString(char: Char, coord: Pair<Int, Int>) = "$char/${coord.first}/${coord.second}"

    class InfiniteLoopException : Throwable()

    private fun getVisitedChar(currentChar: Char): Char {
        return when (currentChar) {
            UP_CHAR -> 'U'
            DOWN_CHAR -> 'D'
            RIGHT_CHAR -> 'R'
            LEFT_CHAR -> 'L'
            else -> throw IllegalStateException("Unknown char $currentChar")
        }
    }

    private fun getDirection(currentChar: Char): Map2d.Direction {
        return when (currentChar) {
            UP_CHAR -> NORTH
            DOWN_CHAR -> SOUTH
            RIGHT_CHAR -> EAST
            LEFT_CHAR -> WEST
            else -> throw IllegalStateException("Unknown char $currentChar")
        }
    }

    private fun isGuardInMap(): Boolean = guardPosition != null

    fun countGuardPosition(): Int {
        val alreadyVisited = mutableListOf<String>()
        markAsVisited(alreadyVisited, '^', guardPosition!!)
        while (isGuardInMap()) {
            updateCharMap(alreadyVisited)
        }
        guardInitialPath.clear()
        guardInitialPath.addAll(alreadyVisited.map {
            val coord = it.substring(2).split("/")
            coord[0].toInt() to coord[1].toInt()
        })
        return alreadyVisited.map { it.substring(1) }.toSet().size
    }

    fun countObstaclesForLoop(): Int {
        val positions = mutableSetOf<Pair<Int, Int>>()
        guardInitialPath.drop(1).forEach { coord ->
            resetMap()
            if (coord != guardPosition) {
                val alreadyVisited = mutableListOf<String>()
                charMap.set(coord, OBSTACLE_CHAR)
                try {
                    while (isGuardInMap()) {
                        updateCharMap(alreadyVisited)
                    }
                } catch (e: InfiniteLoopException) {
                    positions.add(coord)
                }
            }
        }
        return positions.size
    }
}


