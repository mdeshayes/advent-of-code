package advent.y2025

import advent.util.EquationSystem
import advent.util.readAllLines
import me.tongfei.progressbar.ProgressBar

data class Machine(val lights: MutableList<Boolean>, val buttons: List<List<Int>>, val joltages: List<Int>)

const val regex: String = "\\[(.*)](.*)\\{(.*)}"
const val limit: Long = 7L


fun main() {
    val allLines1 = readAllLines("2025/day10_input.txt")
    val allLines = allLines1
    val day10 = Day10(allLines)
    println("Fewest button pressed: ${day10.getFewestButtonsPressed()}")
    println("Fewest button pressed for joltages: ${day10.getFewestButtonsPressedForJoltages()}")
}

class Day10(lines: List<String>) {

    private var bestFound = limit

    private val cache = mutableMapOf<List<Int>, Int>()

    private val machines = lines.map {
        val matches = Regex(regex).matchEntire(it)
        val lights = matches!!.groupValues[1].map { light -> light == '#' }.toMutableList()
        val buttons: List<List<Int>> = matches.groupValues[2].trim().split(" ").map { button ->
            button.replace("(", "").replace(")", "").split(",").map(String::toInt)
        }
        val joltages = matches.groupValues[3].split(",").map(String::toInt)
        Machine(lights, buttons, joltages)
    }

    fun getFewestButtonsPressed(): Long {
        val progressBar = ProgressBar("Machines", machines.size.toLong())
        return machines.sumOf {
            bestFound = limit
            getFewestButtonsPressed(it, it.lights.map { false }, 0).also { progressBar.step() }
        }
    }

    private fun getFewestButtonsPressed(
        machine: Machine,
        lights: List<Boolean>,
        currentCount: Long,
    ): Long {
        if (machine.lights == lights) {
            if (currentCount < bestFound) {
                bestFound = currentCount
            }
            return currentCount
        }
        if (currentCount > bestFound) {
            return Long.MAX_VALUE
        }
        return machine.buttons.minOf {
            getFewestButtonsPressed(
                machine,
                applyButton(lights, it),
                currentCount + 1
            )
        }
    }

    private fun applyButton(
        lights: List<Boolean>,
        button: List<Int>,
    ): List<Boolean> {
        val newLights = lights.toMutableList()
        button.forEach { newLights[it] = !newLights[it] }
        return newLights
    }

    fun getFewestButtonsPressedForJoltages(): Int {
        val progressBar = ProgressBar("Find buttons for Joltages", machines.size.toLong())
        return machines.sumOf { machine ->
            val matrix = getMatrix(machine)
            matrix.reduce { sumVector, vector ->
                sumVector.mapIndexed { index, value -> value + vector[index] }.toMutableList()
            }
            val system = EquationSystem(machine.buttons.mapIndexed { index, _ -> "button$index" }, matrix)
            system.getSumOfVariables().also { progressBar.step() }
        }
    }

    private fun getMatrix(machine: Machine): MutableList<MutableList<Int>> {
        val matrix = (0 until machine.joltages.size).map { rowIndex ->
            val row = machine.buttons.map { if (it.contains(rowIndex)) 1 else 0 }.toMutableList()
            row.add(machine.joltages[rowIndex])
            row
        }.toMutableList()
        return matrix
    }

}