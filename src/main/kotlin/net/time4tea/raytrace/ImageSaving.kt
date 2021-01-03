package net.time4tea.raytrace

import net.time4tea.oidn.copyTo
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

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