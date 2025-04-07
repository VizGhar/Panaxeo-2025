package algos

import bestPathSoFar
import bestScoreSoFar
import core.base
import core.cities
import core.distanceMatrix
import currentDistance
import setPathUI
import java.io.File
import kotlin.math.exp
import kotlin.random.Random

data class SaPath(val path: List<Int>) {
    val score: Int
    val traveledCities: Int
    val traveledDistance: Int
    val validPath: List<Int>

    init {
        var result = 0
        var distance = 0
        var previousCityId = -1
        var traveled = 0
        for (cityId in path) {
            if (previousCityId != -1) {
                val distanceToNew = distanceMatrix[previousCityId][cityId]
                val distanceHome = distanceMatrix[cityId][path[0]]
                // going there denies going home?
                if (distance + distanceToNew + distanceHome > 1500) {
                    break
                }
                distance += distanceToNew
                if (distance > 1500) break
            }
            traveled++
            result += cities[cityId].population
            previousCityId = cityId
        }
        score = result
        traveledCities = traveled
        traveledDistance = distance + distanceMatrix[path[traveled - 1]][base.index]
        validPath = path.take(traveledCities) + base.index
        validate()
    }

    fun validate() {
        if (score > bestScoreSoFar) {
            val result = path.take(traveledCities) + base.index
            bestScoreSoFar = score
            currentDistance = traveledDistance
            setPathUI(result)
        }
    }
}

private fun SaPath.getRandomNeighbor(): SaPath {
    val newPath = path.toMutableList()
    val i = Random.nextInt(1, newPath.size - 1)
    val j = Random.nextInt(i + 1, newPath.size)
    if (Random.nextBoolean()) {
        newPath[i] = newPath[j].also { newPath[j] = newPath[i] }
    } else {
        newPath.subList(i, j).reverse()
    }
    return SaPath(newPath)
}

private fun initial(blackList: List<Int> = listOf(
    53, 121, 90, 129, 70, 78, 102, 95, 69, 120, 82, 80, 61, 96, 133, 122
)): SaPath {
    return SaPath(
        listOf(base.index) + (cities.indices - blackList - base.index).shuffled()
    )
}


fun sa() {
    repeat(1) {
        bestScoreSoFar = -1
        bestPathSoFar = null
        currentDistance = -1
        var actual = initial()
        var best = actual
        var initialTemp = 10000000.0
        val alpha = 0.999999999
        var t = initialTemp
        for (i in 10000000000 downTo 0) {
            val susedneRiesenie = actual.getRandomNeighbor()
            if (akceptacnaFunkcia(actual, susedneRiesenie, t)) { actual = susedneRiesenie }
            if (actual.score > best.score) {
                best = actual
                println(best.validPath.joinToString(","))
            }
            t *= alpha
        }
        File("out.txt").appendText("${best.score} - ${best.validPath.joinToString(",")}\n")
    }
}

private fun akceptacnaFunkcia(riesenie: SaPath, sused: SaPath, teplota: Double) : Boolean  {
    val diff = sused.score - riesenie.score
    if (diff > 0) return true
    return Random.nextDouble(0.0, 1.0) < exp(diff / teplota)
}