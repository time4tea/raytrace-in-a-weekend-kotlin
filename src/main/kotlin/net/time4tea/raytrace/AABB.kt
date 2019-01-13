package net.time4tea.raytrace

import kotlin.math.max
import kotlin.math.min

class AABB(private val _min: Vec3, private val _max: Vec3) {

    private val dimensions = listOf(Vec3::x, Vec3::y, Vec3::z)

    fun include(other: AABB): AABB {
        val small = Vec3(
            min(_min.x(), other._min.x()),
            min(_min.y(), other._min.y()),
            min(_min.z(), other._min.y())
        )
        val big = Vec3(
            max(_max.x(), other._max.x()),
            max(_max.y(), other._max.y()),
            max(_max.z(), other._max.y())
        )
        return AABB(small, big)
    }

    fun hit(ray: Ray, min: Float, max: Float): Boolean {

        var tmin = min
        var tmax = max

        for (d in dimensions) {
            val invD = 1.0f / d(ray.direction())
            val t0 = (d(_min) - d(ray.origin())) * invD
            val t1 = (d(_max) - d(ray.origin())) * invD
            if (invD < 0.0f) {
                tmin = if (t0 > tmin) t0 else tmin
                tmax = if (t1 < tmax) t1 else tmax
            } else {
                tmin = if (t1 > tmin) t1 else tmin
                tmax = if (t0 < tmax) t0 else tmax
            }
            if (tmax <= tmin) {
                return false
            }
        }
        return true
    }

    fun min() = _min
    fun max() = _max

}