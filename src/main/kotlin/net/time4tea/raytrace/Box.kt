package net.time4tea.raytrace

class Box(p0: Vec3, p1: Vec3, ptr: Material) : Hitable {

    private val components = HitableList(
        listOf(
            XY_Rect(p0.x, p1.x, p0.y, p1.y, p1.z, ptr),
            FlipNormals(XY_Rect(p0.x, p1.x, p0.y, p1.y, p0.z, ptr)),
            XZ_Rect(p0.x, p1.x, p0.z, p1.z, p1.y, ptr),
            FlipNormals(XZ_Rect(p0.x, p1.x, p0.z, p1.z, p0.y, ptr)),
            YZ_Rect(p0.y, p1.y, p0.z, p1.z, p1.x, ptr),
            FlipNormals(YZ_Rect(p0.y, p1.y, p0.z, p1.z, p0.x, ptr))
        )
    )

    private val box = AABB(p0, p1)

    override fun hit(ray: Ray, min: Float, max: Float): Hit? = components.hit(ray, min, max)

    override fun bbox(): AABB? = box

    override fun components(): Int {
        return components.components()
    }
}