package net.time4tea.raytrace

import net.time4tea.raytrace.scenes.week.WeekFinal
import java.io.File
import java.time.Duration
import java.time.Instant
import javax.imageio.ImageIO


fun main() {
    val scene = WeekFinal()
    val world = scene.scene()
    val lookfrom = scene.lookfrom()
    val lookat = scene.lookat()

    val dist_to_focus = 10.0f
    val aperture = 0.00f
    val fov = 40f

    val display = BufferedImageDisplay(1024, 768)
    val aspect = display.size().width.toFloat() / display.size().height.toFloat()

    val camera = Camera(lookfrom, lookat, Vec3(0f, 1f, 0f), fov, aspect, aperture, dist_to_focus)

    val renderer = Renderer(world, 100, 50, scene.constantLight())

    val image = display.image()

    SwingFrame(image)
    val start = Instant.now()

    renderer.render(camera, ScaledDisplay(1, display))

    val duration = Duration.between(start, Instant.now())
    println("Duration was $duration")

    ImageIO.write(image, "PNG", File("output.png"))
}
