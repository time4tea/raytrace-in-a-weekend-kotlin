package net.time4tea.raytrace.scenes.weekend

import net.time4tea.raytrace.*
import kotlin.random.Random

class RandomScene : Scene {

    override fun lookfrom() = Vec3(13f, 2f, 3f)
    override fun lookat() = Vec3(0f, 0f, 0f)

    override fun scene(): Hitable {

        val list = mutableListOf<Hitable>()

        for (a in -11..11) {
            for (b in -11..11) {
                val choose_mat = Random.nextFloat()

                val centre = Vec3(a + 0.9f * Random.nextFloat(), 0.2f, b + 0.9f * Random.nextFloat())
                if ((centre - Vec3(4.0f, 0.2f, 0.0f)).length() > 0.9) {

                    val material = when {
                        choose_mat < 0.8 -> Metal(
                            Colour(
                                0.5f * (1f + Random.nextFloat()),
                                0.5f * (1f + Random.nextFloat()),
                                0.5f * (1f + Random.nextFloat())
                            ), 0.5f * Random.nextFloat()
                        )
                        choose_mat < 0.95 -> Lambertian(
                            ConstantTexture(Colour(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()))
                        )
                        else -> Dielectric(1.5f)
                    }

                    val item = when (Random.nextInt(2)) {
                        0 -> Sphere(centre, 0.2f, material)
                        else -> Box(centre - Vec3(0.1, 0.1, 0.1), centre + Vec3(0.1, 0.1, 0.1), material)
                    }
                    list.add(item)
                }
            }
        }
        return BVH(list)
    }
}