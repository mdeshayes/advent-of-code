package advent.y2025

import advent.util.readAllLines
import java.lang.Math.floorMod
import kotlin.math.abs

fun main() {
    val allLines = readAllLines("2025/day1_input.txt")
    val day01 = Day01(allLines)
    println("Part1: The code is ${day01.getPassword()}")
    println("Part1: The 0x434C49434B code is ${day01.get0x434C49434BPassword()}")
}

data class PositionAndCounter(val position: Int, val counter: Int = 0)

class Day01(lines: List<String>) {

    private val rotations = lines.map {
        if (it[0] == 'L') {
            -it.substring(1).toInt()
        } else {
            it.substring(1).toInt()
        }
    }

    fun getPassword() = rotations.fold(PositionAndCounter(50)) { (position, counter), rotation ->
        PositionAndCounter(
            position = floorMod(position + rotation, 100),
            counter = if (isPointingToZero(position, rotation)) counter + 1 else counter
        )
    }.counter

    fun get0x434C49434BPassword() = rotations.fold(PositionAndCounter(50)) { (position, counter), rotation ->
        PositionAndCounter(
            position = floorMod(position + rotation, 100),
            counter = counter + countNumberOfPassViaZero(position, rotation)
        )
    }.counter

    private fun isPointingToZero(position: Int, rotation: Int): Boolean = floorMod(position + rotation, 100) == 0

    private fun countNumberOfPassViaZero(position: Int, rotation: Int): Int {
        val target = position + rotation % 100
        val nbFullRotations = abs(rotation) / 100
        val additionalRotation = target !in 1..99 && position != 0
        return nbFullRotations + if (additionalRotation) 1 else 0
    }
}

