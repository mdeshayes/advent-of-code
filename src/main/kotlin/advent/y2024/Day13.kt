package advent.y2024

import advent.util.readAllLines
import kotlin.math.abs

private const val BUTTON_REGEX = "Button [A-B]: X\\+([0-9]+), Y\\+([0-9]+)"
private const val PRIZE_REGEX = "Prize: X=([0-9]+), Y=([0-9]+)"

fun main() {
    val lines = readAllLines("2024/day13_input.txt")
    val day13 = Day13(lines)
    println("Part 1: Total cost is ${day13.getCost()} tokens")
    println("Part 2: Total cost after shifting is ${day13.getShiftedCost()} tokens")
}

class Day13(lines: List<String>) {

    private val machines = lines
        .filterNot { it.isBlank() }
        .chunked(3)
        .map { lines ->
            val aButtonLine = extractValues(lines[0], BUTTON_REGEX)
            val bButtonLine = extractValues(lines[1], BUTTON_REGEX)
            val prizeLine = extractValues(lines[2], PRIZE_REGEX)
            Machine(aButtonLine, bButtonLine, prizeLine)
        }

    private fun extractValues(line: String, regex: String): Pair<Long, Long> {
        val values = Regex(regex).find(line)!!.groupValues.drop(1).map { it.toLong() }
        return values[0] to values[1]
    }

    private val machinesShifted = machines.map {
        Machine(
            it.aButton,
            it.bButton,
            it.prize.first + 10000000000000 to it.prize.second + 10000000000000
        )
    }

    fun getCost() = getCost(machines)

    fun getShiftedCost() = getCost(machinesShifted)

    private fun getCost(machines: List<Machine>) =
        machines.sumOf { solution -> solution.getAllSolutions().minOfOrNull { it.first * 3 + it.second } ?: 0 }

    class Machine(val aButton: Pair<Long, Long>, val bButton: Pair<Long, Long>, val prize: Pair<Long, Long>) {

        fun getAllSolutions(): List<Pair<Long, Long>> {
            val (xP, yP) = prize
            val (xA, yA) = aButton
            val (xB, yB) = bButton
            val a = (yB.toDouble() * xP - xB * yP) / (xA.toDouble() * yB - yA * xB)
            val b = (xA.toDouble() * yP - yA * xP) / (xA.toDouble() * yB - yA * xB)
            if (isValid(a, b)) {
                return listOf(a.toLong() to b.toLong())
            }
            return emptyList()
        }

        private fun isValid(a: Double, b: Double): Boolean {
            return abs(a.toLong() - a) < 0.00001 && abs(b.toLong() - b) < 0.00001 && a > 0 && b > 0
        }
    }
}