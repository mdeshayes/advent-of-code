package advent.y2024

import advent.util.Day14Application
import advent.util.readAllLines
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import java.lang.Math.floorMod


private const val VISUAL_MODE = false
private const val ROBOT_PATTERN = "p=(.*),(.*) v=(.*),(.*)"

private const val HEIGHT = 103
private const val WIDTH = 101

fun main() {
    val lines = readAllLines("2024/day14_input.txt")
    var day14 = Day14(lines, HEIGHT, WIDTH)
    day14.moveRobots(100)
    println("Part 1: Safety score is ${day14.computeSafetyFactor()} after 100s")
    day14 = Day14(lines, HEIGHT, WIDTH)
    if (VISUAL_MODE) {
        day14.startVisualMode()
    } else {
        val durationForChristmasTree = day14.findChristmasTree()
        println("Part 2: Christmas tree found after ${durationForChristmasTree}s")
    }
}

class Day14(lines: List<String>, private val nbLines: Int, private val nbColumns: Int) {

    internal val robots: List<Robot> = lines.map { buildRobot(it) }
    private val initialPosition = getCurrentPosition().toMap()

    private fun getInitialPosition() = initialPosition
    private fun getCurrentPosition() = robots.associateBy({ it }, { it.position })

    fun moveRobots(times: Int) = robots.forEach { moveRobot(it, times) }

    fun computeSafetyFactor() = robots.count { inFirstQuadrant(it) } *
            robots.count { inSecondQuadrant(it) } *
            robots.count { inThirdQuadrant(it) } *
            robots.count { inFourthQuadrant(it) }

    fun findChristmasTree(): Int {
        moveRobots(1)
        val safetyFactors = mutableListOf(computeSafetyFactor())
        while (!isInInitialPosition()) {
            moveRobots(1)
            safetyFactors.add(computeSafetyFactor())
        }
        return safetyFactors.indexOf(safetyFactors.min()) + 1
    }

    private fun inFirstQuadrant(it: Robot) = it.position.first < nbColumns / 2 && it.position.second < nbLines / 2
    private fun inSecondQuadrant(it: Robot) = it.position.first > nbColumns / 2 && it.position.second < nbLines / 2
    private fun inThirdQuadrant(it: Robot) = it.position.first < nbColumns / 2 && it.position.second > nbLines / 2
    private fun inFourthQuadrant(it: Robot) = it.position.first > nbColumns / 2 && it.position.second > nbLines / 2

    private fun moveRobot(robot: Robot, times: Int) {
        val (x, y) = robot.position
        val (xSpeed, ySpeed) = robot.velocity
        robot.position = floorMod(x + xSpeed * times, nbColumns) to floorMod(y + ySpeed * times + nbLines, nbLines)
    }

    private fun isInInitialPosition() = positionsAreEquals(getCurrentPosition(), getInitialPosition())

    private fun positionsAreEquals(
        currentPosition: Map<Robot, Pair<Int, Int>>,
        initialPosition: Map<Robot, Pair<Int, Int>>
    ) = currentPosition.all { (robot, position) -> initialPosition[robot] == position }

    private fun buildRobot(line: String): Robot {
        return buildRobot(Regex(ROBOT_PATTERN).find(line)!!.groupValues.drop(1).map { it.toInt() })
    }

    private fun buildRobot(parameters: List<Int>) =
        Robot(
            position = parameters[0] to parameters[1],
            velocity = parameters[2] to parameters[3]
        )

    fun startVisualMode() {
        val config = LwjglApplicationConfiguration().apply {
            LwjglApplicationConfiguration.getDesktopDisplayMode()
            width = 8 * 110
            height = 8 * 110
            fullscreen = false
            resizable = false
        }
        config.title = "Day 14"
        LwjglApplication(Day14Application(this), config);
    }

    data class Robot(var position: Pair<Int, Int>, val velocity: Pair<Int, Int>)

}