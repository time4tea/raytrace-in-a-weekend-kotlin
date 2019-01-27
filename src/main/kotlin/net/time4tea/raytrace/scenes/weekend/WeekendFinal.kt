package net.time4tea.raytrace.scenes.weekend

import net.time4tea.raytrace.*
import java.io.File

interface Scene {
    fun scene(): Hitable
    fun lookat(): Vec3
    fun lookfrom(): Vec3
}

class WeekendFinal : Scene {

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
                            Vec3(0, -1000, 0), 1000f, Lambertian(green_white)
                        ),
                        Sphere(Vec3(0, 1, 0), 1.0f, Dielectric(1.5)),
                        Sphere(
                            Vec3(-4, 1, 0), 1.0f, Lambertian(
                                earth
                            )
                        ),
                        Sphere(Vec3(4, 1, 0), 1.0f, Metal(Vec3(0.7, 0.6, 0.5), 0.0f))
                    )
                ), RandomScene().scene()
            )
        )
    }

}
