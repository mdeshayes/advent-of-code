package advent.y2025

import advent.util.readAllLines


fun main() {
    val allLines1 = readAllLines("2025/day12_input.txt")
    val allLines = allLines1
    val day12 = Day12(allLines)
    println("Number of valid regions: ${day12.getNumberOfValidRegions()}")
}

data class Region(val width: Int, val height: Int, val numberOfPresents: List<Int>)

class Day12(lines: List<String>) {

    val regions = lines
        .filter { it.contains("x") }
        .map { it.split(":") }
        .map {
            Region(
                it[0].split("x")[0].toInt(),
                it[0].split("x")[1].toInt(),
                it[1].split(" ").filterNot(String::isEmpty).map(String::toInt)
            )
        }

    fun canFit(region: Region): Boolean {
        val presentsToFit = region.numberOfPresents.sum()
        val availableSpaceForPresent = (region.width / 3) * (region.height / 3)
        return availableSpaceForPresent >= presentsToFit
    }

    fun getNumberOfValidRegions(): Int {
        return regions.count { canFit(it) }
    }

}



