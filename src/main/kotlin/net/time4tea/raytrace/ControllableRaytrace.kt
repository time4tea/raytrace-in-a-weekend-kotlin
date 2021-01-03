package net.time4tea.raytrace

import net.time4tea.oidn.Oidn
import net.time4tea.oidn.OidnImages
import net.time4tea.oidn.copyTo
import net.time4tea.raytrace.scenes.week.WeekFinal
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicReference
import javax.imageio.ImageIO


class ControllableScene(
    val scenefn: () -> Scene,
    val executor: ExecutorService,
    val display: Display,
    val start: (Vec3, Vec3, Vec3, Int, Int) -> Unit,
    val complete: () -> Unit
) {

    var scene = scenefn()

    var lookfrom = scene.lookfrom()
    var lookat = scene.lookat()

    var dist_to_focus = 10.0f
    var aperture = 0.00f
    var fov = 40f

    val up = Vec3(0f, 1f, 0f)

    var samples = 20
    var max_depth = 10

    val submitted: AtomicReference<Future<*>> = AtomicReference(FutureTask { true })

    fun render() {
        submitted.get().cancel(false)
        submitted.set(executor.submit {
            println("Rendering...")
            try {
                val renderer = Renderer(scene.scene(), samples, max_depth, scene.constantLight())

                val aspect = display.size().width.toFloat() / display.size().height.toFloat()
                start(lookfrom, lookat, up, samples, max_depth)
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

    fun depth(x: (Int) -> Int) {
        max_depth = x(max_depth)
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

    val scene = WeekFinal()

    val controllableScene = ControllableScene({ scene }, executor, scaled, { lookfrom, lookat, up, samples, depth ->
        println("Camera: LookFrom: $lookfrom  -> To: $lookat, \n\tOrientation: $up, \n\tSamples: $samples, \n\tMax Depth: $depth")
    }, {
        oidnView.copy()
    })

    frame.addKeyListener(KeyMovement(controllableScene))
    frame.addKeyListener(
        KeySaving(
            scene, mapOf(
                "rendered" to display.image,
                "denoised" to oidnView.image
            )
        )
    )

    controllableScene.render()
}

class ImageSaving {
    fun save(image: BufferedImage, file: File) {
        val dest = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        try {
            image.copyTo(dest)
            ImageIO.write(dest, "PNG", file)
        } finally {
            dest.flush()
        }
    }
}

class KeySaving(scene: Scene, private val images: Map<String, BufferedImage>) : KeyListener {
    private val sceneName = scene.javaClass.simpleName.toLowerCase()
    private val saving = ImageSaving()

    override fun keyTyped(e: KeyEvent) {

    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_S -> {
                images.forEach { (n, i) ->
                    val file = File("example-output/$sceneName-$n.png")
                    println("Saving $n -> $file")
                    saving.save(i, file)
                }
            }
        }
    }

    override fun keyReleased(e: KeyEvent) {

    }
}

