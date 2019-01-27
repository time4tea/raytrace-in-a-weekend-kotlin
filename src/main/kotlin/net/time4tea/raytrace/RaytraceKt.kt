package net.time4tea.raytrace

import net.time4tea.raytrace.scenes.weekend.WeekendFinal
import java.io.File
import java.time.Duration
import java.time.Instant
import javax.imageio.ImageIO


fun main() {
    val world = WeekendFinal().scene()
    val lookfrom = Vec3(13f, 2f, 3f)
    val lookat = Vec3(0f, 0f, 0f)

    val dist_to_focus = 10.0f
    val aperture = 0.10f

    val display = BufferedImageDisplay(640, 480)
    val aspect = display.size().width.toFloat() / display.size().height.toFloat()

    val camera = Camera(lookfrom, lookat, Vec3(0f, 1f, 0f), 20f, aspect, aperture, dist_to_focus)

    val renderer = Renderer(world, 50, 50)

    val image = display.image()

    SwingFrame(image)
    val start = Instant.now()

    renderer.render(camera, display)

    val duration = Duration.between(start, Instant.now())
    println("Duration was $duration")

    ImageIO.write(image, "PNG", File("output.png"))
}
