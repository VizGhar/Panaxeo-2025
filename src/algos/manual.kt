package algos

import bestScoreSoFar
import core.Path
import setPathUI
import java.io.File

/**
 * Manual mode - just prints the best path and lets user interact with map = adding / removing cities
 */
fun manual() {
    println(bestPath)
    setPathUI(bestPath.path)
    bestScoreSoFar = 2763991
}

private val bestPath by lazy {
    File("out.txt").readLines()
        .map {
            val path = it.substringAfter("-").split(",").map { it.toInt() }.drop(1).dropLast(1)
            val r = Path()
            for (p in path) {
                r.addCity(p)
            }
            r
        }
        .first { it.score == 2763991 }
}
