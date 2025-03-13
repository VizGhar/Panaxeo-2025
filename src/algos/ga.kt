package algos

import core.base
import core.cities
import core.distanceMatrix
import java.io.File
import kotlin.random.Random

/**
 * Genetic algorithm was meant to improve solutions generated by SA. Quite unsuccessful, but cool
 */
fun ga() {
    geneticAlgorithm()
}

private const val populationSize = 3000
private const val elitismRate = 100
private const val mutationRate = 0.1
private const val crossoverRate = 0.9
private const val tournamentSize = 2

private fun fitness(path: List<Int>): Int {
    var distance = 0
    var previous = base.index
    val validPath = path.takeWhile { current ->
        if (current == previous) return@takeWhile true
        (distance + distanceMatrix[previous][current] + distanceMatrix[current][base.index] <= 1500).also {
            distance = distance + distanceMatrix[previous][current]
            previous = current
        }
    }
    return validPath.sumOf { cities[it].population }
}

private fun orderCrossover(parent1: List<Int>, parent2: List<Int>): List<Int> {
    val size = parent1.size
    val start = Random.nextInt(size / 3, size / 2)
    val end = start + Random.nextInt(size / 3, size / 2).coerceAtMost(size - start)

    val child = MutableList(size) { -1 }

    // Skopíruj podsekvenciu z parent1
    for (i in start until end) {
        child[i] = parent1[i]
    }

    child[0] = base.index

    // Naplň zvyšok z parent2 v pôvodnom poradí
    var index = end % size
    for (city in parent2) {
        if (!child.contains(city)) {
            while (child[index] != -1) {
                index = (index + 1) % size
            }
            child[index] = city
        }
    }

    return child
}

// rotate part of route
private fun mutation1(path: List<Int>): List<Int> {
    val i = Random.nextInt(1, path.size - 2)
    val j = Random.nextInt(i + 1, path.size - 1)
    return path.take(i) + path.subList(i, j).reversed() + path.drop(j)
}

// swap 2 cities
private fun mutation2(path: List<Int>): List<Int> {
    val i = Random.nextInt(1, path.size - 2)
    val j = Random.nextInt(1, path.size - 2)
    return path.mapIndexed { index, id ->
        when (index) {
            i -> path[j]
            j -> path[i]
            else -> id
        }
    }
}

private fun crossover(p1: List<Int>, p2: List<Int>): List<Int> {
    return orderCrossover(p1, p2)
}

private fun mutate(path: List<Int>): List<Int> {
    return when (Random.nextInt(2)) {
        0 -> mutation1(path)
        else -> mutation2(path)
    }
}

private fun tournamentSelection(population: List<List<Int>>) = (1..tournamentSize)
    .map { population.random() }
    .maxBy { fitness(it) }

private fun geneticAlgorithm(): List<Int> {
    var population = List(populationSize) { listOf(base.index) + (cities.indices - base.index).shuffled() }
    var best = population.maxBy { fitness(it) }

    while (true) {
        val newPopulation = mutableListOf<List<Int>>()

        for (i in 0..elitismRate) {
            newPopulation += population[i]
        }

        while (newPopulation.size < populationSize) {
            val parent1 = tournamentSelection(population)
            val parent2 = tournamentSelection(population)

            val child = if (Random.nextDouble() < crossoverRate) crossover(parent1, parent2) else parent1
            val mutatedChild = if (Random.nextDouble() < mutationRate) mutate(child) else child

            newPopulation += mutatedChild
        }

        val newBest = newPopulation.maxBy { fitness(it) }
        if (fitness(newBest) > fitness(best)) {
            best = newBest
            println(fitness(best))
            println(best)
        }
        population = newPopulation
    }

    return best
}