import net.time4tea.raytrace.BVH
import net.time4tea.raytrace.CheckerTexture
import net.time4tea.raytrace.Colour
import net.time4tea.raytrace.ConstantTexture
import net.time4tea.raytrace.Dielectric
import net.time4tea.raytrace.Hitable
import net.time4tea.raytrace.HitableList
import net.time4tea.raytrace.ImageTexture
import net.time4tea.raytrace.Lambertian
import net.time4tea.raytrace.Metal
import net.time4tea.raytrace.Scene
import net.time4tea.raytrace.Sphere
import net.time4tea.raytrace.Vec3
import net.time4tea.raytrace.scenes.weekend.RandomScene
import net.time4tea.raytrace.scenes.weekend.constantLighting
import java.io.File

object : Scene {

    override fun lookfrom() = Vec3(13f, 2f, 3f)
    override fun lookat() = Vec3(0f, 0f, 0f)
    override fun constantLight(): (Vec3) -> Colour = { it -> constantLighting(it) }

    override fun scene(): Hitable {
        val green_white = CheckerTexture(
            ConstantTexture(Colour(0.2, 0.3, 0.1)),
            ConstantTexture(Colour(0.9, 0.9, 0.9))
        )

        val earth = ImageTexture(File("src/main/resources/earthmap.jpg"))

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
                        Sphere(Vec3(4, 1, 0), 1.0f, Metal(Colour(0.7, 0.6, 0.5), 0.0f))
                    )
                ), RandomScene().scene()
            )
        )
    }

}