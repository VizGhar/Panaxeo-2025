import core.City
import core.base
import core.cities
import core.distanceMatrix
import java.io.File

/**
 * This is by far the best step - Use C++ concorde library to find solutions, but...
 *
 * Concorde is solving only Symmetrical TSP problem, so it has to know all cities. My approach is
 *
 * 1. Pick the best solution found so far (combination of SA + iterative / manual)
 * 2. Try to replace each city by another better city
 * 3. Run concorde on new set of cities
 * 4. Catch better results
 *
 * This one brought me 2nd place together with somebody that found this solution faster
 *
 * TODO: replace tuples of cities
 */
fun main() {
    val includedCities = concordeBestPath
        .distinct()
        .map { index -> cities.first { it.index == index } }

    concordeBestScore = includedCities.score()

    println("initial score = $concordeBestScore")

    val excludedCities = (cities - includedCities)

    for (tuple in 1..5) {
        System.err.println("Try tuple size of $tuple")
        test(includedCities, excludedCities, 2)
    }

    println("BEST solution ($concordeBestScore) found:")
    println(concordeBestPath)
}

private var concordeBestPath = listOf(91,2,127,59,20,101,57,64,31,18,33,81,14,97,32,84,1,71,40,118,134,119,25,68,23,76,50,11,85,5,39,44,10,131,28,42,109,137,124,94,49,92,16,72,3,116,22,98,108,29,6,130,75,110,37,87,135,107,52,47,27,105,66,13,77,17,104,65,73,30,123,60,103,111,0,79,128,45,132,62,51,34,55,67,35,8,58,24,43,12,63,83,26,117,46,4,115,100,7,41,21,19,89,9,112,138,36,86,15,99,74)
private var concordeBestScore = 0

private fun List<City>.score() = sumOf { it.population }

/**
 * Run test on given set of included / excluded cities,
 * where replacing [tupleSize] cities from [includedCities] by another [tupleSize] cities from [excludedCities]
 */
private fun test(
    includedCities: List<City>,
    excludedCities: List<City>,
    tupleSize: Int,
    removed: List<Int> = emptyList(),
    added: List<Int> = emptyList()
) {
    // can't remove Gelnica
    if (removed.any { includedCities[it] == base }) return

    // reached tuple size
    if (removed.size == tupleSize) {
        val newSet = includedCities - removed.map { includedCities[it] }.toSet() + added.map { excludedCities[it] }

        // solution won't be better - skip
        if (newSet.sumOf { it.population } < concordeBestScore) {
            return
        }

        composeTest(newSet)
        test(newSet)
        return
    }

    // recursively deep dive into test
    val lowestIndexToRemove = removed.lastOrNull()?.plus(1) ?: 0
    val lowestIndexToAdd = added.lastOrNull()?.plus(1) ?: 0

    for (rem in lowestIndexToRemove..<includedCities.size) {
        for (add in lowestIndexToAdd..<excludedCities.size) {
            test(includedCities, excludedCities, tupleSize, removed + rem, added + add)
        }
    }
}

/**
 * Compose test Concorde .TSP file
 */
private fun composeTest(
    cities: List<City>,
    output: String = "input.tsp"
) {
    File(output).writer()
        .append("NAME: panaxeo2025\n" +
                "TYPE: TSP\n" +
                "DIMENSION: ${cities.size}\n" +
                "EDGE_WEIGHT_TYPE: EXPLICIT\n" +
                "EDGE_WEIGHT_FORMAT: FULL_MATRIX\n" +
                "EDGE_WEIGHT_SECTION\n")
        .append(
            cities.joinToString("\n") { cityFrom ->
                cities.joinToString(" ") { cityTo ->
                    distanceMatrix[cityFrom.index][cityTo.index].toString()
                }
            }
        )
        .append("\n")
        .append("EOF\n")
        .flush()
}

/**
 * Run and evaluate Concorde .TSP
 */
private fun test(
    cityList: List<City>,
    input: String = "input.tsp",
    output: String = "result.sol"
) {
    val result = Runtime
        .getRuntime()
        .exec("/home/vizghar/concorde/src/concorde_build/TSP/concorde -o $output $input")
        .inputStream
        .reader()
        .readLines()

    val r = result.joinToString(" ").substringAfter("Optimal Solution: ").substringBefore(".").toInt()

    if (r <= 1500) {
        val symbols =
            File(output)
                .readLines().drop(1).joinToString("") { it.replace(" ", ",") }
                .trimEnd(',')
                .split(",")
                .map { cityList[it.toInt()] }

        val pathEnding = symbols.takeWhile { it.name != "Gelnica" }
        val pathStart = symbols.drop(pathEnding.size)
        val adjustedPath = pathStart + pathEnding + base
        val score = cityList.score()

        val singleLine = adjustedPath.joinToString(","){ it.name }
        println("SOLUTION score (${score}); length ($r) FOUND:")
        println(singleLine)
        concordeBestScore = score
        concordeBestPath = adjustedPath.map { cities.indexOf(it) }
    }
    File(input).delete()
    File(output).delete()
    File("input.mas").delete()
    File("input.pul").delete()
    File("input.sav").delete()
    File("input.sol").delete()
    File("Oinput.mas").delete()
    File("Oinput.pul").delete()
    File("Oinput.sav").delete()
}
