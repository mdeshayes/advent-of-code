package advent.y2025

import advent.util.readAllLines

fun main() {
    val allLines = readAllLines("2025/day2_input.txt")[0]
    val day02 = Day02(allLines)
    println("Numbers of invalids = ${day02.getNumbersOfInvalids()}")
    println("Numbers of really invalids = ${day02.getNumbersOfReallyInvalids()}")
}

class Day02(line: String) {

    val ranges = line.split(",").map {
        val rangeLimits = it.split("-")
        rangeLimits[0].toLong() to rangeLimits[1].toLong()
    }

    fun getNumbersOfInvalids() = ranges.sumOf { (begin, end) ->
        (begin..end).filter { isInvalid(it) }.sum()
    }

    fun getNumbersOfReallyInvalids() = ranges.sumOf { (begin, end) ->
        (begin..end).filter { isReallyInvalid(it) }.sum()
    }

    private fun isInvalid(number: Long): Boolean {
        val stringRepresentation = number.toString()
        if (stringRepresentation.length % 2 > 0) return false
        val firstHalf = stringRepresentation.take(stringRepresentation.length / 2)
        val secondHalf = stringRepresentation.substring(stringRepresentation.length / 2)
        return firstHalf == secondHalf
    }

    private fun isReallyInvalid(number: Long): Boolean {
        val stringRepresentation = number.toString()
        val maxPatternSize = stringRepresentation.length / 2
        for (patternSize in 1..maxPatternSize) {
            val chunks = stringRepresentation.chunked(patternSize)
            if (chunks.all { it == chunks[0] }) return true
        }
        return false
    }

}

