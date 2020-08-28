package net.time4tea.raytrace

import net.time4tea.oidn.Oidn
import net.time4tea.oidn.OidnImages
import net.time4tea.raytrace.script.Scripting
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ControllableScene(
    val scenefn: () -> Scene,
    val executor: Executor,
    val display: Display,
    val start: (Vec3, Vec3, Vec3, Int) -> Unit,
    val complete: () -> Unit
) {

    var scene = scenefn()

    var lookfrom = scene.lookfrom()
    var lookat = scene.lookat()

    var dist_to_focus = 10.0f
    var aperture = 0.00f
    var fov = 40f

    val up = Vec3(0f, 1f, 0f)

    var samples = 2

    fun render() {
        executor.execute {
            try {
                val renderer = Renderer(scene.scene(), samples, 2, scene.constantLight())

                val aspect = display.size().width.toFloat() / display.size().height.toFloat()
                start(lookfrom, lookat, up, samples)
                renderer.render(
                    Camera(lookfrom, lookat, up, fov, aspect, aperture, dist_to_focus),
                    display
                )
                complete()
            } catch (e: Exception) {
                e.printStackTrace()
            } catch ( e: NotImplementedError ) {
                e.printStackTrace()
            }
        }
    }

    fun reload() {
        scene = scenefn()
        updated()
    }

    fun updated() {
        render()
    }

    fun moveCamera(x: (Vec3) -> Vec3) {
        lookfrom = x(lookfrom)
        updated()
    }

    fun samples(x: (Int) -> Int) {
        samples = x(samples)
        updated()
    }
}


fun main() {

    val oidn = Oidn()

    val image = OidnImages.newBufferedImage(800, 800)
    val display = BufferedImageDisplay(image)
    val oidnView = OidnView(oidn, display.image)

    val frame = SwingFrame(display.image, oidnView.image)

    val executor = Executors.newSingleThreadScheduledExecutor()

    val scaled = ScaledDisplay(1, display)

    val loader = Scripting()

    val file = File("src/main/kotlin/net/time4tea/raytrace/scenes/dynamic/Triangles.kts")
    var bob: Scene = loader.load(file)

    val controllableScene = ControllableScene({ bob }, executor, scaled, { lookfrom, lookat, up, samples ->
        println("Camera: LookFrom: $lookfrom  -> To: $lookat, Orientation: $up, Samples: $samples")
    }, { oidnView.copy() })

    var lastUpdate = 0L

    executor.scheduleWithFixedDelay(
        {
            val newUpdate = file.lastModified()
            if ( newUpdate != lastUpdate ) {
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


    frame.addKeyListener(object : KeyListener {
        override fun keyTyped(p0: KeyEvent) {
        }

        override fun keyPressed(event: KeyEvent) {
            val step = 50.0
            when (event.keyCode) {
                // needs to rotate around the look at really...
                KeyEvent.VK_RIGHT -> controllableScene.moveCamera { it.plus(Vec3(step, 0.0, 0.0)) }
                KeyEvent.VK_LEFT -> controllableScene.moveCamera { it.plus(Vec3(-step, 0.0, 0.0)) }
                KeyEvent.VK_UP -> controllableScene.moveCamera { it.plus(Vec3(0.0, -step, 0.0)) }
                KeyEvent.VK_DOWN -> controllableScene.moveCamera { it.plus(Vec3(0.0, step, 0.0)) }
                KeyEvent.VK_EQUALS -> controllableScene.moveCamera { it.plus(Vec3(0.0, 0.0, step)) }
                KeyEvent.VK_MINUS -> controllableScene.moveCamera { it.plus(Vec3(0.0, 0.0, -step)) }
                KeyEvent.VK_1 -> controllableScene.samples { it - 1 }
                KeyEvent.VK_2 -> controllableScene.samples { it + 1 }
            }
        }

        override fun keyReleased(p0: KeyEvent) {

        }
    })


    controllableScene.render()
}
