package net.time4tea.raytrace

import kotlin.math.max
import kotlin.math.min

class AABB(private val _min: Vec3, private val _max: Vec3) {

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

        val origin = r.origin()
        val direction = r.direction()

        // X
        val x_min = (_min.x() - origin.x()) / direction.x()
        val x_max   = (_max.x() - origin.x()) / direction.x()
        
        tmin = max(min(x_min, x_max), tmin)
        tmax = min(max(x_min, x_max), tmax)
        
        if ( tmax <= tmin) return false

        // Y
        val y_min = (_min.y() - origin.y()) / direction.y()
        val y_max   = (_max.y() - origin.y()) / direction.y()
        
        tmin = max(min(y_min, y_max), tmin)
        tmax = min(max(y_min, y_max), tmax)
        
        if ( tmax <= tmin) return false

        // Z
        val z_min = (_min.z() - origin.z()) / direction.z()
        val z_max   = (_max.z() - origin.z()) / direction.z()
        
        tmin = max(min(z_min, z_max), tmin)
        tmax = min(max(z_min, z_max), tmax)
        
        if ( tmax <= tmin) return false

        return true
    }

    fun min() = _min
    fun max() = _max

    override fun toString(): String {
        return "AABB(_min=$_min, _max=$_max)"
    }
}