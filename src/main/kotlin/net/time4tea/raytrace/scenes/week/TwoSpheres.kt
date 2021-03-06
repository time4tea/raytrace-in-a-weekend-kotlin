package net.time4tea.raytrace.scenes.week

import net.time4tea.raytrace.*
import net.time4tea.raytrace.scenes.weekend.constantLighting

class TwoSpheres : Scene {
    override fun constantLight(): (Vec3) -> Colour = {it -> constantLighting(it) }

    override fun scene(): Hitable {
        val checker = CheckerTexture(
            ConstantTexture(Colour(0.2, 0.3, 0.1)), ConstantTexture(Colour(0.9, 0.9, 0.9))
        );

        return BVH(
            listOf(
                Sphere(Vec3(0, -10, 0), 10f, Lambertian(checker)),
                Sphere(Vec3(0, 10, 0), 10f, Lambertian(checker))
            )
        )
    }

    override fun lookat(): Vec3 {
        return Vec3.ZERO
    }

    override fun lookfrom(): Vec3 {
        return Vec3(13,2,3)
    }
}