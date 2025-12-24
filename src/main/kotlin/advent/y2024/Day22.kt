package advent.y2024

import advent.util.readAllLines
import java.lang.Math.floorMod

fun main() {
    val lines = readAllLines("2024/day22_input.txt")
    val day22 = Day22(lines)
    println("Part 1: sum of secrets is ${day22.getSumOfSecrets(2000)}")
    val sequenceAndMaxBananas = day22.getMaxBananas(2000)
    println("Part 2: we can buy ${sequenceAndMaxBananas.second} bananas with the sequence ${sequenceAndMaxBananas.first}")
}

class Day22(lines: List<String>) {

    private val secretSeeds = lines.map { it.toLong() }

    fun getMaxBananas(iterations: Int): Pair<List<Int>, Int> {
        val bananasPerSequencePerSeller = getDiffPricesSequences(iterations + 1)
        val bananasPerSequence = mutableMapOf<List<Int>, Int>()
        bananasPerSequencePerSeller.forEach { map ->
            map.forEach { (sequence, bananas) ->
                if (bananasPerSequence.containsKey(sequence)) {
                    bananasPerSequence[sequence] = bananasPerSequence[sequence]!! + bananas
                } else {
                    bananasPerSequence[sequence] = bananas
                }
            }
        }
        return bananasPerSequence.maxBy { it.value }.toPair()
    }

    private fun getDiffPricesSequences(iterations: Int) = secretSeeds
        .map { secret -> getPriceDiffSequence(iterations, secret) }

    private fun getPriceDiffSequence(iterations: Int, secret: Long): Map<List<Int>, Int> {
        val priceSequence = getPriceSequence(iterations, secret)
        val diffPrice = getDiffPrice(priceSequence)
        val pricePerSequence = getPricePerSequence(diffPrice)
        val deduplicatedPricePerSequence = getDeduplicatedPricePerSequence(pricePerSequence)
        return deduplicatedPricePerSequence.values.associate { it }
    }

    private fun getPriceSequence(iterations: Int, secret: Long) = (0 until iterations)
        .map { generateSecret(secret, it) }
        .map { it.toString().last().digitToInt() }

    private fun getDiffPrice(priceSequence: List<Int>) = (1 until priceSequence.size).map {
        priceSequence[it] - priceSequence[it - 1] to priceSequence[it]
    }

    private fun getDeduplicatedPricePerSequence(pricePerSequence: List<Pair<List<Int>, Int>>) =
        pricePerSequence.groupBy { it.first }.mapValues { it.value.first() }

    private fun getPricePerSequence(diffPrice: List<Pair<Int, Int>>) =
        (3 until diffPrice.size).map {
            listOf(
                diffPrice[it - 3].first,
                diffPrice[it - 2].first,
                diffPrice[it - 1].first,
                diffPrice[it].first
            ) to diffPrice[it].second
        }


    fun getSumOfSecrets(iterations: Int) = secretSeeds.sumOf { generateSecret(it, iterations) }

    private fun generateSecret(seed: Long, iterations: Int): Long {
        if (iterations == 0) {
            return seed
        }
        val secret = generateSecret(generateSecret(seed), iterations - 1)
        return secret
    }

    private fun generateSecret(seed: Long): Long {
        val afterStepOne = floorMod(seed xor (seed * 64L), 16777216L)
        val afterStepTwo = floorMod((afterStepOne / 32.0).toLong() xor afterStepOne, 16777216L)
        val afterStepThree = floorMod((afterStepTwo * 2048) xor afterStepTwo, 16777216L)
        return afterStepThree
    }


}