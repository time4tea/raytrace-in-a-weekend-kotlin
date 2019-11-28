package net.time4tea.raytrace.scenes.week

import net.time4tea.raytrace.*
import net.time4tea.raytrace.scenes.weekend.constantLighting

class TwoPerlinSpheres : Scene {
    override fun constantLight(): (Vec3) -> Colour = {it -> constantLighting(it) }

    override fun scene(): Hitable {
        val perlin = NoiseTexture(0.6f)

        return BVH(
            listOf(
                Sphere(Vec3(0,-1000, 0), 1000f, Lambertian(perlin)),
                Sphere(Vec3(0, 2, 0), 2f, Lambertian(perlin))
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