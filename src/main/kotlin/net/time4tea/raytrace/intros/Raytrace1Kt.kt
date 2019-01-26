package net.time4tea.raytrace.intros

import net.time4tea.raytrace.BufferedImageDisplay
import net.time4tea.raytrace.SwingFrame
import net.time4tea.raytrace.Vec3

fun main() {
    val display = BufferedImageDisplay(200, 100)
    SwingFrame(display.image())

    val nx = display.size().width
    val ny = display.size().height

    for (j in 0 until ny) {
        for (i in 0 until nx) {
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