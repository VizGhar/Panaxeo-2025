package core

import bestPathSoFar
import bestScoreSoFar
import currentDistance
import setPathUI
import java.io.File

data class City(val rank: Int, val name: String, val population: Int, val lat: Double, val lon :Double) {
    val index get() = rank - 1
}

val base = City(92, "Gelnica", 7448, 0.0, 0.0)

// all zero indexed
val cities = File("cities.csv").readLines().drop(1).map { it.split(",").let { City(it[0].toInt(), it[1], it[2].toInt(), it[4].toDouble(), it[3].toDouble()) } }
val distanceMatrix = File("city_distance_matrix.csv").readLines().drop(1).map { s -> s.split(",").drop(1).map { value -> value.toInt() }.toTypedArray() }.toTypedArray()
val citiesMap = cities.associateBy { it.rank - 1 }

data class Path(
    val path: MutableList<Int> = mutableListOf(base.index, base.index),
    var distance: Int = 0,
    var score: Int = base.population
) {

    fun cp() = Path(
        path.toMutableList(),
        distance,
        score
    )

    fun canAddCity(city: Int, at: Int = path.size - 1): Boolean {
        val newDistance = distance -
                distanceMatrix[path[at - 1]][path[at]] +
                distanceMatrix[path[at - 1]][city] +
                distanceMatrix[city][path[at]]
        return newDistance <= 1500
    }

    fun bestIndexToAddCity(city: City): Int? {
        var bestScore = Int.MAX_VALUE
        var bestIndex = -1
        for (i in 1..path.size - 2) {
            if (canAddCity(city.index, i)) {
                addCity(city.index, i)
                if (distance < bestScore) {
                    bestScore = distance
                    bestIndex = i
                }
                removeCity(i)
            }
        }
        return bestIndex.takeIf { it != -1 }
    }

    fun addCity(city: Int, at: Int = path.size - 1) {
        if (path.size < 2) throw IllegalStateException()
        val a = path[at - 1]
        val b = city
        val c = path[at]
        val newDistance = distance -
                distanceMatrix[a][c] +
                distanceMatrix[a][b] +
                distanceMatrix[b][c]
        distance = newDistance
        score += cities[city].population
        path.add(at, city)
        validate()
    }

    fun removeCity(at: Int = path.size - 2) {
        if (path.size <= 2) throw IllegalStateException()
        val a = path[at - 1]
        val b = path[at]
        val c = path[at + 1]
        distance = distance -
                distanceMatrix[b][c] -
                distanceMatrix[a][b] +
                distanceMatrix[a][c]
        score -= cities[b].population
        path.remove(b)
    }

    fun print() {
        println()
        println("$score / $distance : " + path.joinToString(",") { citiesMap[it]?.name ?: throw IllegalStateException("Index $it doesn't exists") })
    }

    fun validate() {
        if (score > bestScoreSoFar) {
            bestScoreSoFar = score
            currentDistance = distance
            bestPathSoFar = this
            bestPathSoFar?.print()
            setPathUI(path)
        }
    }
}
