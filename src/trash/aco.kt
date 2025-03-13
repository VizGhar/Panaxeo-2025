package trash

import algos.SaPath
import core.base
import core.cities
import core.distanceMatrix
import kotlin.math.pow

private val alpha = 1.0  // Vplyv feromónov
private val beta = 5.0   // Vplyv heuristiky (1/distance)
private val rho = 0.2    // Miera odparovania
private val Q = 100.0    // Feromónová konštanta
private val numAnts = 50
private val numIterations = 100000

private val pheromones = Array(cities.size) { DoubleArray(cities.size) { 1.0 } }

private fun selectNextCity(currentCity: Int, visited: Set<Int>, currentDistance: Int): Int? {
    val probabilities = mutableListOf<Pair<Int, Double>>()
    val sum = cities.indices.filter { it !in visited && currentDistance + distanceMatrix[currentCity][it] + distanceMatrix[it][base.index] < 1500 }.sumOf { neighbor ->
        val pheromone = pheromones[currentCity][neighbor].pow(alpha)
        val heuristic = (1.0 / distanceMatrix[currentCity][neighbor]).pow(beta)
        pheromone * heuristic
    }

    for (neighbor in cities.indices.filter { it !in visited && currentDistance + distanceMatrix[currentCity][it] + distanceMatrix[it][base.index] < 1500 }) {
        val pheromone = pheromones[currentCity][neighbor].pow(alpha)
        val heuristic = (1.0 / distanceMatrix[currentCity][neighbor]).pow(beta)
        probabilities.add(neighbor to (pheromone * heuristic) / sum)
    }

    return probabilities.randomByWeightOrNull()
}

private fun <T> List<Pair<T, Double>>.randomByWeightOrNull(): T? {
    if (isEmpty()) return null
    val rand = Math.random()
    var cumulative = 0.0
    for ((item, weight) in this) {
        cumulative += weight
        if (rand <= cumulative) return item
    }
    return this.last().first
}


private fun runACO(): SaPath {
    var bestPath = listOf<Int>()
    var bestScore = 0

    for (iteration in 1..numIterations) {
        val solutions = mutableListOf<SaPath>()

        repeat(numAnts) {
            val visited = mutableSetOf(base.index)
            val path = mutableListOf(base.index)
            var totalDistance = 0

            while (visited.size < cities.size) {
                val nextCity = selectNextCity(path.last(), visited, totalDistance)
                if (nextCity == null) {
                    path.add(base.index)
                    break
                }
                val newDistance = totalDistance + distanceMatrix[path.last()][nextCity]

                path.add(nextCity)
                visited.add(nextCity)
                totalDistance = newDistance
            }

            path.add(base.index)
            solutions.add(SaPath(path))
        }

        // Vyber najlepšiu cestu z tejto iterácie
        val bestAnt = solutions.maxByOrNull { it.score } ?: throw IllegalStateException()
        if (bestAnt.score > bestScore) {
            bestScore = bestAnt.score
            bestPath = bestAnt.path
        }

        // Aktualizácia feromónov
        for (i in pheromones.indices) {
            for (j in pheromones[i].indices) {
                pheromones[i][j] *= (1 - rho)  // Odparovanie feromónu
            }
        }

        for (path in solutions) {
            val score = path.score
            for (i in 0 until path.path.size - 1) {
                pheromones[path.path[i]][path.path[i + 1]] += Q / score
            }
        }
    }

    return SaPath(bestPath)
}

fun aco() {
    runACO()
}