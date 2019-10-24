package net.time4tea.raytrace

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.sin

interface Texture {
    fun value(u: Float, v: Float, p: Vec3): Vec3
}

class ConstantTexture(private val colour: Vec3) : Texture {
    override fun value(u: Float, v: Float, p: Vec3): Vec3 {
        return colour
    }
}

class CheckerTexture(private val t1: Texture, private val t2: Texture) : Texture {
    override fun value(u: Float, v: Float, p: Vec3): Vec3 {
        val sines = sin(10 * p.x()) * sin(10 * p.y()) * sin(10 * p.z())
        return if (sines < 0) {
            t1.value(u, v, p)
        } else {
            t2.value(u, v, p)
        }
    }
}

class NoiseTexture(private val scale: Float) : Texture {

    private val noise = Perlin()

    override fun value(u: Float, v: Float, p: Vec3): Vec3 {
        return Vec3.UNIT * 0.5f * (1 + sin(scale * p.x() + 5 * noise.turb(scale * p)))
    }
}

class ImageTexture(file: File) : Texture {

    private val image: BufferedImage = ImageIO.read(file)
    private val width = image.width
    private val height = image.height

    override fun value(u: Float, v: Float, p: Vec3): Vec3 {
        var x = (u * width).toInt()
        var y = ((1 - v) * height).toInt()

        if (x < 0) x = 0
        if (y < 0) y = 0

        if (x > width - 1) x = width - 1
        if (y > height - 1) y = height - 1

        val pixel = Color(image.getRGB(x, y))
        return Vec3(pixel.red / 255.0, pixel.green / 255.0, pixel.blue / 255.0)
    }
}