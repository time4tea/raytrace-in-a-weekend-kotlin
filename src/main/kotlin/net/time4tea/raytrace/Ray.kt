package net.time4tea.raytrace

class Ray(val origin: Vec3, val direction: Vec3) {
    fun point_at_parameter(t: Float): Vec3 {
        return origin + (direction * t)
    }
}