package advent.y2024

import advent.util.readAllLines
import java.util.Comparator

fun main() {
    val lines = readAllLines("2024/day5_input.txt")
    val day05 = Day05(lines)
    println("Part1: There are ${day05.getPossibleUpdateScore()} possible updates")
    println("Part2: There are ${day05.getUpdatesToFixScore()} updates to fix")
}

class Day05(lines: List<String>) {

    private val linesByType = lines.groupBy { if (it.contains(",")) "update" else "rules" }
    private val rules = linesByType["rules"]!!
        .filter { it.isNotBlank() }
        .map { it.split("|")[0].toInt() to it.split("|")[1].toInt() }
    private val updates = linesByType["update"]!!
        .map { it.split(",") }
        .map { it.map { string -> string.toInt() } }
    private val ruleComparator = RulesComparator(rules)

    fun getPossibleUpdateScore(): Int = getPossibleUpdates().sumOf { getMiddleNumber(it) }

    fun getUpdatesToFixScore(): Int = updates
        .filterNot { isUpdatePossible(it) }
        .map { fixUpdate(it) }
        .sumOf { getMiddleNumber(it) }

    private fun getPossibleUpdates(): List<List<Int>> = updates.filter { isUpdatePossible(it) }

    private fun getMiddleNumber(it: List<Int>) = it[(it.size - 1) / 2]

    private fun isUpdatePossible(update: List<Int>): Boolean = fixUpdate(update) == update

    private fun fixUpdate(update: List<Int>) = update.toMutableList().sortedWith(ruleComparator)

    private class RulesComparator(private val rules: List<Pair<Int, Int>>) : Comparator<Int> {
        override fun compare(a: Int, b: Int) = when {
            rules.contains(a to b) -> -1
            rules.contains(b to a) -> 1
            else -> 0
        }
    }
}

