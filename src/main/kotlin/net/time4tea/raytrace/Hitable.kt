package net.time4tea.raytrace

interface Hitable {
    fun hit(ray: Ray, min: Float, max: Float): Hit?
    fun bbox(): AABB?
    fun components(): Int
}

data class Hit(val t: Float, val u: Float, val v: Float, val p: Vec3, val normal: Vec3, val material: Material)

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