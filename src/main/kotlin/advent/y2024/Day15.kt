package advent.y2024

import advent.util.*
import advent.util.Map2d.Direction
import advent.util.Map2d.Direction.Companion.EAST
import advent.util.Map2d.Direction.Companion.NORTH
import advent.util.Map2d.Direction.Companion.SOUTH
import advent.util.Map2d.Direction.Companion.WEST
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration


private const val MAP_PATTERN = "[#.@O]+"
private const val INSTRUCTION_PATTERN = "[\\^>v<]+"
private const val ROBOT_CHAR = '@'
private const val WALL_CHAR = '#'
private const val BOX_CHAR = 'O'
private const val FREE_CHAR = '.'
private const val WIDER_BOX_CHAR_LEFT = '['
private const val WIDER_BOX_CHAR_RIGHT = ']'

fun main() {
    val lines = readAllLines("2024/day15_input.txt")
    val day15 = Day15(lines)
    println("Part 1: Sum of boxes GPS is ${day15.getBoxesGps()}")
//    day15.startVisualMode()
    println("Part 2: Sum of large boxes GPS is ${day15.getBoxesGpsWiderMap()}")
}

class Day15(lines: List<String>) {

    private val charMap = Map2d(extractMapLines(lines)) { it }
    private var position = findRobot(charMap)
    val widerCharMap = Map2d(extractWiderMapLines(lines)) { it }
    private var positionWiderMap = findRobot(widerCharMap)

    private var instructions = extractInstructions(lines)

    fun moveRobotInWiderMap() {
        moveRobotInWiderMap(instructions.first())
        instructions = instructions.drop(1)
    }

    fun getBoxesGps(): Int {
        instructions.forEach {
            moveRobot(it)
        }
        return charMap
            .filter { (_, char) -> char == BOX_CHAR }
            .sumOf { (coord, _) -> coord.first + coord.second * 100 }
    }

    fun getBoxesGpsWiderMap(): Int {
        instructions.forEach { direction ->
            moveRobotInWiderMap(direction)
        }
        return widerCharMap
            .filter { (_, char) -> char == WIDER_BOX_CHAR_LEFT }
            .sumOf { (coord, _) -> coord.first + coord.second * 100 }
    }

    private fun moveRobotInWiderMap(direction: Direction) {
        val newPosition = direction + positionWiderMap
        val newChar = widerCharMap.getOrNull(newPosition)
        when (newChar) {
            FREE_CHAR -> {
                moveRobotTo(newPosition)
            }

            WIDER_BOX_CHAR_RIGHT -> {
                val canMove = moveWiderBox(
                    boxLeftPosition = WEST + newPosition,
                    boxRightPosition = newPosition,
                    direction = direction
                )
                if (canMove) {
                    moveRobotTo(newPosition)
                }
            }

            WIDER_BOX_CHAR_LEFT -> {
                val canMove = moveWiderBox(
                    boxLeftPosition = newPosition,
                    boxRightPosition = EAST + newPosition,
                    direction = direction
                )
                if (canMove) {
                    moveRobotTo(newPosition)
                }
            }
        }
    }

    private fun moveRobotTo(newPosition: Pair<Int, Int>) {
        widerCharMap.set(positionWiderMap, FREE_CHAR)
        widerCharMap.set(newPosition, ROBOT_CHAR)
        positionWiderMap = newPosition
    }

    private fun moveRobot(direction: Direction) {
        val newRobotPosition = direction + position
        val newChar = charMap.getOrNull(newRobotPosition)
        when (newChar) {
            FREE_CHAR -> {
                charMap.set(position, FREE_CHAR)
                charMap.set(newRobotPosition, ROBOT_CHAR)
                position = newRobotPosition
            }

            BOX_CHAR -> {
                val canMove = moveBox(newRobotPosition, direction)
                if (canMove) {
                    charMap.set(position, FREE_CHAR)
                    charMap.set(newRobotPosition, ROBOT_CHAR)
                    position = newRobotPosition
                }
            }
        }
    }

    private fun moveBox(boxPosition: Pair<Int, Int>, direction: Direction): Boolean {
        val boxNewPosition = direction + boxPosition
        when (val newChar = charMap.getOrNull(boxNewPosition)) {
            FREE_CHAR -> charMap.set(boxNewPosition, BOX_CHAR).also { return true }
            WALL_CHAR -> return false
            BOX_CHAR -> return moveBox(boxNewPosition, direction)
            else -> throw IllegalStateException("Unknown char $newChar")
        }
    }

    private fun moveWiderBox(
        boxLeftPosition: Pair<Int, Int>,
        boxRightPosition: Pair<Int, Int>,
        direction: Direction
    ): Boolean {
        return when (direction) {
            NORTH, SOUTH -> moveWiderBoxVertically(direction, boxLeftPosition, boxRightPosition)
            EAST -> moveWiderBoxToEast(boxRightPosition)
            WEST -> moveWiderBoxToWest(boxLeftPosition)
            else -> throw IllegalStateException()
        }
    }

    private fun moveWiderBoxToEast(boxRightPosition: Pair<Int, Int>): Boolean {
        val eastCharPosition = EAST + boxRightPosition
        val eastChar = widerCharMap.getOrNull(eastCharPosition)
        when (eastChar) {
            FREE_CHAR -> {
                widerCharMap.set(eastCharPosition, WIDER_BOX_CHAR_RIGHT)
                widerCharMap.set(boxRightPosition, WIDER_BOX_CHAR_LEFT)
                return true
            }

            WALL_CHAR -> return false
            WIDER_BOX_CHAR_LEFT -> {
                if (moveWiderBoxToEast(EAST + eastCharPosition)) {
                    widerCharMap.set(eastCharPosition, WIDER_BOX_CHAR_RIGHT)
                    widerCharMap.set(boxRightPosition, WIDER_BOX_CHAR_LEFT)
                    return true
                }
                return false
            }

            else -> throw IllegalStateException(
                "Unexpected char when moving box to EAST ${
                    widerCharMap.getOrNull(
                        eastCharPosition
                    )
                }"
            )
        }
    }

    private fun moveWiderBoxToWest(boxLeftPosition: Pair<Int, Int>): Boolean {
        val westCharPosition = WEST + boxLeftPosition
        val westChar = widerCharMap.getOrNull(westCharPosition)
        when (westChar) {
            FREE_CHAR -> {
                widerCharMap.set(westCharPosition, WIDER_BOX_CHAR_LEFT)
                widerCharMap.set(boxLeftPosition, WIDER_BOX_CHAR_RIGHT)
                return true
            }

            WALL_CHAR -> return false
            WIDER_BOX_CHAR_RIGHT -> {
                if (moveWiderBoxToWest(WEST + westCharPosition)) {
                    widerCharMap.set(westCharPosition, WIDER_BOX_CHAR_LEFT)
                    widerCharMap.set(boxLeftPosition, WIDER_BOX_CHAR_RIGHT)
                    return true
                }
                return false
            }

            else -> throw IllegalStateException(
                "Unexpected char when moving box to EAST ${
                    widerCharMap.getOrNull(
                        westCharPosition
                    )
                }"
            )
        }
    }

    private fun moveWiderBoxVertically(
        direction: Direction,
        boxLeftPosition: Pair<Int, Int>,
        boxRightPosition: Pair<Int, Int>
    ): Boolean {
        val boxNewRightPosition = direction + boxRightPosition
        val boxNewLeftPosition = direction + boxLeftPosition
        val newRightChar = widerCharMap.getOrNull(boxNewRightPosition)
        val newLeftChar = widerCharMap.getOrNull(boxNewLeftPosition)
        if (newRightChar == FREE_CHAR && newLeftChar == FREE_CHAR) {
            widerCharMap.set(boxLeftPosition, FREE_CHAR)
            widerCharMap.set(boxRightPosition, FREE_CHAR)
            widerCharMap.set(boxNewRightPosition, WIDER_BOX_CHAR_RIGHT)
            widerCharMap.set(boxNewLeftPosition, WIDER_BOX_CHAR_LEFT)
            return true
        }
        if (newRightChar == WALL_CHAR || newLeftChar == WALL_CHAR) {
            return false
        }
        if (newRightChar == WIDER_BOX_CHAR_RIGHT) {
            if (moveWiderBox(boxNewLeftPosition, boxNewRightPosition, direction)) {
                widerCharMap.set(boxLeftPosition, FREE_CHAR)
                widerCharMap.set(boxRightPosition, FREE_CHAR)
                widerCharMap.set(boxNewRightPosition, WIDER_BOX_CHAR_RIGHT)
                widerCharMap.set(boxNewLeftPosition, WIDER_BOX_CHAR_LEFT)
                return true
            }
            return false
        }
        var canMove = true
        var moveRight = false
        if (newRightChar == WIDER_BOX_CHAR_LEFT) {
            moveRight = canMoveVertically(direction, boxNewRightPosition, EAST + boxNewRightPosition)
            canMove = moveRight
        }
        var moveLeft = false
        if (newLeftChar == WIDER_BOX_CHAR_RIGHT) {
            moveLeft = canMoveVertically(direction, WEST + boxNewLeftPosition, boxNewLeftPosition)
            canMove = canMove && moveLeft
        }
        if (canMove) {
            if (moveRight) {
                moveWiderBoxVertically(direction, boxNewRightPosition, EAST + boxNewRightPosition)
            }
            if (moveLeft) {
                moveWiderBoxVertically(direction, WEST + boxNewLeftPosition, boxNewLeftPosition)
            }
            widerCharMap.set(boxLeftPosition, FREE_CHAR)
            widerCharMap.set(boxRightPosition, FREE_CHAR)
            widerCharMap.set(boxNewRightPosition, WIDER_BOX_CHAR_RIGHT)
            widerCharMap.set(boxNewLeftPosition, WIDER_BOX_CHAR_LEFT)
        }
        return canMove
    }

    private fun canMoveVertically(
        direction: Direction,
        boxLeftPosition: Pair<Int, Int>,
        boxRightPosition: Pair<Int, Int>
    ): Boolean {
        val boxNewRightPosition = direction + boxRightPosition
        val boxNewLeftPosition = direction + boxLeftPosition
        val newRightChar = widerCharMap.getOrNull(boxNewRightPosition)
        val newLeftChar = widerCharMap.getOrNull(boxNewLeftPosition)
        if (newRightChar == FREE_CHAR && newLeftChar == FREE_CHAR) {
            return true
        }
        if (newRightChar == WALL_CHAR || newLeftChar == WALL_CHAR) {
            return false
        }
        if (newRightChar == WIDER_BOX_CHAR_RIGHT) {
            return canMoveVertically(direction, boxNewLeftPosition, boxNewRightPosition)
        }
        var canMove = true
        if (newRightChar == WIDER_BOX_CHAR_LEFT) {
            canMove = canMove && canMoveVertically(direction, boxNewRightPosition, EAST + boxNewRightPosition)
        }
        if (newLeftChar == WIDER_BOX_CHAR_RIGHT) {
            canMove = canMove && canMoveVertically(direction, WEST + boxNewLeftPosition, boxNewLeftPosition)
        }
        return canMove
    }

    private fun extractMapLines(lines: List<String>) = lines.filter { it.contains(Regex(MAP_PATTERN)) }
    private fun extractWiderMapLines(lines: List<String>): List<String> = lines
        .filter { it.contains(Regex(MAP_PATTERN)) }
        .map {
            it
                .replace(".", "..")
                .replace("O", "[]")
                .replace("#", "##")
                .replace("@", "@.")
        }


    private fun extractInstructions(lines: List<String>) =
        lines
            .filter { it.contains(Regex(INSTRUCTION_PATTERN)) }
            .joinToString("")
            .asSequence()
            .map {
                when (it) {
                    '>' -> EAST
                    '<' -> WEST
                    '^' -> NORTH
                    'v' -> SOUTH
                    else -> throw IllegalStateException("Unknown instruction $it")
                }
            }.toList()

    private fun findRobot(charMap: Map2d<Char>): Pair<Int, Int> {
        return charMap.find { (_, char) -> char == ROBOT_CHAR }?.first!!
    }

    fun startVisualMode() {
        val config = LwjglApplicationConfiguration().apply {
            LwjglApplicationConfiguration.getDesktopDisplayMode()
            width = 8 * 120
            height = 8 * 60
            fullscreen = false
            resizable = false
        }
        config.title = "Day 15"
        LwjglApplication(Day15Application(this), config);
    }

}