package net.time4tea.raytrace

import kotlin.math.ln
import kotlin.random.Random

class ConstantMedium(
    private val boundary: Hitable,
    private val density: Float,
    texture: Texture
) : Hitable {

    private val phase_function = Isotropic(texture)

    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        return boundary.hit(ray, -Float.MAX_VALUE, Float.MAX_VALUE)?.let { hit1 ->
            boundary.hit(ray, hit1.t + 0.0001f, Float.MAX_VALUE)?.let { hit2 ->
                if (hit1.t >= hit2.t) {
                    null
                } else {
                    val distance_inside_boundary = (hit2.t - hit1.t) * ray.direction.length()
                    val hit_distance = -(1.0f / density) * ln(Random.nextFloat())
                    if (hit_distance < distance_inside_boundary) {
                        val t = hit1.t + hit_distance / ray.direction.length()
                        Hit(
                            t,
                            0.0f, 0.0f,
                            ray.point_at_parameter(t),
                            Vec3(1.0, 0.0, 0.0),
                            phase_function
                        )
                    }
                    else {
                        null
                    }
                }
            }
        }
    }

    override fun bbox(): AABB? {
        return boundary.bbox()
    }

    override fun components(): Int {
        return boundary.components()
    }
}