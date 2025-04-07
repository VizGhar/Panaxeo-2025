package trash

import setPathUI
import java.io.File

// demo animation
fun demo() {
    File("demo").readLines()
        .forEach {
            setPathUI(it.split(",").map { it.toInt() })
            Thread.sleep(100)
        }
}