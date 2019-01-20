package net.time4tea.raytrace

import kotlin.math.max
import kotlin.math.min

class AABB(private val _min: Vec3, private val _max: Vec3) {

    companion object {
        private val dimensions = listOf(Vec3::x, Vec3::y, Vec3::z)
    }

    fun include(other: AABB): AABB {
        val small = Vec3(
            min(_min.x(), other._min.x()),
            min(_min.y(), other._min.y()),
            min(_min.z(), other._min.z())
        )
        val big = Vec3(
            max(_max.x(), other._max.x()),
            max(_max.y(), other._max.y()),
            max(_max.z(), other._max.z())
        )
        return AABB(small, big)
    }

    fun hit(r: Ray, min: Float, max: Float): Boolean {

        var tmin = min
        var tmax = max

        for (d in dimensions) {
            val t0 = min(
                (d(_min) - d(r.origin())) / d(r.direction()),
                (d(_max) - d(r.origin())) / d(r.direction())
            )
            val t1 = max(
                (d(_min) - d(r.origin())) / d(r.direction()),
                (d(_max) - d(r.origin())) / d(r.direction())
            )
            tmin = max(t0, tmin)
            tmax = min(t1, tmax)
            if (tmax <= tmin) {
                return false
            }
        }
        return true
    }

    fun min() = _min
    fun max() = _max

    override fun toString(): String {
        return "AABB(_min=$_min, _max=$_max)"
    }
}