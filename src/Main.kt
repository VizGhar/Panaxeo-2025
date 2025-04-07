import algos.manual
import algos.sa
import core.Path
import trash.demo
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