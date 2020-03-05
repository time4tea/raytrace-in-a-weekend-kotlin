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

    val display = BufferedImageDisplay(800, 800)
    val aspect = display.size().width.toFloat() / display.size().height.toFloat()

    val camera = Camera(lookfrom, lookat, Vec3(0f, 1f, 0f), fov, aspect, aperture, dist_to_focus)

    val renderer = Renderer(world, 100, 10, scene.constantLight())

    val image = display.image()

    SwingFrame(image)

    val scaled = ScaledDisplay(1, display)

    val start = Instant.now()
    renderer.render(camera, scaled)

    val duration = Duration.between(start, Instant.now())
    println("Duration was $duration")

    val scene_name = scene.javaClass.simpleName.toLowerCase()
    ImageIO.write(image, "PNG", File("example-output/$scene_name.png"))
}
