package advent.y2024

import advent.util.readAllLines

const val MUL_REGEX = "mul\\(([0-9]+),([0-9]+)\\)"

fun main() {
    val lines = readAllLines("2024/day3_input.txt")
    val day03 = Day03(lines)
    println("Part1: " + day03.computeMul())
    println("Part2: " + day03.computeMulWithoutDontBlocks())
}

class Day03(lines: List<String>) {

    private val instructions = lines.joinToString()
    private val instructionsWithoutDontBlocks = (instructions + "do()").replace(Regex("don't\\(\\).*?do\\(\\)"), "")

    fun computeMulWithoutDontBlocks() = compute(instructionsWithoutDontBlocks)
    fun computeMul() = compute(instructions)

    private fun compute(instructions: String) =
        Regex(MUL_REGEX)
            .findAll(instructions)
            .sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }

}
