package net.time4tea.raytrace

class Translate(private val hitable: Hitable, private val translation: Vec3) : Hitable {
    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        val translated = Ray(ray.origin() - translation, ray.direction())
        return hitable.hit(translated, min, max)?.let {
            it.copy(p = it.p + translation)
        }
    }

    override fun bbox(): AABB? {
        return hitable.bbox()?.let {
            AABB(it.min() + translation, it.max() + translation)
        }
    }

    override fun components(): Int {
        return hitable.components()
    }
}