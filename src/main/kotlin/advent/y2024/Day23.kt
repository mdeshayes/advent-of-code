package advent.y2024

import advent.util.readAllLines

fun main() {
    val lines = readAllLines("2024/day23_input.txt")
    val day23 = Day23(lines)
    println("Part1: There are ${day23.getNetworksContainingT(3).size} three computers networks")
    println("Part2: The password for the LAN party is ${day23.getBiggestNetwork().sortedBy { it }.joinToString(",")}")
}

class Day23(lines: List<String>) {

    private val connections = lines.map { it.split("-")[0] to it.split("-")[1] }
    private val computersWithConnections =
        connections.map { setOf(it.first, it.second) }.flatten().associateWith { mutableSetOf<String>() }
    private val computers: MutableList<Computer>

    init {
        connections.forEach { (computer1, computer2) ->
            computersWithConnections[computer1]!!.add(computer2)
            computersWithConnections[computer2]!!.add(computer1)
        }
        computers = computersWithConnections.keys.map { Computer(it, mutableListOf()) }.toMutableList()
        computersWithConnections.forEach { (computer, connections) ->
            connections.forEach { connection ->
                val node = computers.find { it.name == connection }!!
                computers.find { it.name == computer }!!.connectedComputers.add(node)
            }
        }
    }

    fun getNetworksContainingT(size: Int) = getAllNetworks(size)
        .filter { it.any { name -> name.startsWith("t") } && it.size == size }

    private fun getAllNetworks(maxSize: Int = Int.MAX_VALUE): Set<Set<String>> {
        val networks: MutableSet<MutableSet<String>> =
            connections.map { mutableSetOf(it.first, it.second) }.toMutableSet()
        for (computer in computers) {
            for (network in networks.toMutableSet()) {
                if (network.all { isConnected(computer, it) }) {
                    if (network.size < maxSize) {
                        network.add(computer.name)
                    } else {
                        networks.addAll(createNewNetworks(network, computer))
                    }
                }
            }
        }
        return networks.toSet()
    }

    private fun createNewNetworks(network: MutableSet<String>, computer: Computer) = network
        .map {
            network.toMutableSet().apply {
                this.remove(it)
                this.add(computer.name)
            }
        }.toSet()

    fun getBiggestNetwork() = getAllNetworks().maxBy { it.size }

    private fun isConnected(computer: Computer, otherComputerName: String): Boolean {
        return computer.connectedComputers.any { it.name == otherComputerName }
    }

    class Computer(val name: String, val connectedComputers: MutableList<Computer>) {
        override fun toString(): String {
            return name
        }
    }
}