package net.time4tea.raytrace

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.random.Random

class Vec3(val x: Float, val y: Float, val z: Float) {

    constructor(x: Double, y: Double, z: Double) : this(x.toFloat(), y.toFloat(), z.toFloat())
    constructor(x: Int, y: Int, z: Int) : this(x.toFloat(), y.toFloat(), z.toFloat())

    companion object {

        fun xx(): (Vec3) -> Float = { it.x }
        fun yy(): (Vec3) -> Float = { it.y }
        fun zz(): (Vec3) -> Float = { it.z }

        val UNIT = Vec3(1.0f, 1.0f, 1.0f)
        val ZERO = Vec3(0f, 0f, 0f)

        fun random(): Vec3 = Vec3(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
        fun random_unit_sphere(): Vec3 {
            var p: Vec3
            do p = 2.0f * random() - UNIT
            while (p.squared_length() >= 1.0)
            return p
        }
    }

    fun squared_length(): Float = x * x + y * y + z * z
    fun length(): Float = sqrt(squared_length())

    operator fun unaryPlus() = this
    operator fun unaryMinus() = Vec3(
        -x,
        -y,
        -z
    )

    operator fun plus(other: Vec3): Vec3 {
        return Vec3(
            x + other.x,
            y + other.y,
            z + other.z
        )
    }

    operator fun minus(other: Vec3): Vec3 {
        return Vec3(
            x - other.x,
            y - other.y,
            z - other.z
        )
    }

    operator fun times(t: Float): Vec3 = Vec3(
        x * t,
        y * t,
        z * t
    )

    operator fun times(other: Vec3): Vec3 {
        return Vec3(
            x * other.x,
            y * other.y,
            z * other.z
        )
    }

    operator fun div(other: Vec3): Vec3 {
        return Vec3(
            x / other.x,
            y / other.y,
            z / other.z
        )
    }

    operator fun div(s: Float): Vec3 {
        return Vec3(
            x / s,
            y / s,
            z / s
        )
    }

    operator fun div(s: Int): Vec3 {
        return Vec3(
            x / s,
            y / s,
            z / s
        )
    }

    fun withX(x: Float): Vec3 {
        return Vec3(x, y, z)
    }

    fun withY(y: Float): Vec3 {
        return Vec3(x, y, z)
    }

    fun withZ(z: Float): Vec3 {
        return Vec3(x, y, z)
    }

    fun dot(other: Vec3): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun cross(v2: Vec3): Vec3 {
        return Vec3(
            (y * v2.z - z * v2.y),
            (-(x * v2.z - z * v2.x)),
            (x * v2.y - y * v2.x)
        )
    }

    fun min(v2: Vec3): Vec3 {
        return Vec3(
            kotlin.math.min(x,v2.x),
            kotlin.math.min(y,v2.y),
            kotlin.math.min(z,v2.z)
        )
    }

    fun max(v2: Vec3): Vec3 {
        return Vec3(
            kotlin.math.max(x,v2.x),
            kotlin.math.max(y,v2.y),
            kotlin.math.max(z,v2.z)
        )
    }

    fun unit(): Vec3 = this / length()

    fun reflect(n: Vec3): Vec3 {
        return this - 2 * this.dot(n) * n
    }

    fun refract(n: Vec3, ni_over_nt: Float): Vec3? {
        val uv = unit()
        val dt = uv.dot(n)
        val discriminant = (1.0 - ni_over_nt * ni_over_nt * (1 - dt * dt)).toFloat()
        return if (discriminant > 0) {
            ni_over_nt * (uv - n * dt) - n * sqrt(discriminant)
        } else {
            null
        }
    }

    data class UV(val u: Float, val v: Float)

    fun sphere_uv(): UV {
        val phi = atan2(z, x)
        val theta = asin(y)
        val u = 1 - (phi + PI) / (2 * PI)
        val v = (theta + PI / 2) / PI
        return UV(u.toFloat(), v.toFloat())
    }

    override fun toString(): String {
        return "Vec3(x=$x, y=$y, z=$z)"
    }
}

operator fun Float.times(v: Vec3) = v * this