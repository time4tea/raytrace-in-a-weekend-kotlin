package net.time4tea.raytrace.scenes.weekend

import net.time4tea.raytrace.*
import java.io.File



class WeekendFinalLights : Scene {

    override fun lookfrom() = Vec3(13f, 2f, 3f)
    override fun lookat() = Vec3(0f, 0f, 0f)

    override fun scene(): Hitable {
        val green_white = CheckerTexture(
            ConstantTexture(Vec3(0.2, 0.3, 0.1)),
            ConstantTexture(Vec3(0.9, 0.9, 0.9))
        )

        val earth = ImageTexture(File("src/main/resources/earth.jpg"))

        return HitableList(
            listOf(
                BVH(
                    listOf(
                        Sphere(
                            Vec3(0, -1000, 0), 1000f, DiffuseLight(green_white)
                        ),
                        Sphere(
                            Vec3(0, -0, 0), 10000f, Metal(Vec3(0.5f, 0.7f, 1.0f), 1.0f)
                        ),
                        Sphere(Vec3(0, 1, 0), 1.0f, Dielectric(1.5)),
                        Sphere(
                            Vec3(-4, 1, 0), 1.0f, Lambertian(
                                earth
                            )
                        ),
                        Sphere(Vec3(4, 1, 0), 1.0f, Metal(Vec3(0.7, 0.6, 0.5), 0.0f))
                    )
                )
            )+ RandomScene().scene()
        )
    }
}
