package net.time4tea.raytrace.scenes.weekend

import net.time4tea.raytrace.*

class SingleSphere : Scene {
    override fun scene(): Hitable {
        val boundary = Sphere(Vec3(0, 0, 0), 70f, Dielectric(1.5))
        return BVH(
            listOf(
                ConstantMedium(boundary, 0.2f, ConstantTexture(Vec3(0.2, 0.4, 0.9)))
            )
        )
    }

    override fun lookat(): Vec3 {
        return Vec3.ZERO
    }

    override fun lookfrom(): Vec3 {
        return Vec3(100, 100, 100)
    }
}