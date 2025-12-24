package advent.y2024

import advent.util.readAllLines

fun main() {
    val lines = readAllLines("2024/day24_input.txt")
    val day24 = Day24(lines)
    println("Part1: Z number is ${day24.getZNumber()}")
    day24.expandEquationsAndCheckSwaps()
    println("Part2: correct swaps are ${day24.getCorrectSwapsSorted()}")
}

class Day24(private val lines: List<String>) {

    private var registers: MutableMap<String, Boolean?> = mutableMapOf()
    private var operations: MutableList<Operation> = mutableListOf()

    init {
        reset()
    }

    private fun reset() {
        registers = lines
            .filterNot { it.isEmpty() }
            .map { line ->
                if (line.contains(":")) {
                    return@map line.split(":")[0].trim() to (line.split(":")[1].trim() == "1")
                }
                return@map line.split("->")[1].trim() to null
            }.toMap().toMutableMap()
        operations = lines
            .filter { it.contains("->") }
            .map { line ->
                val firstPart = line.split("->")[0].split(" ").map { it.trim() }
                val resultRegister = line.split("->")[1].trim()
                val operand1 = firstPart[0].trim()
                val operator = getOperator(firstPart[1])
                val operand2 = firstPart[2].trim()
                Operation(SingleValueOperand(operand1), SingleValueOperand(operand2), resultRegister, operator)
            }.toMutableList()
    }

    private fun findAllRegistersValues() {
        var previousEmptyRegisterNumber = registers.count { (_, value) -> value == null }
        while (registers.any { (_, value) -> value == null }) {
            operations.forEach { applyOperation(it) }
            if (previousEmptyRegisterNumber == registers.count { (_, value) -> value == null }) {
                throw IllegalStateException("Unfeasible computation")
            }
            previousEmptyRegisterNumber = registers.count { (_, value) -> value == null }
        }
    }

    fun getZNumber(): Long {
        try {
            findAllRegistersValues()
            return registers.keys
                .filter { it.startsWith("z") }
                .sortedBy { it.substring(1).toInt() }
                .reversed()
                .map { if (registers[it]!!) 1 else 0 }
                .joinToString("")
                .toLong(2)
        } catch (e: IllegalStateException) {
            return -1
        }
    }

    fun expandEquationsAndCheckSwaps() {
        reset()
        getCorrectSwaps().forEach { swapRegisters(it.first, it.second) }
        findAllRegistersValues()
        expandEquations()
        checkResult()
    }

    private fun getCorrectSwaps() = listOf(
        "z07" to "nqk",
        "z24" to "fpq",
        "pcp" to "fgt",
        "srn" to "z32"
    )

    fun getCorrectSwapsSorted() = getCorrectSwaps().flatMap { listOf(it.first, it.second) }.sorted().joinToString(",")

    private fun checkResult() {
        operations.sortedBy { it.resultRegister.substring(1).toInt() }.forEach { operation ->
            val isValid = isValid(operation)
            if (!isValid.first) {
                println("${operation.resultRegister} -> ${isValid.second}: $operation")
            }
        }
        val xValue = binaryToLong(registers.filter { it.key.startsWith("x") }.map { it.value!! })
        val yValue = binaryToLong(registers.filter { it.key.startsWith("y") }.map { it.value!! })
        val zExpected = xValue + yValue
        val binaryZExpected = zExpected.toString(2)
        println("Binary Z expected: \t\t\t$binaryZExpected")
        val binaryCurrentZ = registers.keys
            .filter { it.startsWith("z") }
            .sortedBy { it.substring(1).toInt() }
            .reversed()
            .map { if (registers[it]!!) 1 else 0 }
            .joinToString("")
        println("Current Z : \t\t\t\t$binaryCurrentZ")
        if (binaryZExpected == binaryCurrentZ) {
            println("No error found")
        }
        binaryZExpected.reversed().forEachIndexed { index, char ->
            if (binaryCurrentZ.reversed()[index] != char) {
                println("Error on index $index")
            }
        }
    }

    private fun expandEquations() {
        val operationsToRemove = mutableListOf<Operation>()
        while (!operations.all {
                it.getOperandNames()
                    .all { name -> name.startsWith("z") || name.startsWith("x") || name.startsWith("y") }
            }) {
            operations.forEach { operation ->
                val resultRegister = operation.resultRegister
                if (!resultRegister.startsWith("z")) {
                    operations.filter { it.operand1.getNames().contains(resultRegister) }
                        .forEach {
                            it.operand1 = replaceOperand(
                                it.operand1,
                                resultRegister,
                                SubOperation(operation.operand1, operation.operand2, operation.operator)
                            )
                        }
                    operations.filter { it.operand2.getNames().contains(resultRegister) }
                        .forEach {
                            it.operand2 = replaceOperand(
                                it.operand2,
                                resultRegister,
                                SubOperation(operation.operand1, operation.operand2, operation.operator)
                            )
                        }
                    operationsToRemove.add(operation)
                }
            }
        }
        operations.removeAll(operationsToRemove)
    }

    private fun swapRegisters(register1: String, register2: String) {
        val krv = operations.find { it.resultRegister == register1 }!!
        val jss = operations.find { it.resultRegister == register2 }!!
        krv.resultRegister = register2
        jss.resultRegister = register1
    }

    private fun isValid(operation: Operation): Pair<Boolean, String> {
        val number = operation.resultRegister.substring(1)
        var errorMsg = ""
        val xRegex = Regex("x$number")
        val yRegex = Regex("y$number")
        val occurrencesX = xRegex.findAll(operation.toString()).toList().size
        val occurrencesY = yRegex.findAll(operation.toString()).toList().size
        if (occurrencesX != 1 && number != "45") {
            errorMsg += "[$occurrencesX found of x$number (1 expected)]"
        }
        if (occurrencesY != 1 && number != "45") {
            errorMsg += "[$occurrencesY found of y$number (1 expected)]"
        }
        if (number.toInt() > 1 && number.toInt() != 45) {
            for (i in 1..<number.toInt()) {
                val iWithTwoDigits = if (i > 9) "$i" else "0$i"
                val xorRegex1 = Regex("y$iWithTwoDigits XOR x$iWithTwoDigits")
                val xorRegex2 = Regex("x$iWithTwoDigits XOR y$iWithTwoDigits")
                val xorFound =
                    xorRegex1.findAll(operation.toString()).toList().size + xorRegex2.findAll(operation.toString())
                        .toList().size
                if (xorFound != 1
                ) {
                    errorMsg += "[$xorFound y$iWithTwoDigits XOR x$iWithTwoDigits found (1 expected)]"
                }
                val andRegex1 = Regex("x$iWithTwoDigits AND y$iWithTwoDigits")
                val andRegex2 = Regex("y$iWithTwoDigits AND x$iWithTwoDigits")
                val andFound =
                    andRegex1.findAll(operation.toString()).toList().size + andRegex2.findAll(operation.toString())
                        .toList().size
                if (andFound != 1) {
                    errorMsg += "[$andFound x$iWithTwoDigits AND y$iWithTwoDigits found (expected 1)]"
                }
            }
        }
        return errorMsg.isEmpty() to errorMsg
    }

    private fun binaryToLong(testNumberX1: List<Boolean>) =
        testNumberX1.reversed().map { if (it) 1 else 0 }.joinToString("").toLong(2)

    private fun applyOperation(operation: Operation) {
        val valueOperand1 = registers[operation.operand1.getNames().single()]
        val valueOperand2 = registers[operation.operand2.getNames().single()]
        if (valueOperand1 != null && valueOperand2 != null && registers[operation.resultRegister] == null) {
            registers[operation.resultRegister] = operation.operator.invoke(valueOperand1, valueOperand2)
        }
    }

    private fun getOperator(operator: String): (Boolean, Boolean) -> Boolean {
        return when (operator) {
            "AND" -> Boolean::and
            "OR" -> Boolean::or
            "XOR" -> Boolean::xor
            else -> throw IllegalArgumentException("Unknow operator $operator")
        }
    }

    open class Operation(
        var operand1: Operand,
        var operand2: Operand,
        var resultRegister: String,
        val operator: (Boolean, Boolean) -> Boolean
    ) {
        override fun toString(): String {
            return "$operand1 ${getOperatorName(operator)} $operand2 -> $resultRegister"
        }

        fun getOperandNames(): List<String> {
            return operand1.getNames() + operand2.getNames()
        }
    }

    abstract class Operand() {
        abstract fun getNames(): List<String>
    }

    class SingleValueOperand(var operand: String) : Operand() {
        override fun getNames(): List<String> {
            return listOf(operand)
        }

        override fun toString(): String {
            return operand
        }

        override fun equals(other: Any?): Boolean {
            return if (other is SingleValueOperand) {
                other.operand == operand
            } else {
                false
            }
        }

        override fun hashCode(): Int {
            return operand.hashCode()
        }

    }

    class SubOperation(
        private var operand1: Operand,
        private var operand2: Operand,
        private val operator: (Boolean, Boolean) -> Boolean
    ) : Operand() {
        override fun getNames(): List<String> {
            return operand1.getNames() + operand2.getNames()
        }

        override fun toString(): String {
            return "($operand1 ${getOperatorName(operator)} $operand2)"
        }

        fun replace(oldName: String, newOperand: Operand) {
            if (operand1 is SingleValueOperand && (operand1 as SingleValueOperand).operand == oldName) {
                operand1 = newOperand
            }
            if (operand1 is SubOperation && operand1.getNames().contains(oldName)) {
                operand1 = replaceOperand(operand1, oldName, newOperand)
            }
            if (operand2 is SingleValueOperand && (operand2 as SingleValueOperand).operand == oldName) {
                operand2 = newOperand
            }
            if (operand2 is SubOperation && operand2.getNames().contains(oldName)) {
                operand2 = replaceOperand(operand2, oldName, newOperand)
            }
        }

    }
}

private fun getOperatorName(operator: (Boolean, Boolean) -> Boolean): String {
    return when (operator) {
        Boolean::or -> "OR"
        Boolean::xor -> "XOR"
        Boolean::and -> "AND"
        else -> throw IllegalStateException("")
    }
}

private fun replaceOperand(operand: Day24.Operand, oldName: String, newOperand: Day24.Operand): Day24.Operand {
    when (operand) {
        is Day24.SingleValueOperand -> return newOperand
        is Day24.SubOperation -> {
            operand.replace(oldName, newOperand)
            return operand
        }
    }
    throw IllegalStateException("Unknown operand $operand")
}


