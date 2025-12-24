package advent.y2024

import advent.util.readAllLines


fun main() {
    val lines = readAllLines("2024/day19_input.txt")
    val day19 = Day19(lines)
    println("Part1: ${day19.countPossibleDesign()} possible designs found")
    println("Part2: ${day19.countPossibleCombinations()} possible combinations found")
}

class Day19(lines: List<String>) {

    private val availableTowels = lines[0].split(",").map { it.trim() }
    private val designsToDisplay = lines.subList(2, lines.size)
    private val memory = mutableMapOf<String, Long>()

    fun countPossibleDesign() = designsToDisplay.count {
        isPossible(it)
    }

    fun countPossibleCombinations() = designsToDisplay.sumOf {
        countPossibleCombinations(it)
    }

    private fun countPossibleCombinations(design: String): Long {
        if (design.isEmpty()) {
            return 1L
        }
        if (memory[design] != null) {
            return memory[design]!!
        }
        return availableTowels.map {
            if (design.startsWith(it)) {
                val subDesign = design.substring(it.length)
                val possibleCombinations = countPossibleCombinations(subDesign)
                memory[subDesign] = possibleCombinations
                return@map possibleCombinations
            }
            return@map 0L
        }.sum()
    }

    private fun isPossible(design: String): Boolean {
        if (design.isEmpty()) {
            return true
        }
        availableTowels.forEach {
            if (design.startsWith(it)) {
                if (isPossible(design.substring(it.length))) {
                    return true
                }
            }
        }
        return false
    }

}