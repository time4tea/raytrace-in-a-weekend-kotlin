package net.time4tea.raytrace

import kotlin.math.max
import kotlin.math.min

class AABB(private val _min: Vec3, private val _max: Vec3) {

    fun include(other: AABB): AABB {
        val small = Vec3(
            min(_min.x, other._min.x),
            min(_min.y, other._min.y),
            min(_min.z, other._min.z)
        )
        val big = Vec3(
            max(_max.x, other._max.x),
            max(_max.y, other._max.y),
            max(_max.z, other._max.z)
        )
        return AABB(small, big)
    }

    fun hit(r: Ray, min: Float, max: Float): Boolean {

        var t_min = min
        var t_max = max

        val direction = r.direction
        val origin = r.origin

        // x
        val tminx = min(
            (_min.x - origin.x) / direction.x,
            (_max.x - origin.x) / direction.x
        )
        val tmaxx = max(
            (_min.x - origin.x) / direction.x,
            (_max.x - origin.x) / direction.x
        )
        
        t_min = max(tminx, t_min)
        t_max = min(tmaxx, t_max)

        if ( t_max < t_min) return false

        // y
        val tminy = min(
            (_min.y - origin.y) / direction.y,
            (_max.y - origin.y) / direction.y
        )
        val tmaxy = max(
            (_min.y - origin.y) / direction.y,
            (_max.y - origin.y) / direction.y
        )

        t_min = max(tminy, t_min)
        t_max = min(tmaxy, t_max)

        if ( t_max < t_min) return false

        // z
        val tminz = min(
            (_min.z - origin.z) / direction.z,
            (_max.z - origin.z) / direction.z
        )
        val tmaxz = max(
            (_min.z - origin.z) / direction.z,
            (_max.z - origin.z) / direction.z
        )

        t_min = max(tminz, t_min)
        t_max = min(tmaxz, t_max)

        if ( t_max < t_min) return false

        return true
    }

    fun min() = _min
    fun max() = _max

    override fun toString(): String {
        return "AABB(_min=$_min, _max=$_max)"
    }
}