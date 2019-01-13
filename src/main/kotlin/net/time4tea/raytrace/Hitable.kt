package net.time4tea.raytrace

interface Hitable {
    fun hit(ray: Ray, min: Float, max: Float): Hit?
}

data class Hit(val t: Float, val p: Vec3, val normal:Vec3, val material: Material)

class HitableList(private val items: List<Hitable>): Hitable {
    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        return items.fold(
            null,
            fun(running: Hit?, item: Hitable): Hit? {
                return item.hit(ray, min, running?.t ?: max) ?: running
            }
        )
    }
}