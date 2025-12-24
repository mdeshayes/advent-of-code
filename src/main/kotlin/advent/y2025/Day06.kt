package advent.y2025

import advent.util.Map2d
import advent.util.readAllLines

fun main() {
    val allLines = readAllLines("2025/day6_input.txt")
    val day06 = Day06(allLines)
    println("Sum of problem solutions: ${day06.getSumOfProblemSolutions()}")
    println("Sum of right-to-left problem solutions: ${day06.getSumOfRightToLeftProblemSolutions()}")
}

class Day06(lines: List<String>) {

    var problems: MutableList<Pair<List<Long>, Char>> = mutableListOf()
    val inputData: Map2d<Char> = Map2d(lines) { it }

    init {
        val operands = mutableListOf<MutableList<Long>>()
        val operators = mutableListOf<Char>()
        lines.forEach { line ->
            if (!line.contains("+")) {
                val lineOperands = line.split(" ").filterNot { it.isEmpty() }.map { it.toLong() }
                lineOperands.forEachIndexed { index, operand ->
                    if (operands.size <= index) {
                        operands.add(mutableListOf())
                    }
                    operands[index].add(operand)
                }
            } else {
                line.split(" ").filterNot { it.isEmpty() }.forEach { operators.add(it[0]) }
            }
        }
        operands.forEachIndexed { index, problemOperand ->
            problems.add(operands[index] to operators[index])
        }
    }

    fun getSumOfProblemSolutions(): Long {
        return problems.sumOf { getProblemSolution(it) }
    }

    private fun getProblemSolution(problem: Pair<List<Long>, Char>): Long {
        val (operands, operator) = problem
        if (operator == '+') return operands.reduce(Long::plus)
        if (operator == '*') return operands.reduce(Long::times)
        throw Exception("Unknown operator '$operator'")
    }

    fun getSumOfRightToLeftProblemSolutions(): Long {
        val emptyColumnsIndexes = (0..inputData.getWidth() - 1).filter { index ->
            (0..inputData.getHeight() - 1).all { inputData.get(index, it) == ' ' }
        } + inputData.getWidth()
        var previousEmptyColumnIndex = -1
        var sum = 0L
        emptyColumnsIndexes.forEach { index ->
            var operator = ""
            val operands = mutableListOf<Long>()
            (index - 1 downTo previousEmptyColumnIndex + 1).forEach { columnIndex ->
                val operand =
                    (0..inputData.getHeight() - 2)
                        .map { inputData.get(columnIndex, it) }
                        .joinToString(separator = "")
                        .trim()
                        .toLong()
                operator += inputData.get(columnIndex, inputData.getHeight() - 1)
                operands.add(operand)
            }
            sum += if (operator.trim() == "+") {
                operands.reduce(Long::plus)
            } else {
                operands.reduce(Long::times)
            }
            previousEmptyColumnIndex = index
        }
        return sum
    }

}



