package advent.y2025

import advent.util.readAllLines
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    val allLines = readAllLines("2025/day8_input.txt")
    val day08 = Day08(allLines)
    println("Three larger circuit size multiplied together: ${day08.getThreeLargerCircuitsSize()}")
    val lastJunctionToCreateCircuit = day08.getLastJunctionToCreateCircuit()
    println("Lasts junction boxes' x multiplied together: ${lastJunctionToCreateCircuit.first.x * lastJunctionToCreateCircuit.second.x}")
}

data class Position3d(val x: Long, val y: Long, val z: Long)

class Day08(lines: List<String>) {

    val distancesByPair = mutableMapOf<Pair<Position3d, Position3d>, Double>()
    val pairsByDistance = mutableMapOf<Double, Pair<Position3d, Position3d>>()
    val distances = mutableListOf<Double>()
    val positions = lines.map {
        val coordinates = it.split(",")
        Position3d(coordinates[0].toLong(), coordinates[1].toLong(), coordinates[2].toLong())
    }

    init {
        computeAllDistances()
    }

    fun getThreeLargerCircuitsSize(): Long {
        val pairedBoxes = getPairedBoxes(1000)
        val circuits = getCircuits(pairedBoxes)
        return circuits.sortedByDescending { it.size }.take(3).map { it.size }.reduce(Int::times).toLong()
    }

    fun getLastJunctionToCreateCircuit(): Pair<Position3d, Position3d> {
        val pairedBoxes = getPairedBoxes()
        val circuits = mutableSetOf<MutableSet<Position3d>>()
        for (junction in pairedBoxes) {
            addJunctionToCircuits(junction, circuits)
            if (circuits.size == 1 && circuits.first().size == 1000) {
                return junction.first() to junction.last()
            }
        }
        throw Exception("No large circuit found")
    }

    private fun getCircuits(pairedBoxes: List<MutableSet<Position3d>>): MutableSet<MutableSet<Position3d>> {
        val circuits = mutableSetOf<MutableSet<Position3d>>()
        for (junction in pairedBoxes) {
            addJunctionToCircuits(junction, circuits)
        }
        return circuits
    }

    private fun addJunctionToCircuits(
        junction: MutableSet<Position3d>,
        circuits: MutableSet<MutableSet<Position3d>>,
    ) {
        val box1 = junction.first()
        val box2 = junction.last()
        if (circuits.any { it.contains(box1) && it.contains(box2) }) return
        if (circuits.isEmpty()) {
            circuits.add(mutableSetOf(box1, box2))
            return
        }
        val box1Circuit = circuits.find { it.contains(box1) } ?: mutableSetOf(box1)
        val box2Circuit = circuits.find { it.contains(box2) } ?: mutableSetOf(box2)
        circuits.removeAll { it.contains(box1) || it.contains(box2) }
        val newCircuit = box1Circuit + box2Circuit
        circuits.add(newCircuit.toMutableSet())
    }

    private fun getPairedBoxes(numberOfConnections: Int? = null): List<MutableSet<Position3d>> = distances
        .sorted()
        .map { mutableSetOf(pairsByDistance[it]!!.first, pairsByDistance[it]!!.second) }
        .take(numberOfConnections ?: distances.size)

    private fun computeAllDistances() {
        for (i in 0..positions.size - 1) {
            for (j in i + 1..positions.size - 1) {
                val distance = computeDistance(positions[i], positions[j])
                distancesByPair[positions[i] to positions[j]] = distance
                pairsByDistance[distance] = positions[i] to positions[j]
                distances.add(distance)
            }
        }
    }

    private fun computeDistance(box1: Position3d, box2: Position3d): Double {
        val xSquare = (box1.x - box2.x).toDouble().pow(2)
        val ySquare = (box1.y - box2.y).toDouble().pow(2)
        val zSquare = (box1.z - box2.z).toDouble().pow(2)
        return sqrt(xSquare + ySquare + zSquare)
    }

}



