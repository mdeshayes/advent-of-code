package advent.y2024

import advent.util.readAllLines

fun main() {
    val line = readAllLines("2024/day9_input.txt")[0]
    val day09 = Day09(line)
    day09.compact()
    println("Part1: checksum is ${day09.getDiskChecksum()}")
    day09.compactBlocs()
    println("Part2: checksum is ${day09.getDiskBlockChecksum()}")
}

class Day09(line: String) {

    private val disk = line
        .asSequence()
        .map { it.digitToInt() }
        .flatMapIndexed { index, digit -> getNumber(index, digit) }
        .toMutableList()

    private val diskBlocs = line
        .asSequence()
        .map { it.digitToInt() }
        .mapIndexed { index, digit -> getNumber(index, digit) }
        .filterNot { it.isEmpty() }
        .toMutableList()

    private fun getNumber(index: Int, digit: Int) = List(size = digit) { if (index % 2 == 0) index / 2 else -1 }

    fun compact() {
        while (isNotCompacted()) {
            val lastDigitIndex = disk.indexOfLast { it >= 0 }
            val firstSpaceIndex = disk.indexOfFirst { it == -1 }
            swapFiles(firstSpaceIndex, lastDigitIndex)
        }
    }

    fun compactBlocs() {
        val fileBlock = diskBlocs.filter { isFile(it) }.map { it.first() }.reversed()
        fileBlock.forEach { id ->
            val blocIndex = diskBlocs.indexOfLast { it[0] == id }
            val bloc = diskBlocs[blocIndex]
            val firstSpaceIndex = diskBlocs.indexOfFirst { !isFile(it) && it.size >= bloc.size }
            if (firstSpaceIndex != -1) {
                val freeSpace = diskBlocs[firstSpaceIndex]
                if (firstSpaceIndex < blocIndex) {
                    diskBlocs[blocIndex] = createFreeSpace(bloc.size)
                    diskBlocs[firstSpaceIndex] = bloc.toMutableList()
                    if (freeSpace.size > bloc.size) {
                        diskBlocs.add(firstSpaceIndex + 1, createFreeSpace(freeSpace.size - bloc.size))
                    }
                }
            }
        }
    }

    private fun swapFiles(firstSpaceIndex: Int, lastDigitIndex: Int) {
        disk[firstSpaceIndex] = disk[lastDigitIndex]
        disk[lastDigitIndex] = -1
    }

    private fun isNotCompacted() = disk.indexOfLast { it >= 0 } != disk.indexOfFirst { it == -1 } - 1

    fun getDiskChecksum() = disk.map { if (it == -1) 0 else it.toLong() }.mapIndexed { index, it -> index * it }.sum()

    fun getDiskBlockChecksum() = diskBlocs.asSequence().flatten().map { if (it == -1) 0 else it.toLong() }
        .mapIndexed { index, it -> index * it }.sum()

    private fun createFreeSpace(size: Int) = MutableList(size) { -1 }

    private fun isFile(file: List<Int>) = file.all { it >= 0 }

}