package net.time4tea.raytrace

import kotlin.random.Random

class BVH(hitables: List<Hitable>) : Hitable {

    val box: AABB
    val left: Hitable
    val right: Hitable

    init {
        val axis = Random.nextInt(3)
        val sorted = hitables.sortedWith(
            when (axis) {
                0 -> compareX()
                1 -> compareY()
                2 -> compareZ()
                else -> throw RuntimeException("unexpected item in the bagging area")
            }
        )
        val size = sorted.size
        when (size) {
            0 -> throw RuntimeException("zero length")
            1 -> {
                left = sorted[0]
                right = sorted[0]
            }
            2 -> {
                left = sorted[0]
                right = sorted[1]
            }
            else -> {
                left = BVH(sorted.subList(0, size / 2))
                right = BVH(sorted.subList(size / 2, size))
            }
        }
        val left_box = left.bbox() ?: throw RuntimeException("no bbox")
        val right_box = right.bbox() ?: throw RuntimeException("no bbox")
        box = left_box.include(right_box)
    }

    companion object {
        fun compareX(): Comparator<Hitable> = compareWithAccessor(Vec3.xx())
        fun compareY(): Comparator<Hitable> = compareWithAccessor(Vec3.yy())
        fun compareZ(): Comparator<Hitable> = compareWithAccessor(Vec3.zz())

        private fun compareWithAccessor(f: (Vec3) -> Float): Comparator<Hitable> =
            Comparator { a: Hitable, b: Hitable ->
                val abox = a.bbox()!!
                val bbox = b.bbox()!!
                f(abox.min()).compareTo(f(bbox.min()))
            }
    }

    override fun hit(ray: Ray, min: Float, max: Float): Hit? {
        return when {
            box.hit(ray, min, max) -> {
                val hit_left = left.hit(ray, min, max)
                val hit_right = right.hit(ray, min, max)

                return when {
                    hit_left != null && hit_right != null -> when {
                        hit_left.t < hit_right.t -> hit_left
                        else -> hit_right
                    }
                    hit_left != null -> hit_left
                    hit_right != null -> hit_right
                    else -> null
                }
            }
            else -> null
        }
    }

    override fun bbox(): AABB? {
        return box
    }

    override fun components(): Int {
        return left.components() + right.components()
    }
}