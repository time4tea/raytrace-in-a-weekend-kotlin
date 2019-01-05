package net.time4tea.raytrace

import kotlin.math.tan
import kotlin.random.Random.Default.nextFloat

class Camera(
    private val origin: Vec3,
    lookat: Vec3,
    up: Vec3,
    fov: Float,
    aspect: Float,
    aperture: Float,
    private val focus_dist: Float
) {
    private val lens_radius = aperture / 2f
    private val theta = fov * Math.PI / 180f
    private val half_height = tan(theta / 2.0f).toFloat()
    private val half_width = aspect * half_height

    private val w = (origin - lookat).unit()
    private val u = (up.cross(w)).unit()
    private val v = w.cross(u)

    val lower_left_corner = origin - half_width * focus_dist * u - half_height * focus_dist * v - focus_dist * w
    val horizontal = 2 * half_width * focus_dist * u
    val vertical = 2 * half_height * focus_dist * v

    fun get_ray(s: Float, t: Float): Ray {
        val rd: Vec3 = lens_radius * random_in_unit_disc()
        val offset = u * rd.x() + v * rd.y()
        return Ray(origin + offset, lower_left_corner + s * horizontal + t * vertical - origin - offset)
    }

    companion object {
        private fun random_in_unit_disc(): Vec3 {
            var p: Vec3
            do {
                p = 2.0f * Vec3( nextFloat(), nextFloat(), 0.0f) - Vec3(1.0f, 1.0f, 0.0f)
            } while ( p.dot(p) > 1.0 )
            return p
        }
    }
}