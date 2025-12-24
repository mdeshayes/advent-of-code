package advent.y2025

import advent.util.readAllLines


fun main() {
    val allLines1 = readAllLines("2025/day11_input.txt")
    val allLines = allLines1
    val day11 = Day11(allLines)
    println("Number of paths from you to out: ${day11.getNumberOfPaths()}")
    println("Number of paths from srv to out: ${day11.getNumberOfPathsViaFftAndDac()}")
}

class Day11(lines: List<String>) {

    var cache = mutableMapOf<String, Long>()

    val paths =
        lines.associate { it.split(":")[0] to it.split(":")[1].split(" ").map(String::trim).filterNot(String::isEmpty) }

    fun getNumberOfPaths(): Long {
        return getNumberOfPaths("you", "out", "TOTO", true)
    }

    fun getNumberOfPathsViaFftAndDac(): Long {
        return getNumberOfPaths("svr", "fft", "dac", true) *
                getNumberOfPaths("fft", "dac", resetCache = true) *
                getNumberOfPaths("dac", "out", resetCache = true) +
                getNumberOfPaths("svr", "dac", "fft", true) *
                getNumberOfPaths("dac", "fft", resetCache = true) *
                getNumberOfPaths("fft", "out", resetCache = true)
    }

    private fun getNumberOfPaths(
        startingPosition: String,
        targetPosition: String,
        forbiddenPosition: String? = null,
        resetCache: Boolean,
    ): Long {
        if (resetCache) {
            cache = mutableMapOf()
        }
        if (startingPosition == forbiddenPosition) {
            return 0L
        }
        if (startingPosition == targetPosition) {
            return 1L
        }
        if (cache.contains(startingPosition)) {
            return cache[startingPosition]!!
        }
        return paths[startingPosition]?.sumOf {
            val numberOfPaths = getNumberOfPaths(it, targetPosition, forbiddenPosition, false)
            cache[it] = numberOfPaths
            numberOfPaths
        } ?: 0L
    }
}



