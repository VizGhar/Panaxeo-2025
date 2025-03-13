import core.City
import core.cities
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.math.hypot

private val panel by lazy { MapPanel("slovakia.png", minLat = 47.7258, minLng = 16.825, maxLat = 49.622, maxLng = 22.564) }

fun createUI() {
    val frame = JFrame("Panaxeo contest 2025")
    frame.add(panel)
    frame.pack()
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE)
    frame.isVisible = true
    frame.setSize(1770, 885)
}

fun setPathUI(path: List<Int>) { panel.path = path }

class MapPanel(
    imagePath: String,
    private val minLat: Double,
    private val minLng: Double,
    private val maxLat: Double,
    private val maxLng: Double
) : JPanel() {

    private var isHovering: City? = null

    private fun dist(x: Int, y: Int, x2: Int, y2: Int) =
        hypot(x.toDouble() - x2, y.toDouble() - y2)

    init {
        val icon = ImageIcon(imagePath)
        val label = JLabel(icon)
        add(label)

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val x = e.x
                val y = e.y
                println("mouse press - $bestPathSoFar")
                bestPathSoFar?.let { path ->
                    val city = cities.firstOrNull { city ->
                        val (x2, y2) = geoToPixel(city.lat, city.lon)
                        dist(x, y, x2, y2) < 20
                    }

                    if (city == null) return

                    if (path.path.contains(city.index)) {
                        path.removeCity(path.path.indexOf(city.index))
                        currentDistance = path.distance
                        bestScoreSoFar = path.score

                        repaint()
                        return
                    }

                    val bestIndex = path.bestIndexToAddCity(city)

                    if (bestIndex != null) {
                        path.addCity(city.index, bestIndex)
                        repaint()
                    }
                }
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val x = e.x
                val y = e.y

                isHovering = cities.firstOrNull { city ->
                    val (x2, y2) = geoToPixel(city.lat, city.lon)
                    dist(x, y, x2, y2) < 20
                }

                if (isHovering != null) {
                    repaint()
                }
            }
        })
    }

    private fun geoToPixel(lat: Double, lng: Double): Pair<Int, Int> {
        val imgWidth = width
        val imgHeight = height
        val x = (lng - minLng) / (maxLng - minLng) * imgWidth
        val y = (1 - (lat - minLat) / (maxLat - minLat)) * imgHeight
        return x.toInt() to y.toInt()
    }

    @get:Synchronized var path: List<Int> = listOf()
        set(value) {
            field = value
            repaint()
        }

    override fun paint(g: Graphics?) {
        super.paint(g)
        val pathPure = path.map { it }
        val g2 = g as Graphics2D
        for ((_, _, pop, lat, lng) in cities) {
            val p = geoToPixel(lat, lng)
            val amplify = pop.toDouble() / cities[10].population
            g2.color = Color((0xFF * (amplify)).toInt().coerceIn(0, 0xFF), 0, 0)
            g2.fillOval(p.first - 10, p.second - 10, 20, 20)
        }

        g2.drawString(bestScoreSoFar.toString(), 30, 30)
        g2.drawString(currentDistance.toString(), 30, 50)

        // Ak je kurzor nad kruhom, zobrazíme tooltip
        isHovering?.let {
            g2.color = Color.BLACK
            g2.font = Font("Roboto", 0, 24)
            val point = geoToPixel(it.lat, it.lon)
            g2.drawString("${it.index} - ${it.name}", point.first + 20, point.second)
        }

        g2.color = Color(0, 0x66, 0x66)
        g2.stroke = BasicStroke(3.0f)

        pathPure.map { geoToPixel(cities[it].lat, cities[it].lon) }
            .windowed(2)
            .forEach {
                g2.drawLine(it[0].first, it[0].second, it[1].first, it[1].second)
            }
    }
}