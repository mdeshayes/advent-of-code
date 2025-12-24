package advent.y2024

import advent.util.readAllLines


fun main() {
    val line = readAllLines("2024/day11_input.txt")[0]
    val day11 = Day11(line)
    println("Part1: There are ${day11.getStonesNumberAfterBlinks(25)} stones after 25 blinks")
    val begin = System.currentTimeMillis()
    println("Part2: There are ${day11.getStonesNumberAfterBlinks(75)} stones after 75 blinks (${(System.currentTimeMillis() - begin) / 1000L}s)")
}

private const val BATCH_SIZE = 25

class Day11(line: String) {

    private var stones: List<Long> = line.split(" ").map { it.toLong() }
    private var memory: MutableMap<Long, List<Long>> = HashMap()

    fun getStonesNumberAfterBlinks(iterations: Int): Long {
        return getStonesNumberAfterBlinks(stones, iterations / BATCH_SIZE)
    }

    private fun getStonesNumberAfterBlinks(stones: List<Long>, iterations: Int): Long {
        if (iterations == 0) {
            return stones.size.toLong()
        }
        return stones
            .groupBy { it }
            .map { (stoneValue, listOfStones) ->
                getStonesNumberAfterBlinks(
                    batchTransformStone(stoneValue),
                    iterations - 1
                ) * listOfStones.size
            }
            .sum()
    }

    private fun batchTransformStone(stone: Long): List<Long> {
        if (memory.containsKey(stone)) {
            return memory[stone]!!
        }
        val listAfterBlinking = getListAfterBatchBlinking(stone)
        memory[stone] = listAfterBlinking
        return listAfterBlinking
    }

    private fun getListAfterBatchBlinking(initialStone: Long) =
        (0 until BATCH_SIZE)
            .fold(mutableListOf(initialStone)) { stones, _ ->
                stones.flatMap { stone -> singleTransformStone(stone) }.toMutableList()
            }

    private fun singleTransformStone(stone: Long): List<Long> {
        return when {
            stone == 0L -> listOf(1L)
            stone.toString().length % 2 == 0 -> stone.toString().chunked(stone.toString().length / 2)
                .map { it.toLong() }

            else -> listOf(stone * 2024L)
        }
    }

}