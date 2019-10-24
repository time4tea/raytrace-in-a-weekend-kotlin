package net.time4tea.raytrace.scenes.week

import net.time4tea.raytrace.*
import java.io.File
import kotlin.random.Random


class WeekFinal : Scene {

    override fun lookfrom() = Vec3(478, 278, -600)
    override fun lookat() = Vec3(278, 278, 0)

    override fun scene(): Hitable {

        val world = mutableListOf<Hitable>()

        val ground = Lambertian(ConstantTexture(Vec3(0.48, 0.83, 0.53)))

        world.add(
            BVH(
                (0 until 20).flatMap { i ->
                    (0 until 20).map { j ->
                        val w = 100f
                        val x0 = -1000 + i * w
                        val z0 = -1000 + j * w
                        val y0 = 0f
                        val x1 = x0 + w
                        val y1 = 100f * (Random.nextFloat() + 0.01f)
                        val z1 = z0 + w
                        Box(Vec3(x0, y0, z0), Vec3(x1, y1, z1), ground)
                    }
                }
            )
        )

        val light = DiffuseLight(ConstantTexture(Vec3(1, 1, 1)))

        world.add(XZ_Rect(123f, 423f, 147f, 412f, 554f, light))

        world.add(Sphere(Vec3(260, 150, 45), 50f, Dielectric(1.5)))
        world.add(Sphere(Vec3(0, 150, 145), 50f, Metal(Vec3(0.8, 0.8, 0.9), 10f)))

        val boundary1 = Sphere(Vec3(360, 150, 145), 70f, Dielectric(1.5))
        world.add(boundary1)
        world.add(ConstantMedium(boundary1, 0.2f, ConstantTexture(Vec3(0.2, 0.4, 0.9))))

        val boundary2 = Sphere(Vec3(0, 0, 0), 5000f, Dielectric(1.5))
        world.add(ConstantMedium(boundary2, 0.0001f, ConstantTexture(Vec3(1.0, 1.0, 1.0))))

        world.add(Sphere(Vec3(400, 200, 400), 100f, Lambertian(ImageTexture(File("src/main/resources/earth.jpg")))))

        world.add(Sphere(Vec3(220, 280, 300), 80f, Lambertian(NoiseTexture(0.1f))))

        val white = Lambertian(ConstantTexture(Vec3(0.73, 0.73, 0.73)))
        val smallSpheres = BVH(
            (0 until 1000).map {
                Sphere(
                    Vec3(
                        165 * Random.nextFloat(),
                        165 * Random.nextFloat(),
                        165 * Random.nextFloat()
                    ),
                    10 * Random.nextFloat(),
                    white
                )
            }
        )
        world.add(Translate(RotateY(smallSpheres, 15f), Vec3(-100, 270, 395)))

        return BVH(world)
    }
}
