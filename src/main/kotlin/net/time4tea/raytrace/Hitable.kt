package net.time4tea.raytrace

interface Hitable {
    fun hit(ray: Ray, min: Float, max: Float): Hit?
    fun bbox(): AABB?
    fun components(): Int
}

class Hit(
    @JvmField val t: Float,
    @JvmField val u: Float,
    @JvmField val v: Float,
    @JvmField val p: Vec3,
    @JvmField val normal: Vec3,
    @JvmField val material: Material
)

class HitableList(private val items: List<Hitable>) : Hitable {
    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        return items.fold(
            null,
            fun(running: Hit?, item: Hitable): Hit? {
                return item.hit(ray, min, running?.t ?: max) ?: running
            }
        )
    }

    override fun components(): Int {
        return items.sumBy { it.components() }
    }

    override fun bbox(): AABB? {
        if (items.isEmpty()) {
            return null
        }
        var box = items[0].bbox() ?: return null
        for (item in items.drop(1)) {
            item.bbox()?.let {
                box = box.include(it)
            } ?: return null
        }
        return box
    }
}

class FlipNormals(private val delegate: Hitable): Hitable {
    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        return delegate.hit(ray, min, max)?.let {
            Hit(
                it.t, it.u, it.v, it.p, -it.normal, it.material
            )
        }
    }

    override fun bbox(): AABB? = delegate.bbox()

    override fun components(): Int = delegate.components()
}