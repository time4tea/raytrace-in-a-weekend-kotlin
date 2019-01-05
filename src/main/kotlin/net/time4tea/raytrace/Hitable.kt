package net.time4tea.raytrace

interface Hitable {
    fun hit(ray: Ray, min: Float, max: Float): Hit?
}

data class Hit(val t: Float, val p: Vec3, val normal:Vec3, val material: Material)

class HitableList(private val items: List<Hitable>): Hitable {
    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        var hit: Hit? = null
        for (item in items) {
            item.hit(ray, min, hit?.t ?: max)?.let {
                hit = it
            }
        }
        return hit
    }
}