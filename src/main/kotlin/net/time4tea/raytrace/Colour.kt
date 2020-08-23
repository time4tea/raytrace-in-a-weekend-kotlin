package net.time4tea.raytrace

import java.awt.Color
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class Colour(val r: Float, val g: Float, val b: Float) {

    constructor(r: Double, g: Double, b: Double) : this(r.toFloat(), g.toFloat(), b.toFloat())

    companion object {
        val WHITE = Colour(1.0f, 1.0f, 1.0f)
        val BLACK = Colour(0f, 0f, 0f)

        fun random(): Colour = Colour(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())

        fun from(hex: String): Colour {
            return from(Color.decode(hex))
        }

        fun from(color: Color):Colour {
            return Colour(color.red / 256.0, color.green / 256.0, color.blue / 256.0)
        }
    }

    operator fun unaryPlus() = this
    operator fun unaryMinus() = Colour(
        -r,
        -g,
        -b
    )

    operator fun plus(other: Colour): Colour {
        return Colour(
            r + other.r,
            g + other.g,
            b + other.b
        )
    }

    operator fun minus(other: Colour): Colour {
        return Colour(
            r - other.r,
            g - other.g,
            b - other.b
        )
    }

    operator fun times(t: Float): Colour = Colour(
        r * t,
        g * t,
        b * t
    )

    operator fun times(other: Colour): Colour {
        return Colour(
            r * other.r,
            g * other.g,
            b * other.b
        )
    }

    operator fun div(other: Colour): Colour {
        return Colour(
            r / other.r,
            g / other.g,
            b / other.b
        )
    }

    operator fun div(s: Float): Colour {
        return Colour(
            r / s,
            g / s,
            b / s
        )
    }

    operator fun div(s: Int): Colour {
        return Colour(
            r / s,
            g / s,
            b / s
        )
    }

    fun sqrt(): Colour {
        return Colour(
            sqrt(r),
            sqrt(g),
            sqrt(b)
        )
    }

    override fun toString(): String {
        return "Colour(e0=$r, e1=$g, e2=$b)"
    }

    fun asColor() = Color(
        min(r, 1.0f),
        min(g, 1.0f),
        min(b, 1.0f)
    )
}

operator fun Float.times(v: Colour) = v * this