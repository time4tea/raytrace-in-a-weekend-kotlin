package net.time4tea.raytrace

import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

class RotateY(private val p: Hitable, angle: Float) : Hitable {

    val sin_theta: Float
    val cos_theta: Float
    val bbox: AABB?

    init {
        val radians = toRadians(angle.toDouble())
        sin_theta = sin(radians).toFloat()
        cos_theta = cos(radians).toFloat()

        bbox = p.bbox()?.let { original_box ->
            var min = Vec3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE)
            var max = Vec3(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
            for (i in 0 until 2) {
                for (j in 0 until 2) {
                    for (k in 0 until 2) {
                        val x = i * original_box.max().x() + (1 - i) * original_box.min().x()
                        val y = j * original_box.max().y() + (1 - j) * original_box.min().y()
                        val z = k * original_box.max().z() + (1 - k) * original_box.min().z()
                        val newx = cos_theta * x + sin_theta * z
                        val newz = -sin_theta * x + cos_theta * z
                        val tester = Vec3(newx, y, newz)
                        if (tester.x() > max.x()) max = max.withX(tester.x())
                        if (tester.x() < min.x()) min = min.withX(tester.x())
                        if (tester.y() > max.y()) max = max.withY(tester.y())
                        if (tester.y() < min.y()) min = min.withY(tester.y())
                        if (tester.z() > max.z()) max = max.withZ(tester.z())
                        if (tester.z() < min.z()) min = min.withZ(tester.z())
                    }
                }
            }
            AABB(min, max)
        }
    }

    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        var origin = ray.origin()
        var direction = ray.direction()
        origin = origin.withX(cos_theta * ray.origin().x() - sin_theta * ray.origin().z())
        origin = origin.withZ(sin_theta * ray.origin().x() + cos_theta * ray.origin().z())
        direction = direction.withX(cos_theta * ray.direction().x() - sin_theta * ray.direction().z())
        direction = direction.withZ(sin_theta * ray.direction().x() + cos_theta * ray.direction().z())
        val rotated_r = Ray(origin, direction)
        return p.hit(rotated_r, min, max)?.let {
            var p = it.p
            var normal = it.normal
            p = p.withX(cos_theta * it.p.x() + sin_theta * it.p.z())
            p = p.withZ(-sin_theta * it.p.x() + cos_theta * it.p.z())
            normal = normal.withZ(cos_theta * it.normal.x() + sin_theta * it.normal.z())
            normal = normal.withZ(-sin_theta * it.normal.x() + cos_theta * it.normal.z())
            Hit(it.t, it.u, it.v, p, normal, it.material)
        }
    }

    override fun bbox(): AABB? {
        return bbox
    }

    override fun components(): Int {
        return 1
    }
}