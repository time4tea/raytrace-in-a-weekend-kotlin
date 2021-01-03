package net.time4tea.raytrace

import net.time4tea.oidn.Oidn
import net.time4tea.oidn.OidnImages
import net.time4tea.raytrace.scenes.found.CornellGlassBoxes
import net.time4tea.raytrace.scenes.week.CornellBoxWithBox
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicReference


class ControllableScene(
    val scenefn: () -> Scene,
    val executor: ExecutorService,
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

    var samples = 10
    val max_depth = 100

    val submitted: AtomicReference<Future<*>> = AtomicReference(FutureTask { true })

    fun render() {
        println("Rendering")
        submitted.get().cancel(false)
        submitted.set(executor.submit {
            try {
                val renderer = Renderer(scene.scene(), samples, max_depth, scene.constantLight())

                val aspect = display.size().width.toFloat() / display.size().height.toFloat()
                start(lookfrom, lookat, up, samples)
                renderer.render(
                    Camera(lookfrom, lookat, up, fov, aspect, aperture, dist_to_focus),
                    display
                )
                complete()
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: NotImplementedError) {
                e.printStackTrace()
            }
        })
    }

    fun reload() {
        scene = scenefn()
        updated()
    }

    fun updated() {
        render()
    }

    fun moveCamera(x: (Vec3, Vec3) -> Vec3) {
        lookfrom = x(lookfrom, lookat)
        updated()
    }

    fun samples(x: (Int) -> Int) {
        samples = x(samples)
        updated()
    }
}

fun main() {

    val oidn = Oidn()

    val image = OidnImages.newBufferedImage(600, 600)
    val display = BufferedImageDisplay(image)
    val oidnView = OidnView(oidn, display.image)

    val frame = SwingFrame(display.image, oidnView.image)

    val executor = Executors.newSingleThreadScheduledExecutor()

    val scaled = ScaledDisplay(1, display)

    val bob: Scene = CornellBoxWithBox()

    val controllableScene = ControllableScene({ bob }, executor, scaled, { lookfrom, lookat, up, samples ->
        println("Camera: LookFrom: $lookfrom  -> To: $lookat, Orientation: $up, Samples: $samples")
    }, { oidnView.copy() })

    frame.addKeyListener(KeyMovement(controllableScene))


    controllableScene.render()
}
