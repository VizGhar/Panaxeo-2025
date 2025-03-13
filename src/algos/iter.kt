package algos

import core.cities
import core.distanceMatrix

/**
 * Iterative improvement of given path. Take k-consequent cities and try all possible arrangements to shorten
 * given path. No cities are removed/added in this part. This algorithm will only shorten path if possible
 */
fun iterate(path: List<Int>) {
    val betterPath = dynamicKOpt(path, path.length / 2)

    if (betterPath.length < path.length) {
        println("Better found ${path.length} improved to ${betterPath.length}")
        println(betterPath)
    } else {
        println("Better not found")
    }
}

private typealias Path = List<Int>

private val Path.length: Int get() = windowed(2).sumOf { distanceMatrix[it[0]][it[1]] }
private val Path.score: Int get() = distinct().sumOf { cities[it].population }

private fun dynamicKOpt(path: Path, minK: Int = 2, maxK: Int = 5): Path {
    var bestPath = path
    var improved = false
    for (k in minK..maxK) {
        for (startIncluding in 36 - k .. 36 + k) {
            val subList = path.drop(startIncluding).take(k)
            val permutations = subList.permutations()
            for (c in permutations) {
                val newPath = path.take(startIncluding) + c + path.drop(startIncluding + k)
                if (newPath.length < bestPath.length) {
                    bestPath = newPath
                    improved = true
                    System.err.println("found - ${bestPath.length} - $bestPath")
                }
            }
        }
    }
    return if (improved) bestPath else path
}

private fun <T> List<T>.permutations(): Sequence<List<T>> = sequence {
    if (size <= 1) {
        yield(this@permutations)
    } else {
        for (i in indices) {
            val remaining = this@permutations - this@permutations[i]
            for (perm in remaining.permutations()) {
                yield(listOf(this@permutations[i]) + perm)
            }
        }
    }
}
