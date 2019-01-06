package net.time4tea.raytrace

import kotlin.math.sqrt

class Sphere(private val centre: Vec3, private val radius: Float, private val material: Material) : Hitable {

    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        val origin_centre = ray.origin() - centre

        val a = ray.direction().dot(ray.direction())
        val b = origin_centre.dot(ray.direction())
        val c = origin_centre.dot(origin_centre) - (radius * radius)

        val discriminant = b * b - a * c
        return if (discriminant > 0.0) {
            val size = sqrt(discriminant)

            var temp = (-b - size) / a

            if (temp < max && temp > min) {
                val p = ray.point_at_parameter(temp)
                return Hit(temp, p, (p - centre) / radius, material)
            }

            temp = (-b + size) / a

            if (temp < max && temp > min) {
                val p = ray.point_at_parameter(temp)
                return Hit(temp, p, (p - centre) / radius, material)
            }
            null
        } else {
            null
        }
    }
}