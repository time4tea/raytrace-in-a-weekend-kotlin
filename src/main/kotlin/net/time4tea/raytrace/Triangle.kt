package net.time4tea.raytrace


class Triangle(val v0: Vec3, val v1: Vec3, val v2: Vec3, val material: Material) : Hitable {

    val aabb = AABB(
        v0.min(v1).min(v2),
        v0.max(v1).max(v2)
    )

    val kEpsilon = 1e-8

    override fun hit(ray: Ray, min: Float, max: Float): Hit? {

        val v0v1 = v1 - v0
        val v0v2 = v2 - v0

        val pvec = ray.direction().cross(v0v2)
        val det = v0v1.dot(pvec)
        if (det < kEpsilon) return null
        val inv = 1 / det
        val tvec = ray.origin() - v0
        val u = tvec.dot(pvec) * inv

        if (u < 0 || u > 1) return null
        val qvec = tvec.cross(v0v1)
        val v = ray.direction().dot(qvec) * inv
        if (v < 0 || u + v > 1) return null

        val t = v0v2.dot(qvec) * inv
        val n = v0v1.cross(v0v2)

        return Hit(
            t, u, v, pvec, n, material
        )
    }

    override fun bbox(): AABB? {
        return aabb
    }

    override fun components(): Int {
        return 1
    }

    override fun toString(): String {
        return "Triangle(v0=$v0, v1=$v1, v2=$v2, material=$material, aabb=$aabb)"
    }

}