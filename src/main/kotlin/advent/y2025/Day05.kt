package advent.y2025

import advent.util.readAllLines
import kotlin.math.max

fun main() {
    val allLines = readAllLines("2025/day5_input.txt")
    val day05 = Day05(allLines)
    println("Number of fresh ingredients: ${day05.getFreshIngredients().size}")
    println("Number of total fresh ingredients: ${day05.getTotalNumberFreshIngredients()}")
}

class Day05(lines: List<String>) {

    val ranges = lines
        .takeWhile { it.contains("-") }
        .map { it.split("-") }
        .map { it[0].toLong()..it[1].toLong() }

    val ingredients = lines.filter { !it.isEmpty() && !it.contains("-") }.map { it.toLong() }

    fun getFreshIngredients(): List<Long> {
        return ingredients.filter { ingredient -> ranges.any { range -> range.contains(ingredient) } }
    }

    fun getTotalNumberFreshIngredients(): Long {
        val simplifiedRanges = mutableListOf<LongRange>()
        ranges.sortedBy { it.start }.forEach { range ->
            if (simplifiedRanges.isEmpty()) {
                simplifiedRanges.add(range)
            } else {
                if (doesIntersect(range, simplifiedRanges.last())) {
                    val last = simplifiedRanges.removeLast()
                    simplifiedRanges.add(last.start..max(last.endInclusive, range.endInclusive))
                } else {
                    simplifiedRanges.add(range)
                }
            }
        }
        return simplifiedRanges.sumOf { it.endInclusive - it.start + 1 }
    }

    private fun doesIntersect(
        range1: LongRange,
        range2: LongRange,
    ): Boolean = range1.start <= range2.endInclusive

}

