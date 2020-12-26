package net.time4tea.raytrace

import net.time4tea.oidn.Oidn
import net.time4tea.oidn.OidnImages
import net.time4tea.oidn.copyTo
import net.time4tea.raytrace.scenes.week.WeekFinal
import net.time4tea.raytrace.scenes.weekend.WeekendFinal
import java.awt.image.BufferedImage
import java.io.File
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO


class OidnView(oidn: Oidn, private val source: BufferedImage) {

    val image = OidnImages.newBufferedImageFrom(source)

    private val colour = Oidn.allocateBuffer(width = this.source.width, height = this.source.height)
    private val output = Oidn.allocateBuffer(width = this.source.width, height = this.source.height)

    private val device = oidn.newDevice(Oidn.DeviceType.DEVICE_TYPE_DEFAULT)

    private val filter = device.raytraceFilter().also { filter ->
        filter.setFilterImage(
            colour, output, this.source.width, this.source.height
        )
        filter.commit()
    }

    fun stop(process: Boolean = false) {
        if (process) copy()
        synchronized(device) {
            filter.close()
            device.close()
        }
    }

    fun copy() {
        source.copyTo(colour)
        synchronized(device) {
            filter.execute()
        }
        output.copyTo(image)
    }
}

fun main() {

    val oidn = Oidn()

    val scene = WeekendFinal()
    val world = scene.scene()
    val lookfrom = scene.lookfrom()
    val lookat = scene.lookat()

    val dist_to_focus = 10.0f
    val aperture = 0.1f
    val fov = 40f

    val image = OidnImages.newBufferedImage(800, 800)
    val display = BufferedImageDisplay(image)
    val executorService = Executors.newSingleThreadScheduledExecutor()
    val oidnView = OidnView(oidn, display.image)

    val scheduled = executorService.scheduleWithFixedDelay({ oidnView.copy() }, 0, 2000, TimeUnit.MILLISECONDS)

    try {
        val aspect = display.size().width.toFloat() / display.size().height.toFloat()

        val camera = Camera(lookfrom, lookat, Vec3(0f, 1f, 0f), fov, aspect, aperture, dist_to_focus)

        val renderer = Renderer(world, 100, 10, scene.constantLight())

        SwingFrame(display.image, oidnView.image)

        val scaled = ScaledDisplay(1, display)

        val start = Instant.now()
        renderer.render(camera, scaled)
        val duration = Duration.between(start, Instant.now())

        println("Duration was $duration")

    } finally {
        scheduled.cancel(true)
        oidnView.stop(true)
    }

    val scene_name = scene.javaClass.simpleName.toLowerCase()
    ImageIO.write(display.image, "PNG", File("example-output/$scene_name.png"))
    ImageIO.write(oidnView.image, "PNG", File("example-output/$scene_name-denoise.png"))

}
