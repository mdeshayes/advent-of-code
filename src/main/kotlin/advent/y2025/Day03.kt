package advent.y2025

import advent.util.readAllLines

fun main() {
    val lines = readAllLines("2025/day3_input.txt")
    val day03 = Day03(lines)
    println("Maximum Joltage = ${day03.getTotalMaximumJoltage()}")
    println("Maximum Joltage without safety = ${day03.getTotalMaximumJoltageWithoutSafety()}")
}

class Day03(lines: List<String>) {

    val banks = lines.map { line -> line.map { digit -> digit.digitToInt() } }

    fun getTotalMaximumJoltage() = banks.sumOf {
        getMaximumJoltage(it)
    }

    fun getTotalMaximumJoltageWithoutSafety(): Long = banks.sumOf { getMaximumJoltage(it, 12) }

    private fun getMaximumJoltage(bank: List<Int>, nbDigits: Int = 2) =
        getMaximumJoltageAsString(bank, nbDigits).toLong()

    private fun getMaximumJoltageAsString(bank: List<Int>, nbDigits: Int): String {
        if (nbDigits == 0) return ""
        val firstDigit = bank.take(bank.size - (nbDigits - 1)).max()
        val firstDigitPosition = bank.indexOf(firstDigit)
        return firstDigit.toString() + getMaximumJoltageAsString(
            bank.subList(firstDigitPosition + 1, bank.size),
            nbDigits - 1
        )
    }
}

