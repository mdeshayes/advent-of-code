package advent.y2024

import advent.util.Map2d
import advent.util.readAllLines

fun main() {
    val lines = readAllLines("2024/day8_input.txt")
    val day8 = Day8(lines)
    println("Part 1: ${day8.countSingleAntinodes()} single antinodes found")
    println("Part 2: ${day8.countAllAntinodes()} antinodes found")
}

class Day8(lines: List<String>) {

    private val charMap = Map2d(lines) { it }
    private val antennas = getAntennas(lines)

    fun countSingleAntinodes(): Int = getAntennaPairs(antennas).flatMap { getAntinodes(it, all = false) }.toSet().size
    fun countAllAntinodes(): Int = getAntennaPairs(antennas).flatMap { getAntinodes(it, all = true) }.toSet().size

    private fun getAntinodes(
        antennaPair: Pair<Antenna, Antenna>,
        all: Boolean
    ): List<Pair<Int, Int>> {
        val (antenna1, antenna2) = antennaPair
        val xDelta = antenna1.x - antenna2.x
        val yDelta = antenna1.y - antenna2.y
        if (!all) {
            return mutableListOf(
                antenna1.x + xDelta to antenna1.y + yDelta,
                antenna2.x - xDelta to antenna2.y - yDelta
            ).filter { charMap.isValid(it.first, it.second) }
        }
        return getAllAntinodes(antenna1, antenna2, xDelta, yDelta)
    }

    private fun getAllAntinodes(
        antenna1: Antenna,
        antenna2: Antenna,
        xDelta: Int,
        yDelta: Int,
    ): MutableList<Pair<Int, Int>> {
        val validAntinodes = mutableListOf<Pair<Int, Int>>()
        var index = 0
        while (charMap.isValid(antenna1.x + (xDelta * index), antenna1.y + (yDelta * index))) {
            validAntinodes.add(antenna1.x + (xDelta * index) to antenna1.y + (yDelta * index))
            index++
        }
        index = 0
        while (charMap.isValid(antenna2.x - (xDelta * index), antenna2.y - (yDelta * index))) {
            validAntinodes.add(antenna2.x - (xDelta * index) to antenna2.y - (yDelta * index))
            index++
        }
        return validAntinodes
    }

    private fun getAntennaPairs(antennas: List<Antenna>) = antennas
        .groupBy { it.frequency }
        .flatMap { (_, antennasWithSameFrequency) -> generatePairs(antennasWithSameFrequency) }

    private fun generatePairs(antennasWithSameFrequency: List<Antenna>) =
        antennasWithSameFrequency.indices.flatMap { index ->
            (index + 1 until antennasWithSameFrequency.size).map { index2 ->
                antennasWithSameFrequency[index] to antennasWithSameFrequency[index2]
            }
        }

    private fun getAntennas(lines: List<String>) =
        lines.flatMapIndexed { lineIndex, line ->
            line
                .asSequence()
                .withIndex()
                .filterNot { it.value == '.' }
                .map { charIndexed -> Antenna(charIndexed.index, lineIndex, charIndexed.value) }
        }

    data class Antenna(val x: Int, val y: Int, val frequency: Char)

}

