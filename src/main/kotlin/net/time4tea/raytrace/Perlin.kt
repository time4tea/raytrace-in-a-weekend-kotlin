package net.time4tea.raytrace

import java.lang.Math.random
import kotlin.math.abs
import kotlin.math.floor


class Perlin {

    private fun perlin_interp(c: Array<Array<Array<Vec3>>>, u: Float, v: Float, w: Float): Float {
        val uu = u * u * (3 - 2 * u)
        val vv = v * v * (3 - 2 * v)
        val ww = w * w * (3 - 2 * w)
        var accum = 0.0f
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                for (k in 0 until 2) {
                    val weight_v = Vec3(u - i, v - j, w - k)
                    accum += (i * uu + (1 - i) * (1 - uu)) *
                            (j * vv + (1 - j) * (1 - vv)) *
                            (k * ww + (1 - k) * (1 - ww)) * c[i][j][k].dot(weight_v)
                }
            }
        }
        return accum
    }

    private fun noise(p: Vec3): Float {
        val u = p.x - floor(p.x)
        val v = p.y - floor(p.y)
        val w = p.z - floor(p.z)
        val i = floor(p.x).toInt()
        val j = floor(p.y).toInt()
        val k = floor(p.z).toInt()
        val c = Array<Array<Array<Vec3>>>(
            2
        ) { di ->
            Array<Array<Vec3>>(
                2
            ) { dj ->
                Array<Vec3>(
                    2
                ) { dk ->
                    ranvec[
                            perm_x[(i + di) and 255]
                                    xor perm_y[(j + dj) and 255]
                                    xor perm_z[(k + dk) and 255]]
                }
            }
        }
        return perlin_interp(c, u, v, w)
    }

    fun turb(p: Vec3, depth: Int = 7): Float {
        var accum = 0f
        var temp_p = p
        var weight = 1.0f
        for (i in 0 until depth) {
            accum += weight * noise(temp_p)
            weight *= 0.5f
            temp_p *= 2.0f
        }
        return abs(accum)
    }

    private fun perlin_generate(): List<Vec3> {
        return (0 until 256).map {
            Vec3(
                2 * random() - 1,
                2 * random() - 1,
                2 * random() - 1
            ).unit()
        }.toList()
    }

    private fun perlin_generate_perm(): List<Int> {
        val l = (0 until 256).map { it }.toMutableList()
        l.shuffle()
        return l.toList()
    }

    private val ranvec: List<Vec3> = perlin_generate()
    private val perm_x: List<Int> = perlin_generate_perm()
    private val perm_y: List<Int> = perlin_generate_perm()
    private val perm_z: List<Int> = perlin_generate_perm()
}