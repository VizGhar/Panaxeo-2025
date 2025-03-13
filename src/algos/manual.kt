package algos

import core.Path
import java.io.File

/**
 * Manual mode - just prints the best path and lets user interact with map = adding / removing cities
 */
fun manual() {
    println(bestPath)
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
        .maxBy { it.score }
}
