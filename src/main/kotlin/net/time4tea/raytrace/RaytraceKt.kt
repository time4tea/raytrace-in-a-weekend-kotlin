package net.time4tea.raytrace

import net.time4tea.raytrace.scenes.weekend.WeekendFinal
import java.io.File
import java.time.Duration
import java.time.Instant
import javax.imageio.ImageIO


fun main() {
    val scene = WeekendFinal()
    val world = scene.scene()
    val lookfrom = scene.lookfrom()
    val lookat = scene.lookat()

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
