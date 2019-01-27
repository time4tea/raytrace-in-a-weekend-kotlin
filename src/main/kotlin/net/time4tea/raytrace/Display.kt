package net.time4tea.raytrace

import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.Timer

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