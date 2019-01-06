package net.time4tea.raytrace

import kotlinx.coroutines.*
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.Timer
import kotlin.random.Random

interface Display {
    fun size(): Dimension
    fun plot(x: Int, y: Int, colour: Vec3)
}

class Renderer(private val world: Hitable, private val ns: Int) {

    companion object {
        private fun colour(ray: Ray, world: Hitable, depth: Int): Vec3 {
            val hit = world.hit(ray, 0.001f, Float.MAX_VALUE)
            return if (hit != null) {
                if (depth < 50) {
                    hit.material.scatter(ray, hit)?.let { (attenuation, scattered) ->
                        attenuation * colour(scattered, world, depth + 1)
                    } ?: Vec3.ZERO()
                } else {
                    Vec3.ZERO()
                }

            } else {
                val unit_direction = ray.direction().unit()
                val t = 0.5f * (unit_direction.y() + 1.0f)

                (1.0f - t) * Vec3.UNIT() * t * Vec3(0.5f, 0.7f, 1.0f)
            }
        }
    }

    fun render(camera: Camera, display: Display) {

        val nx = display.size().width
        val ny = display.size().height

        val jobs = mutableListOf<Deferred<Pair<Int, List<Vec3>>>>()

        for (j in 0 until ny) {
            jobs.add(GlobalScope.async {
                val row = mutableListOf<Vec3>()

                for (i in 0 until nx) {
                    val col = Vec3(0f, 0f, 0f)
                    for (s in 0..ns) {
                        val u = (i + Random.nextFloat()) / nx.toFloat()
                        val v = (j + Random.nextFloat()) / ny.toFloat()
                        col += colour(camera.get_ray(u, v), world, 0)
                    }

                    col /= ns.toFloat()

                    row.add(col.sqrt())
                }

                Pair(j, row)
            })
        }

        runBlocking {
            jobs.forEach {
                val result = it.await()
                for ((index, colour) in result.second.withIndex()) {
                    display.plot(index, result.first, colour)
                }
            }
        }
    }
}

class SwingFrame(image: BufferedImage) : JFrame() {

    private val icon = JLabel(ImageIcon(image))
    private val timer = Timer(50) { icon.repaint() }

    init {
        title = "something"
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(icon)

        timer.start()
    }
}

class BufferedImageDisplay(private val width: Int, private val height: Int) : Display {

    private val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    override fun size(): Dimension {
        return Dimension(width, height)
    }

    override fun plot(x: Int, y: Int, colour: Vec3) {
        bufferedImage.setRGB(x, height - (y + 1), Color(colour.r(), colour.g(), colour.b()).rgb)
    }

    fun image(): BufferedImage {
        return bufferedImage
    }
}

fun random_scene(): Hitable {

    val list = mutableListOf<Hitable>()

    for (a in -11..11) {
        for (b in -11..11) {
            val choose_mat = Random.nextFloat()

            val centre = Vec3(a + 0.9f * Random.nextFloat(), 0.2f, b + 0.9f * Random.nextFloat())
            if ((centre - Vec3(4.0f, 0.2f, 0.0f)).length() > 0.9) {

                val material = when {
                    choose_mat < 0.8 -> Lambertian(Vec3(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()))
                    choose_mat < 0.95 -> Metal(
                        Vec3(
                            0.5f * (1f + Random.nextFloat()),
                            0.5f * (1f + Random.nextFloat()),
                            0.5f * (1f + Random.nextFloat())
                        ), 0.5f * Random.nextFloat()
                    )
                    else -> Dielectric(1.5f)
                }

                list.add(Sphere(centre, 0.2f, material))
            }
        }
    }
    return HitableList(list)
}

fun main() {

    val biggies = HitableList(
        listOf(
            Sphere(Vec3(0f, -1000f, 0f), 1000f, Lambertian(Vec3(0.5f, 0.5f, 0.5f))),
            Sphere(Vec3(0f, 1f, 0f), 1.0f, Dielectric(1.5f)),
            Sphere(Vec3(-4f, 1f, 0f), 1.0f, Lambertian(Vec3(0.4f, 0.2f, 0.1f))),
            Sphere(Vec3(4f, 1f, 0f), 1.0f, Metal(Vec3(0.7f, 0.6f, 0.5f), 0.0f))
        )
    )

    val randoms = random_scene()

    val world = HitableList(listOf(biggies, randoms))

    val lookfrom = Vec3(13f, 2f, 3f)
    val lookat = Vec3(0f, 0f, 0f)

    val dist_to_focus = 10.0f
    val aperture = 0.01f

    val display = BufferedImageDisplay(1200, 800)

    val aspect = display.size().width.toFloat() / display.size().height.toFloat()

    val camera = Camera(lookfrom, lookat, Vec3(0f, 1f, 0f), 20f, aspect, aperture, dist_to_focus)

    val renderer = Renderer(world, 40)

    val frame = SwingFrame(display.image())

    frame.pack()
    frame.isVisible = true

    renderer.render(camera, display)
}