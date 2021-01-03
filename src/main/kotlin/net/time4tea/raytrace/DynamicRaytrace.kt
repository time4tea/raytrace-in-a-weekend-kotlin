package net.time4tea.raytrace

import net.time4tea.oidn.Oidn
import net.time4tea.oidn.OidnImages
import net.time4tea.raytrace.script.Scripting
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


fun main() {

    val oidn = Oidn()

    val image = OidnImages.newBufferedImage(800, 800)
    val display = BufferedImageDisplay(image)
    val oidnView = OidnView(oidn, display.image)

    val frame = SwingFrame(display.image, oidnView.image)

    val executor = Executors.newSingleThreadScheduledExecutor()

    val scaled = ScaledDisplay(1, display)

    val loader = Scripting()

    val file = File("src/main/kotlin/net/time4tea/raytrace/scenes/dynamic/WeekendFinal.kts")
    var bob: Scene = loader.load(file)

    val controllableScene = ControllableScene({ bob }, executor, scaled, { lookfrom, lookat, up, samples, depth ->
        println("Camera: LookFrom: $lookfrom  -> \n\tTo: $lookat, \n\tOrientation: $up, \n\tSamples: $samples, \n\tDepth: $depth")
    }, { oidnView.copy() })

    var lastUpdate = 0L

    executor.scheduleWithFixedDelay(
        {
            val newUpdate = file.lastModified()
            if (newUpdate != lastUpdate) {
                lastUpdate = newUpdate
                try {
                    bob = loader.load(file)
                    controllableScene.reload()
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
            }
        },
        0L,
        1,
        TimeUnit.SECONDS
    )
    frame.addKeyListener(KeyMovement(controllableScene))

    controllableScene.render()
}
