package net.time4tea.raytrace

import kotlin.math.sqrt

class Sphere(private val centre: Vec3, private val radius: Float, private val material: Material) : Hitable {

    private val box = AABB(
        centre - Vec3(radius, radius, radius),
        centre + Vec3(radius, radius, radius)
    )

    override fun components(): Int {
        return 1
    }

    override fun bbox(): AABB? {
        return box
    }

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
                val normal = (p - centre) / radius
                val uv = normal.sphere_uv()
                return Hit(temp, uv.u, uv.v, p, normal, material)
            }

            temp = (-b + size) / a

            if (temp < max && temp > min) {
                val p = ray.point_at_parameter(temp)
                val normal = (p - centre) / radius
                val uv = normal.sphere_uv()
                return Hit(temp, uv.u, uv.v, p, normal, material)
            }
            null
        } else {
            null
        }
    }

    override fun toString(): String {
        return "Sphere(centre=$centre, radius=$radius, material=$material, box=$box)"
    }
}