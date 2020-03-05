package net.time4tea.raytrace

import java.awt.Dimension
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.Timer

interface Display {
    fun size(): Dimension
    fun plot(x: Int, y: Int, colour: Colour)
}

class SwingFrame(image: BufferedImage) : JFrame() {

    private val icon = JLabel(ImageIcon(image))
    private val timer = Timer(100) { icon.repaint() }

    init {
        title = "Raytrace in a week/weekend"
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane.add(icon)

        timer.start()

        pack()
        isVisible = true
    }
}

class ScaledDisplay(private val scale: Int, private val delegate: Display) : Display {

    private val display: Display

    init {
        if (scale == 1) {
            this.display = delegate
        } else {
            display = object : Display {
                override fun size(): Dimension {
                    val original = delegate.size()
                    return Dimension(original.width / scale, original.height / scale)
                }

                override fun plot(x: Int, y: Int, colour: Colour) {
                    for (pix in x * scale until (x + 1) * scale) {
                        for (piy in y * scale until (y + 1) * scale) {
                            delegate.plot(pix, piy, colour)
                        }
                    }
                }
            }
        }
    }

    override fun size(): Dimension {
        return display.size()
    }

    override fun plot(x: Int, y: Int, colour: Colour) {
        display.plot(x, y, colour)
    }
}

class BufferedImageDisplay(val image: BufferedImage) : Display {

    private val width = image.width
    private val height = image.height

    constructor(width: Int, height: Int) : this(BufferedImage(width, height, BufferedImage.TYPE_INT_RGB))

    override fun size(): Dimension {
        return Dimension(width, height)
    }

    override fun plot(x: Int, y: Int, colour: Colour) {
        assert(x <= width) { "$x > $width" }
        assert(y <= height) { "$y > $height" }
        image.setRGB(x, height - (y + 1), colour.asColor().rgb)
    }
}