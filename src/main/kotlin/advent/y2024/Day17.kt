package advent.y2024

import advent.util.readAllLines
import java.lang.Long.parseLong
import kotlin.math.floor
import kotlin.math.pow


fun main() {
    val lines = readAllLines("2024/day17_input.txt")
    val day17 = Day17(lines)
    println("Part1: Output of the program is ${day17.executeProgram().joinToString(",")}")
    println("Part2: register A value is ${day17.getRegisterAValueToMatchOutputAndProgram()}")
}

class Day17(lines: List<String>) {

    private var registerA: Long = lines[0].split(":").map { it.trim() }.last().toLong()
    private var registerB: Long = lines[1].split(":").map { it.trim() }.last().toLong()
    private var registerC: Long = lines[2].split(":").map { it.trim() }.last().toLong()
    private var instructionPointer = 0
    private val program: List<Int> =
        lines[4].split(":").map { it.trim() }.last().asSequence().filterNot { it == ',' }.map { it.digitToInt() }
            .toList()

    fun executeProgram(): List<Int> {
        val outputs = mutableListOf<Int>()
        while (instructionPointer < program.size) {
            val opcode = program[instructionPointer]
            val operand = program[instructionPointer + 1]
            executeAndMovePointer(opcode, operand.toLong(), outputs)
        }
        return outputs
    }

    private fun executeAndMovePointer(opcode: Int, operand: Long, outputs: MutableList<Int>) {
        when (opcode) {
            0 -> registerA /= floor(2.0.pow(combo(operand).toInt())).toInt()
            1 -> registerB = registerB xor operand
            2 -> registerB = Math.floorMod(combo(operand).toInt(), 8).toLong()
            3 -> if (registerA != 0L) {
                instructionPointer = operand.toInt()
                return
            }

            4 -> registerB = registerB xor registerC
            5 -> outputs.add(Math.floorMod(combo(operand).toInt(), 8))
            6 -> registerB = registerA / floor(2.0.pow(combo(operand).toInt())).toLong()
            7 -> registerC = registerA / floor(2.0.pow(combo(operand).toInt())).toLong()
        }
        instructionPointer += 2
    }

    private fun combo(operand: Long): Long = when (operand) {
        0L, 1L, 2L, 3L -> operand
        4L -> registerA
        5L -> registerB
        6L -> registerC
        else -> throw IllegalArgumentException("Illegal combo operation $operand")
    }

    private fun executeOne(value: Long): List<Int> {
        registerA = value
        registerB = 0
        registerC = 0
        instructionPointer = 0
        return executeProgram()
    }

    private fun findANearValueForRegisterA(): Long {
        val octalDigits = getFeelingNumber().asSequence().map { it.digitToInt() }.toMutableList()
        for (digit in octalDigits.indices) {
            for (value in 2..7) {
                octalDigits[digit] = value
                registerA = parseLong(octalDigits.joinToString(""), 8)
                registerB = 0
                registerC = 0
                instructionPointer = 0
                val outputs = executeProgram()
                if (outputs[octalDigits.size - digit - 1] == program[octalDigits.size - digit - 1]) {
                    break
                }
            }
        }
        return parseLong(octalDigits.joinToString(""), 8)
    }

    private fun getFeelingNumber() = "5322072653740007" // \_(ツ)_/¯

    fun getRegisterAValueToMatchOutputAndProgram(): Long {
        for (i in 0..100000) {
            val value = findANearValueForRegisterA() - 50000 + i
            val executeOne = executeOne(value)
            if (executeOne == program) {
                return value
            }
        }
        throw IllegalStateException("No value found for register A so output == program")
    }
}
