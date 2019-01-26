package net.time4tea.raytrace

class XY_Rect(
    private val x0: Float,
    private val x1: Float,
    private val y0: Float,
    private val y1: Float,
    private val k: Float,
    private val material: Material
) : Hitable {
    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        val origin = ray.origin()
        val direction = ray.direction()

        val t = (k - origin.z()) / direction.z()
        if (t < min || t > max)
            return null

        val x = origin.x() + t * direction.x()
        val y = origin.y() + t * direction.y()

        if (x < x0 || x > x1 || y < y0 || y > y1)
            return null

        return Hit(
            t,
            (x - x0) / (x1 - x0),
            (y - y0) / (y1 - y0),
            ray.point_at_parameter(t),
            Vec3(0.0, 0.0, 1.0),
            material
        )
    }

    private val box = AABB(
        Vec3(x0, y0, k - 0.0001f),
        Vec3(x1, y1, k + 0.0001f)
    )

    override fun bbox(): AABB? {
        return box
    }

    override fun components(): Int = 1
}

class XZ_Rect(
    private val x0: Float,
    private val x1: Float,
    private val z0: Float,
    private val z1: Float,
    private val k: Float,
    private val material: Material
) : Hitable {
    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        val t = (k - ray.origin().y()) / ray.direction().y()
        if (t < min || t > max)
            return null

        val x = ray.origin().x() + t * ray.direction().x()
        val z = ray.origin().z() + t * ray.direction().z()
        if (x < x0 || x > x1 || z < z0 || z > z1)
            return null

        return Hit(
            t,
            (x - x0) / (x1 - x0),
            (z - z0) / (z1 - z0),
            ray.point_at_parameter(t),
            Vec3(0.0, 1.0, 0.0),
            material
        )
    }

    private val box = AABB(
        Vec3(x0, k - 0.0001f, z0),
        Vec3(x1, k + 0.0001f, z1)
    )

    override fun bbox(): AABB? {
        return box
    }

    override fun components(): Int = 1
}

class YZ_Rect(
    private val y0: Float,
    private val y1: Float,
    private val z0: Float,
    private val z1: Float,
    private val k: Float,
    private val material: Material
) : Hitable {
    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        val t = (k - ray.origin().x()) / ray.direction().x()
        if (t < min || t > max)
            return null
        val y = ray.origin().y() + t * ray.direction().y()
        val z = ray.origin().z() + t * ray.direction().z()
        if (y < y0 || y > y1 || z < z0 || z > z1)
            return null

        return Hit(
            t,
            (y - y0) / (y1 - y0),
            (z - z0) / (z1 - z0),
            ray.point_at_parameter(t),
            Vec3(1.0, 0.0, 0.0),
            material
        )
    }

    private val box = AABB(
        Vec3(k - 0.0001f, y0, z0),
        Vec3(k + 0.0001f, y1, z1)
    )

    override fun bbox(): AABB? {
        return box
    }

    override fun components(): Int = 1
}

