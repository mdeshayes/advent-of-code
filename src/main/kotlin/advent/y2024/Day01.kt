package advent.y2024

import advent.util.readAllLines
import kotlin.math.abs

fun main() {
    val lines = readAllLines("2024/day1_input.txt")
    val day01 = Day01(lines)
    println("Part1: The distance between the two lists is ${day01.getDistance()}")
    println("Part2: The similarity between the two list is : ${day01.getSimilarity()}")
}

class Day01(lines: List<String>) {

    private val list1 = lines.map { extractInt(it, 0) }.sorted()
    private val list2 = lines.map { extractInt(it, 1) }.sorted()

    fun getDistance() = list1
        .mapIndexed { index, value -> computeDistance(value, list2[index]) }.sum()

    fun getSimilarity() = list1
        .sumOf { value1 -> value1 * list2.count { value2 -> value1 == value2 } }

    private fun computeDistance(value: Int, value2: Int) = abs(value - value2)

    private fun extractInt(it: String, position: Int) = (it.split("   ")[position]).toInt()
}

