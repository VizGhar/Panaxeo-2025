package trash

import core.Path
import core.cities
import core.distanceMatrix

fun beam() {
    var a = listOf(Path())
    while (a.isNotEmpty()) {
        val nextLayer = mutableListOf<Path>()
        for (solve in a) {
            val candidates = cities
                .filter { it.index !in solve.path && solve.canAddCity(it.index) }
                .sortedBy { distanceMatrix[it.index][solve.path[solve.path.size - 2]] }
                .map { solve.cp().apply { addCity(it.index) } }
            nextLayer.addAll(candidates)
        }
        a = nextLayer.take(10000)
    }
    System.err.println("DONE")
}