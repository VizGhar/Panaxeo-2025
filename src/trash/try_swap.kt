package trash

import core.cities
import core.distanceMatrix

/**
 * Small test whether there is no mistake in provided input that can't be caught visually.
 *
 * Try to
 * 1. remove city (A) at index (i)
 * 2. duplicate city (B)
 * 3. place city (A) between duplicates of (B)
 *
 * So you simply go there-and-back from one city to another.
 * Try every combination
 *
 * No success - distances on input are given properly
 */
fun main() {
    val original = listOf(91, 74, 99, 15, 86, 36, 138, 112, 9, 89, 19, 21, 41, 7, 100, 115, 4, 46, 117, 26, 83, 63, 12, 43, 24, 58, 8, 125, 35, 67, 55, 51, 62, 34, 98, 22, 108, 29, 6, 130, 75, 52, 107, 135, 87, 37, 110, 45, 132, 128, 79, 0, 111, 103, 60, 123, 30, 73, 65, 104, 17, 77, 13, 66, 105, 27, 47, 3, 72, 16, 92, 49, 94, 124, 137, 109, 42, 28, 131, 10, 44, 39, 5, 85, 11, 50, 76, 23, 68, 25, 119, 134, 118, 40, 71, 1, 84, 32, 97, 14, 31, 18, 81, 33, 64, 57, 101, 20, 59, 127, 2, 91)
    var changed = true
    var better = original

    while (changed) {
        changed = false
        val old = better
        better = improve(better)
        if (better != old) {
            println("Jupiii")
            println(better)
            println(better.score())
            println(better.joinToString(","){ cities[it].name })
            changed = true
        } else {
            println("Doprcic")
        }
    }
}

private fun improve(path: List<Int>): List<Int> {
    var shortestPathLength = path.score()
    var shortestPath = path
    for (i in 1..path.size - 2) {
        val removedCityIndex = path[i]
        val shortenedPath = path.take(i) + path.drop(i + 1)

        for (j in 1..shortenedPath.size - 2) {
            val copy = shortenedPath.toMutableList()
            copy.add(j - 1, shortenedPath[j - 1])
            copy.add(j, removedCityIndex)
            if (shortestPathLength >= copy.score()) {
                shortestPathLength = copy.score()
                shortestPath = copy
            }
        }
    }
    return shortestPath
}

private fun List<Int>.score() = this.windowed(2).sumOf { distanceMatrix[it[0]][it[1]] }

