package net.time4tea.raytrace

import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
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
                var h1t = max(hit1.t, min)
                val h2t = min(hit2.t, max)

                if (h1t >= h2t) {
                    null
                } else {
                    if ( h1t < 0 ) { h1t = 0.0f }
                    val distance_inside_boundary = (h2t - h1t) * ray.direction.length()
                    val hit_distance = -(1.0f / density) * ln(Random.nextFloat())
                    if (hit_distance < distance_inside_boundary) {
                        val t = h1t + hit_distance / ray.direction.length()
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