package advent.y2024

import advent.util.readAllLines

fun main() {
    val lines = readAllLines("2024/day7_input.txt")
    val day07 = Day07(lines)
    println("Part 1: Sum of possible equations is ${day07.sumPossibleEquations()}")
    day07.enableConcatOperand()
    println("Part 2: Sum of possible equations with concat is ${day07.sumPossibleEquations()}")
}

class Day07(lines: List<String>) {

    private val equations = lines.map { parseEquation(it) }
    private var concatOperandEnable = false

    fun enableConcatOperand() {
        concatOperandEnable = true
    }

    fun sumPossibleEquations() = equations.filter { it.isPossible(concatOperandEnable) }.sumOf { it.result }

    private fun parseEquation(line: String): Equation {
        val equationMembers = line.split(": ")
        val result = equationMembers[0].toLong()
        val operand = equationMembers[1].split(" ").map { it.toLong() }
        return Equation(result, operand)
    }

    private class Equation(val result: Long, private val operands: List<Long>) {

        fun isPossible(concatOperandEnable: Boolean): Boolean {
            if (operands.size == 1) {
                return operands.single() == result
            }
            val equationWithPlus = Equation(result, reduceOperands(Long::plus, operands))
            val equationWithTime = Equation(result, reduceOperands(Long::times, operands))
            val equationWithConcat = Equation(result, reduceOperands(::concatOperands, operands))
            return equationWithTime.isPossible(concatOperandEnable)
                    || equationWithPlus.isPossible(concatOperandEnable)
                    || (concatOperandEnable && equationWithConcat.isPossible(true))
        }

        private fun reduceOperands(operation: (Long, Long) -> Long, operands: List<Long>) =
            listOf(operation(operands[0], operands[1])) + operands.subList(2, operands.size)

        private fun concatOperands(operand1: Long, operand2: Long) =
            (operand1.toString() + operand2.toString()).toLong()
    }
}

