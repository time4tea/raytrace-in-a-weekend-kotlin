package net.time4tea.raytrace

class Ray(private val origin: Vec3, private val direction: Vec3) {
    fun point_at_parameter(t: Float): Vec3 {
        return origin + (direction * t)
    }

    fun origin() = origin
    fun direction() = direction

}