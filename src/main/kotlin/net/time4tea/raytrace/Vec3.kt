package net.time4tea.raytrace

import kotlin.math.sqrt
import kotlin.random.Random

class Vec3(private var e0: Float, private var e1: Float, private var e2: Float) {

    companion object {

        fun UNIT() = Vec3(1.0f, 1.0f, 1.0f)
        fun ZERO() = Vec3(0f, 0f, 0f)

        fun random(): Vec3 = Vec3(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
        fun random_unit_sphere(): Vec3 {
            var p: Vec3
            do p = 2.0f * random() - UNIT()
            while (p.squared_length() >= 1.0)
            return p
        }
    }

    fun x(): Float = e0
    fun y(): Float = e1
    fun z(): Float = e2

    fun r(): Float = e0
    fun g(): Float = e1
    fun b(): Float = e2

    fun squared_length(): Float = e0 * e0 + e1 * e1 + e2 * e2
    fun length(): Float = kotlin.math.sqrt(squared_length())

    fun make_unit_vector() {
        val k = 1.0f / length()
        e0 *= k
        e1 *= k
        e2 *= k
    }

    operator fun unaryPlus() = this
    operator fun unaryMinus() = Vec3(-e0, -e1, -e2)

    operator fun plus(other: Vec3): Vec3 {
        return Vec3(e0 + other.e0, e1 + other.e1, e2 + other.e2)
    }

    operator fun plusAssign(other: Vec3) {
        e0 += other.e0
        e1 += other.e1
        e2 += other.e2
    }

    operator fun minus(other: Vec3): Vec3 {
        return Vec3(e0 - other.e0, e1 - other.e1, e2 - other.e2)
    }

    operator fun minusAssign(other: Vec3) {
        e0 -= other.e0
        e1 -= other.e1
        e2 -= other.e2
    }

    operator fun times(t: Float): Vec3 = Vec3(e0 * t, e1 * t, e2 * t)

    operator fun times(other: Vec3): Vec3 {
        return Vec3(e0 * other.e0, e1 * other.e1, e2 * other.e2)
    }

    operator fun timesAssign(other: Vec3) {
        e0 *= other.e0
        e1 *= other.e1
        e2 *= other.e2
    }

    operator fun div(other: Vec3): Vec3 {
        return Vec3(e0 / other.e0, e1 / other.e1, e2 / other.e2)
    }

    operator fun div(s: Float): Vec3 {
        return Vec3(e0 / s, e1 / s, e2 / s)
    }

    operator fun divAssign(other: Vec3) {
        e0 /= other.e0
        e1 /= other.e1
        e2 /= other.e2
    }

    operator fun timesAssign(f: Float) {
        e0 *= f
        e1 *= f
        e2 *= f
    }

    operator fun divAssign(f: Float) {
        e0 /= f
        e1 /= f
        e2 /= f
    }

    fun dot(other: Vec3): Float = e0 * other.e0 + e1 * other.e1 + e2 * other.e2

    fun cross(v2: Vec3): Vec3 {
        return Vec3(
            (e1 * v2.e2 - e2 * v2.e1),
            (-(e0 * v2.e2 - e2 * v2.e0)),
            (e0 * v2.e1 - e1 * v2.e0)
        )
    }

    fun unit(): Vec3 = this / length()

    fun reflect(n: Vec3): Vec3 {
        return this - n * (2 * this.dot(n))
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

    fun sqrt(): Vec3 {
        return Vec3(kotlin.math.sqrt(e0), kotlin.math.sqrt(e1), kotlin.math.sqrt(e2))
    }
}

operator fun Float.times(v: Vec3) = v * this