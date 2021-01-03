package net.time4tea.raytrace

import kotlin.random.Random

class BVH(hitables: List<Hitable>) : Hitable {

    private val box: AABB
    private val left: Hitable
    private val right: Hitable

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
        return if (!box.hit(ray, min, max)) null
        else {
            val hitLeft = left.hit(ray, min, max)
            val hitRight = right.hit(ray, min, max)

            return when {
                hitLeft != null && hitRight != null -> when {
                    hitLeft.t < hitRight.t -> hitLeft
                    else -> hitRight
                }
                hitLeft != null -> hitLeft
                hitRight != null -> hitRight
                else -> null
            }
        }
    }

    override fun bbox(): AABB? {
        return box
    }

    override fun components(): Int {
        return left.components() + right.components()
    }
}