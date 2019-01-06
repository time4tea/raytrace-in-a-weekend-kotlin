package net.time4tea.raytrace

fun main() {
    val display = BufferedImageDisplay(200, 100)
    SwingFrame(display.image())

    val nx = display.size().width
    val ny = display.size().height

    for (j in 0..ny-1) {
        for (i in 0..nx-1) {
            display.plot(
                i,
                j,
                Vec3(
                    i.toFloat() / nx.toFloat(),
                    j.toFloat() / ny.toFloat(),
                    0.2f
                )
            )
        }
    }
}