package net.time4tea.raytrace

import kotlin.math.cos
import kotlin.math.sin

sealed class Angle(var n: Double) {
    class Deg(deg: Double): Angle(deg) {
        override fun deg(): Deg {
            return this
        }

        override fun rad(): Rad {
            return Rad(Math.toRadians(n))
        }
    }

    class Rad(n: Double): Angle(n) {
        override fun deg(): Deg {
            return Deg(Math.toDegrees(n))
        }

        override fun rad(): Rad {
            return this
        }
    }

    abstract fun deg(): Deg
    abstract fun rad(): Rad

    fun sin(): Float {
        return sin(rad().n).toFloat()
    }

    fun cos(): Float {
        return cos(rad().n).toFloat()
    }
}

class Mat4(
    val m00: Float,
    val m01: Float,
    val m02: Float,
    val m03: Float,
    val m10: Float,
    val m11: Float,
    val m12: Float,
    val m13: Float,
    val m20: Float,
    val m21: Float,
    val m22: Float,
    val m23: Float,
    val m30: Float,
    val m31: Float,
    val m32: Float,
    val m33: Float
) {

    fun transform(point: Vec3): Vec3 {
        return Vec3(
            x = m00 * point.x + m01 * point.y + m02 * point.z + m03,
            y = m10 * point.x + m11 * point.y + m12 * point.z + m13,
            z = m20 * point.x + m21 * point.y + m22 * point.z + m23
        )
    }

    operator fun times(m1: Mat4): Mat4 {
        return Mat4(
            m00 = m00 * m1.m00 + m01 * m1.m10 + m02 * m1.m20 + m03 * m1.m30,
            m01 = m00 * m1.m01 + m01 * m1.m11 + m02 * m1.m21 + m03 * m1.m31,
            m02 = m00 * m1.m02 + m01 * m1.m12 + m02 * m1.m22 + m03 * m1.m32,
            m03 = m00 * m1.m03 + m01 * m1.m13 + m02 * m1.m23 + m03 * m1.m33,
            m10 = m10 * m1.m00 + m11 * m1.m10 + m12 * m1.m20 + m13 * m1.m30,
            m11 = m10 * m1.m01 + m11 * m1.m11 + m12 * m1.m21 + m13 * m1.m31,
            m12 = m10 * m1.m02 + m11 * m1.m12 + m12 * m1.m22 + m13 * m1.m32,
            m13 = m10 * m1.m03 + m11 * m1.m13 + m12 * m1.m23 + m13 * m1.m33,
            m20 = m20 * m1.m00 + m21 * m1.m10 + m22 * m1.m20 + m23 * m1.m30,
            m21 = m20 * m1.m01 + m21 * m1.m11 + m22 * m1.m21 + m23 * m1.m31,
            m22 = m20 * m1.m02 + m21 * m1.m12 + m22 * m1.m22 + m23 * m1.m32,
            m23 = m20 * m1.m03 + m21 * m1.m13 + m22 * m1.m23 + m23 * m1.m33,
            m30 = m30 * m1.m00 + m31 * m1.m10 + m32 * m1.m20 + m33 * m1.m30,
            m31 = m30 * m1.m01 + m31 * m1.m11 + m32 * m1.m21 + m33 * m1.m31,
            m32 = m30 * m1.m02 + m31 * m1.m12 + m32 * m1.m22 + m33 * m1.m32,
            m33 = m30 * m1.m03 + m31 * m1.m13 + m32 * m1.m23 + m33 * m1.m33
        )
    }

    companion object {
        fun rotate(a1: Vec3, angle: Angle): Mat4 {
            var mag = a1.length()
            if (mag < 1.0e-8) {
                return Mat4(
                    m00 = 1.0f,
                    m01 = 0.0f,
                    m02 = 0.0f,
                    m10 = 0.0f,
                    m11 = 1.0f,
                    m12 = 0.0f,
                    m20 = 0.0f,
                    m21 = 0.0f,
                    m22 = 1.0f,
                    m03 = 0.0f,
                    m13 = 0.0f,
                    m23 = 0.0f,
                    m30 = 0.0f,
                    m31 = 0.0f,
                    m32 = 0.0f,
                    m33 = 1.0f
                )
            } else {
                mag = 1.0f / mag
                val ax = a1.x * mag
                val ay = a1.y * mag
                val az = a1.z * mag
                val sinTheta = angle.sin()
                val cosTheta = angle.cos()
                val t = 1.0f - cosTheta
                val xz = ax * az
                val xy = ax * ay
                val yz = ay * az
                return Mat4(
                    m00 = t * ax * ax + cosTheta,
                    m01 = t * xy - sinTheta * az,
                    m02 = t * xz + sinTheta * ay,
                    m10 = t * xy + sinTheta * az,
                    m11 = t * ay * ay + cosTheta,
                    m12 = t * yz - sinTheta * ax,
                    m20 = t * xz - sinTheta * ay,
                    m21 = t * yz + sinTheta * ax,
                    m22 = t * az * az + cosTheta,
                    m03 = 0.0f,
                    m13 = 0.0f,
                    m23 = 0.0f,
                    m30 = 0.0f,
                    m31 = 0.0f,
                    m32 = 0.0f,
                    m33 = 1.0f
                )
            }
        }
    }
}
