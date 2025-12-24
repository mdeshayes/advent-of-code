package advent.util

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class EquationSystem(variables: List<String>, val matrix: List<List<Int>>) {

    var equations = matrix.map { buildEquation(it) }.toMutableList()
    var initialEquations = matrix.map { buildEquation(it) }.toMutableList()
    var variables = variables.toMutableList()
    var initialVariables = variables.toMutableList()
    val sumOfEquations = initialEquations.reduce { sumEq, eq -> sumEq + eq }
    fun getSumOfVariables(): Int {
        removeDuplicatedEquations()
        return findSumOfVariablesOrNull() ?: simplifySystemAndGetSumOfVariables()
    }

    fun simplifySystemAndGetSumOfVariables(): Int {
        val variableRelations = mutableListOf<VariableRelation>()
        while (equations.isNotEmpty() && equations.all(::isValidEquation)) {
            simplifySystem(variableRelations)
        }
        return findBestValues(variableRelations = variableRelations)
    }

    private fun isValidEquation(equation: Equation): Boolean = equation.coefficients.any { c -> c != 0.0 }

    private fun simplifySystem(variableRelations: MutableList<VariableRelation>) {
        val equationToEliminate = equations.minBy { it.result }
        val indexEquationToEliminate = equations.indexOf(equationToEliminate)
        val indexVariableToEliminate = equationToEliminate.coefficients.indexOfFirst { it != 0.0 }
        val variableToEliminateCoefficient = equationToEliminate.coefficients[indexVariableToEliminate]
        val otherVariablesCoefficients = equationToEliminate.coefficients.mapIndexedNotNull { index, coefficient ->
            if (index != indexVariableToEliminate && coefficient != 0.0) {
                return@mapIndexedNotNull variables[index] to -coefficient / variableToEliminateCoefficient
            }
            return@mapIndexedNotNull null
        }.toMap().toMutableMap()
        variableRelations.add(
            VariableRelation(
                name = variables[indexVariableToEliminate],
                coefficient = otherVariablesCoefficients,
                constant = equationToEliminate.result / variableToEliminateCoefficient
            )
        )
        equations = equations.map {
            simplifyEquation(
                equation = it,
                equationToEliminate = equationToEliminate,
                indexVariableToEliminate = indexVariableToEliminate,
                variableToEliminateCoefficient = variableToEliminateCoefficient
            )
        }.toMutableList()
        equations.removeAt(indexEquationToEliminate)
        variables.removeAt(indexVariableToEliminate)
        removeDuplicatedEquations()
    }

    private fun removeDuplicatedEquations() {
        equations = equations.toMutableSet().toMutableList()
    }

    private fun simplifyEquation(
        equation: Equation,
        equationToEliminate: Equation,
        indexVariableToEliminate: Int,
        variableToEliminateCoefficient: Double,
    ): Equation {
        if (equation.coefficients[indexVariableToEliminate] != 0.0) {
            val ratio = equation.coefficients[indexVariableToEliminate] / variableToEliminateCoefficient
            val newCoefficients = equation.coefficients
                .mapIndexed { index, c ->
                    c - ratio * equationToEliminate.coefficients[index]
                }.toMutableList()
            newCoefficients.removeAt(indexVariableToEliminate)
            val newResult = equation.result - equationToEliminate.result * ratio
            return Equation(newResult, newCoefficients)
        }
        val newEquation = equation
        val newCoefficient = newEquation.coefficients.toMutableList()
        newCoefficient.removeAt(indexVariableToEliminate)
        newEquation.coefficients = newCoefficient
        return newEquation
    }

    private fun isSolutionCorrect(
        freeVariablesValues: Map<String, Int>,
        variableRelations: List<VariableRelation>,
    ): Boolean {
        val variablesValues = solveVariableRelations(variableRelations, freeVariablesValues)
        if (variablesValues.values.any { it < 0 }) return false
        return initialEquations.mapIndexed { index, equation ->
            isEquationSatisfied(equation, variablesValues)
        }.all { it }
    }

    private fun isEquationSatisfied(
        equation: Equation,
        variablesValues: Map<String, Int>,
    ): Boolean = abs(equation.coefficients.mapIndexed { indexC, coeff ->
        coeff * variablesValues[initialVariables[indexC]]!!
    }.sum() - equation.result) < 0.1

    private fun solveVariableRelations(
        variableRelations: List<VariableRelation>,
        freeVariablesValues: Map<String, Int>,
    ): Map<String, Int> {
        val values = mutableMapOf<String, Int>()
        values.putAll(freeVariablesValues)
        val remainingVariableRelationsToSolve = variableRelations.toMutableList()
        while (remainingVariableRelationsToSolve.isNotEmpty()) {
            val variableRelation = findSolvableRelation(remainingVariableRelationsToSolve, values)
            values[variableRelation.name] = solveRelation(variableRelation, values).roundToInt()
            remainingVariableRelationsToSolve.remove(variableRelation)
        }
        return values
    }

    private fun findSolvableRelation(
        remainingVariableRelations: MutableList<VariableRelation>,
        values: MutableMap<String, Int>,
    ): VariableRelation = remainingVariableRelations
        .first { it.coefficient.keys.all { name -> values.contains(name) } }

    private fun solveRelation(
        variableRelation: VariableRelation,
        values: MutableMap<String, Int>,
    ): Double =
        variableRelation.coefficient.entries.sumOf { (name, coefficient) -> values[name]!! * coefficient } + variableRelation.constant

    private fun findSumOfVariablesOrNull(): Int? {
        if (variables.size > 1) {
            for (i in 0 until equations.size) {
                for (j in 0 until equations.size) {
                    if (i != j) {
                        val rowI = equations[i]
                        val rowJ = equations[j]
                        if (areEquationsComplementary(rowI, rowJ)) {
                            return (rowI.result + rowJ.result).toInt()
                        }
                    }
                }
            }
        }
        return null
    }

    private fun areEquationsComplementary(rowI: Equation, rowJ: Equation): Boolean {
        val comparison = rowI.coefficients.mapIndexed { index, coefficient ->
            coefficient + rowJ.coefficients[index] == 1.0
        }.all { it }
        return comparison
    }

    fun findBestValues(
        variableRelations: List<VariableRelation>,
    ): Int {
        val freeVariables = variables.toMutableList()
        if (freeVariables.isEmpty()) return solveVariableRelations(variableRelations, mutableMapOf()).values.sum()
        val maxValues = getMaxValuesByVariable(freeVariables)
        val initialFreeVariablesValues = freeVariables.associateWith { 0 }
        val startTime = System.currentTimeMillis()
        val openList = mutableListOf(Solution(initialFreeVariablesValues))
        openList.forEach { it.cost = getCost(it, variableRelations) }
        val closedList = mutableSetOf<Solution>()
        val solutions = mutableSetOf<Solution>()
        var exploredSolution = 0L
        while (openList.isNotEmpty()) {
            val current = openList.minByOrNull { it.cost } ?: break
            if (isSolutionCorrect(current.freeVariableValues, variableRelations)) {
                current.cost = getCost(current, variableRelations)
                solutions.add(current)
            }
            closedList.add(current)
            openList.remove(current)
            for (neighbor in getNeighbors(current, maxValues)) {
                if (neighbor in closedList) {
                    continue
                }
                exploredSolution++
                if (exploredSolution % 1000 == 0L) {
                    if ((System.currentTimeMillis() - startTime) / 1000 > 2) return solutions.minOfOrNull { it.cost }
                        ?: Int.MAX_VALUE
                }
                if (current.cost <= neighbor.cost) {
                    neighbor.cost = getDistance(neighbor, variableRelations)
                    if (neighbor !in openList) {
                        openList.add(neighbor)
                    }
                }
            }
        }
        if (solutions.isEmpty()) return Int.MAX_VALUE
        return solutions.minOf { it.cost }
    }

    private fun getMaxValuesByVariable(freeVariables: MutableList<String>): Map<String, Double> =
        freeVariables.associateWith {
            val coefficient = sumOfEquations.coefficients[initialVariables.indexOf(it)]
            min(sumOfEquations.result / coefficient + 1, initialEquations.maxOf { e -> e.result })
        }

    private fun getDistance(solution: Solution, variableRelations: List<VariableRelation>): Int {
        val freeVariableValues = solution.freeVariableValues
        val variablesValues = solveVariableRelations(variableRelations, freeVariableValues)
        val distance = initialEquations.mapIndexed { index, equation ->
            abs(equation.coefficients.mapIndexed { indexC, coeff ->
                coeff * variablesValues[initialVariables[indexC]]!!
            }.sum() - equation.result)
        }.sum()
        val penaltiesForNegativeValues = variablesValues.filterValues { it < 0 }.values.sum() * -100.0
        return (distance + penaltiesForNegativeValues).toInt()
    }

    private fun getCost(solution: Solution, variableRelations: List<VariableRelation>): Int {
        val freeVariableValues = solution.freeVariableValues
        val variablesValues = solveVariableRelations(variableRelations, freeVariableValues)
        return variablesValues.values.sum()
    }

    private fun getNeighbors(
        current: Solution,
        limit: Map<String, Double>,
    ): List<Solution> {
        return current.freeVariableValues.mapNotNull { (name, value) ->
            val newVariableValuesPlus = current.freeVariableValues.toMutableMap()
            newVariableValuesPlus[name] = value + 1
            if (value + 1 > limit[name]!!) {
                return@mapNotNull null
            }
            return@mapNotNull Solution(newVariableValuesPlus)
        }
    }

    override fun toString(): String {
        return equations.map { it.coefficients + " = " + it.result }.joinToString("\n")
    }

    private fun buildEquation(row: List<Int>): Equation {
        return Equation(row.last().toDouble(), row.subList(0, row.size - 1).map { it.toDouble() })
    }

    data class VariableRelation(val name: String, val coefficient: MutableMap<String, Double>, var constant: Double) {
        override fun toString(): String {
            return "$name = ${
                coefficient.map { (name, value) -> "$value * $name" }.joinToString("+")
            } ${if (constant > 0) "+ $constant" else "- $constant"}"
        }
    }

    data class Equation(val result: Double, var coefficients: List<Double>) {
        operator fun plus(equation: Equation): Equation {
            val sumCoeff = this.coefficients.mapIndexed { index, value -> equation.coefficients[index] + value }
            val sumResult = this.result + equation.result
            return Equation(sumResult, sumCoeff)
        }

        operator fun minus(equation: Equation): Equation {
            val sumCoeff = this.coefficients.mapIndexed { index, value -> value - equation.coefficients[index] }
            val sumResult = this.result - equation.result
            return Equation(sumResult, sumCoeff)
        }

        override fun toString(): String {
            return "${coefficients.joinToString(" ")} = $result"
        }

    }


    data class Solution(
        var freeVariableValues: Map<String, Int>,
        var cost: Int = Int.MAX_VALUE,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Solution) return false
            return freeVariableValues == other.freeVariableValues
        }

        override fun hashCode(): Int {
            return freeVariableValues.hashCode()
        }

        override fun toString(): String {
            return freeVariableValues.toString()
        }
    }

}