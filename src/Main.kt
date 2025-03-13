import algos.manual
import core.Path
import kotlin.time.measureTime

var currentDistance = -1
var bestScoreSoFar = -1
var bestPathSoFar: Path? = null

fun main() {
    createUI()
    val time = measureTime {
        manual()
    }

    println("Finished in $time")
}