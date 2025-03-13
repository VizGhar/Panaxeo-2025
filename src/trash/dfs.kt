package trash

import bestPathSoFar
import bestScoreSoFar
import core.Path
import core.cities
import core.distanceMatrix

fun dfs(path: Path = Path(), tabu: Set<Int>): Path? {
    val currentCity = path.path[path.path.size - 2]
    val beam = cities
        .filter { it.index !in tabu && path.canAddCity(it.index) }
        .sortedBy { distanceMatrix[it.index][currentCity] }
        .take(30)

    var bestScore = -1
    var bestPath: Path? = null

    for (city in beam) {
        path.addCity(city.index)
        dfs(path, tabu + city.index)
        val newScore = path.score
        if (newScore > bestScore) {
            bestScore = newScore
            bestPath = path.copy()
            if (newScore > bestScoreSoFar) {
                bestScoreSoFar = newScore
                bestPathSoFar = path.cp()
                bestPathSoFar?.print()
            }
        }
        path.removeCity()
    }

    return bestPath
}
