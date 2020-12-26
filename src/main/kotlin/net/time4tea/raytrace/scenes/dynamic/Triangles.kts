import net.time4tea.raytrace.BVH
import net.time4tea.raytrace.Box
import net.time4tea.raytrace.CheckerTexture
import net.time4tea.raytrace.Colour
import net.time4tea.raytrace.ConstantTexture
import net.time4tea.raytrace.Dielectric
import net.time4tea.raytrace.DiffuseLight
import net.time4tea.raytrace.Hitable
import net.time4tea.raytrace.Lambertian
import net.time4tea.raytrace.Material
import net.time4tea.raytrace.Metal
import net.time4tea.raytrace.Scene
import net.time4tea.raytrace.Sphere
import net.time4tea.raytrace.Texture
import net.time4tea.raytrace.Triangle
import net.time4tea.raytrace.Vec3
import net.time4tea.raytrace.times
import net.time4tea.raytrace.wavefront.ObjLoader
import java.io.File
import kotlin.math.sin

object Cube {

}

object : Scene {
    override fun scene(): Hitable {
        val world = mutableListOf<Hitable>()

        val green_white = CheckerTexture(
            ConstantTexture(Colour(0.2, 0.3, 0.1)),
            ConstantTexture(Colour(0.9, 0.9, 0.9))
        )

        world.add(
            Sphere(Vec3(20, 50, 45), 20f, DiffuseLight(ConstantTexture(Colour(7.0, 7.0, 7.0))))
        )

        world.add(
            Sphere(Vec3(-100, 50, 45), 10f, DiffuseLight(ConstantTexture(Colour(7.0, 7.0, 0.0))))
        )

        world.add(Sphere(
            Vec3(0, -1000, 0), 1000f, Lambertian(green_white)
        ))

        val objLoader = ObjLoader()
        objLoader.load(File("src/main/resources/cube.obj"), object : ObjLoader.Loading() {
            val scale = 100.0f
            val vertices = mutableListOf<Vec3>()

            var current : MutableList<Hitable>? = null

            override fun startgroup(vararg names: String) {
                current = mutableListOf()
            }

            override fun face(v1: Geom, v2: Geom, v3: Geom) {
                current!!.add(Triangle(
                    vertices[v1.vertex - 1],
                    vertices[v2.vertex - 1],
                    vertices[v3.vertex - 1],
                    Lambertian(
                        BigCheck(
                            ConstantTexture(Colour(0.2, 0.3, 0.1)),
                            ConstantTexture(Colour(0.9, 0.9, 0.9)),
                            0.2
                        )
                    )
                ).also { /*println(it)*/})
            }

            override fun vertex(x: Float, y: Float, z: Float) {
                vertices.add(Vec3(x * scale, y * scale, z * scale))
            }

            override fun end() {
                current?.let { world.add(BVH(it)) }
            }
        })

        world.add(
            box(
                Vec3(50, 200, 0), 50, Lambertian(
                    BigCheck(
                        ConstantTexture(Colour(0.2, 0.3, 0.1)),
                        ConstantTexture(Colour(0.9, 0.9, 0.9)),
                        0.2
                    )
                )
            )
        )

        return BVH(world)
    }

    private fun box(centre: Vec3, side: Int, material: Material): Box {
        return Box(centre - Vec3(side, side, side), centre + Vec3(side, side, side), material)
    }

    inner class BigCheck(private val t1: Texture, private val t2: Texture, val multiplier: Double = 0.5) : Texture {
        override fun value(u: Float, v: Float, p: Vec3): Colour {
            val sines = sin(multiplier * p.x) * sin(multiplier * p.y) * sin(multiplier * p.z)
            return if (sines < 0) {
                t1.value(u, v, p)
            } else {
                t2.value(u, v, p)
            }
        }
    }

    override fun constantLight(): (Vec3) -> Colour = { direction ->
        val unit_direction = direction.unit()
        val t = 0.5f * (unit_direction.y + 1.0f)
        ((1.0f - t) * Colour.from("#B0B001")) + (t * Colour(
            0.5f,
            0.7f,
            1.0f
        ))
    }

    override fun lookfrom() = Vec3(x = -172.0, y = 178.0, z = 700.0)

    override fun lookat() = Vec3(0, 0, 0)
}