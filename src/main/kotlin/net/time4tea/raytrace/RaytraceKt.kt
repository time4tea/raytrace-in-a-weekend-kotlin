package net.time4tea.raytrace

import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import java.time.Duration
import java.time.Instant
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.Timer
import kotlin.random.Random

interface Display {
    fun size(): Dimension
    fun plot(x: Int, y: Int, colour: Vec3)
}

class SwingFrame(image: BufferedImage) : JFrame() {

    private val icon = JLabel(ImageIcon(image))
    private val timer = Timer(50) { icon.repaint() }

    init {
        title = "something"
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(icon)

        timer.start()

        pack()
        isVisible = true
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

fun random_scene(): List<Hitable> {

    val list = mutableListOf<Hitable>()

    for (a in -11..11) {
        for (b in -11..11) {
            val choose_mat = Random.nextFloat()

            val centre = Vec3(a + 0.9f * Random.nextFloat(), 0.2f, b + 0.9f * Random.nextFloat())
            if ((centre - Vec3(4.0f, 0.2f, 0.0f)).length() > 0.9) {

                val material = when {
                    choose_mat < 0.8 -> Metal(
                        Vec3(
                            0.5f * (1f + Random.nextFloat()),
                            0.5f * (1f + Random.nextFloat()),
                            0.5f * (1f + Random.nextFloat())
                        ), 0.5f * Random.nextFloat()
                    )
                    choose_mat < 0.95 -> Lambertian(
                        ConstantTexture(Vec3(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()))
                    )
                    else -> Dielectric(1.5f)
                }

                list.add(Sphere(centre, 0.2f, material))
            }
        }
    }
    return list//.map { MetricHitable(Metrics.globalRegistry, it)}
}

fun main() {

    val green_white = CheckerTexture(
        ConstantTexture(Vec3(0.2, 0.3, 0.1)),
        ConstantTexture(Vec3(0.9, 0.9, 0.9))
    )

    val earth = ImageTexture(File("src/main/resources/earth.jpg"))

    val hitables = listOf(
        Sphere(
            Vec3(0f, -1000f, 0f), 1000f, Lambertian(green_white)
        ),
        Sphere(Vec3(0f, 1f, 0f), 1.0f, Dielectric(1.5f)),
        Sphere(
            Vec3(-4f, 1f, 0f), 1.0f, Lambertian(
                earth
            )
        ),
        Sphere(Vec3(4f, 1f, 0f), 1.0f, Metal(Vec3(0.7f, 0.6f, 0.5f), 0.0f))
    ) + random_scene()

    println("Items : ${hitables.size}")
    val world = BVH(hitables)

    println("BVH Items : ${world.components()}")
    val lookfrom = Vec3(13f, 2f, 3f)
    val lookat = Vec3(0f, 0f, 0f)

    val dist_to_focus = 10.0f
    val aperture = 0.10f

    val display = BufferedImageDisplay(1200, 800)

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
