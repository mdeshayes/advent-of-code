package advent.y2024

import advent.util.Map2d
import advent.util.readAllLines


fun main() {
    val lines = readAllLines("2024/day4_input.txt")
    val day04 = Day04(lines)
    println("Part1: There are ${day04.getXmasNumber()} Xmas")
    println("Part2: There are ${day04.getCrossMasNumber()} X-max")
}

class Day04(val lines: List<String>) {

    private val charMap = Map2d(lines) { it }

    fun getXmasNumber() = charMap
        .filter { (_, char) -> char == 'X' }
        .sumOf { (coord, _) -> countXmasAround(coord.first, coord.second) }

    fun getCrossMasNumber(): Int = charMap
        .filter { (_, char) -> char == 'A' }
        .count { (coord, _) -> isCrossMas(coord.first, coord.second) }


    private fun isCrossMas(col: Int, line: Int): Boolean =
        listOf(
            ::getCharUpRight to ::getCharDownLeft,
            ::getCharDownLeft to ::getCharUpRight,
            ::getCharUpLeft to ::getCharDownRight,
            ::getCharDownRight to ::getCharUpLeft
        ).count { it.first(col, line, 1) == 'M' && it.second(col, line, 1) == 'S' } == 2

    private fun countXmasAround(col: Int, line: Int): Int = listOf(
        ::getCharUp,
        ::getCharDown,
        ::getCharDownLeft,
        ::getCharDownRight,
        ::getCharUpRight,
        ::getCharUpLeft,
        ::getCharLeft,
        ::getCharRight
    ).count { lookForXmas(col, line, it) }

    private fun lookForXmas(col: Int, line: Int, lookFunc: (Int, Int, Int) -> Char?) =
        lookFunc(col, line, 1) == 'M' && lookFunc(col, line, 2) == 'A' && lookFunc(col, line, 3) == 'S'

    fun getCharUp(col: Int, line: Int, number: Int = 1): Char? = charMap.getOrNull(col, line - number)
    fun getCharDown(col: Int, line: Int, number: Int = 1): Char? = getChar(col, line + number)
    fun getCharLeft(col: Int, line: Int, number: Int = 1): Char? = getChar(col - number, line)
    fun getCharRight(col: Int, line: Int, number: Int = 1): Char? = getChar(col + number, line)
    fun getCharUpRight(col: Int, line: Int, number: Int = 1): Char? = getChar(col + number, line - number)
    fun getCharDownRight(col: Int, line: Int, number: Int = 1): Char? = getChar(col + number, line + number)
    fun getCharUpLeft(col: Int, line: Int, number: Int = 1): Char? = getChar(col - number, line - number)
    fun getCharDownLeft(col: Int, line: Int, number: Int = 1): Char? = getChar(col - number, line + number)

    private fun getChar(col: Int, line: Int): Char? = charMap.getOrNull(col, line)

}

