package advent.y2024

import advent.util.readAllLines
import kotlin.math.abs

fun main() {
    val lines = readAllLines("2024/day2_input.txt")
    val day02 = Day02(lines)
    println("Part1: ${day02.countIsSafe()} reports are safe")
    println("Part2: ${day02.countIsSafeIfWeRemoveOne()} reports could be safe if we remove one number")
}

class Day02(lines: List<String>) {

    private val reports = lines.map { it.split(" ").map(String::toInt).toMutableList() }

    fun countIsSafe(): Int = reports.count { isSafe(it) }
    fun countIsSafeIfWeRemoveOne() = reports.count { isSafeIfWeRemoveOne(it) }

    private fun isSafe(numbers: List<Int>): Boolean {
        var isAscending: Boolean? = null
        for (i in 1 until numbers.size) {
            if (isGapToImportant(numbers[i], numbers[i - 1])) return false
            if (isAscending == null) {
                isAscending = numbers[i] > numbers[i - 1]
            }
            if (isAscending && numbers[i] <= numbers[i - 1]) {
                return false
            }
            if (!isAscending && numbers[i] >= numbers[i - 1]) {
                return false
            }
        }
        return true
    }

    private fun isSafeIfWeRemoveOne(numbers: MutableList<Int>): Boolean {
        if (isSafe(numbers)) {
            return true
        }
        for (i in 0 until numbers.size) {
            val listToBeTested = numbers.toMutableList()
            listToBeTested.removeAt(i)
            if (isSafe(listToBeTested)) {
                return true
            }
        }
        return false
    }

    private fun isGapToImportant(number1: Int, number2: Int) = abs(number1 - number2) > 3
}

