package advent.y2024

import advent.util.readAllLines

fun main() {
    val lines = readAllLines("2024/day25_input.txt")
    val day25 = Day25(lines)
    println("Part 1: ${day25.countKeyLockPairs()} key-lock pairs are compatible")
}

class Day25(lines: List<String>) {

    private val keys =
        lines
            .filterNot { it.isEmpty() }
            .chunked(7)
            .filter { it.first() == "....." }
            .map { schema -> schema[0].indices.map { column -> schema.count { line -> line[column] == '#' } - 1 } }
    private val locks =
        lines
            .filterNot { it.isEmpty() }
            .chunked(7)
            .filterNot { it.isEmpty() }
            .filter { it.first() == "#####" }
            .map { schema -> schema[0].indices.map { column -> schema.count { line -> line[column] == '#' } - 1 } }

    fun countKeyLockPairs(): Int = keys.sumOf { key -> locks.count { lock -> isCompatible(key, lock) } }

    private fun isCompatible(key: List<Int>, lock: List<Int>) =
        key.mapIndexed { index, height -> lock[index] + height <= 5 }.all { it }

}