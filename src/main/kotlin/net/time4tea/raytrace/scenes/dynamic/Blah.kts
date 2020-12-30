import net.time4tea.raytrace.Angle
import net.time4tea.raytrace.BVH
import net.time4tea.raytrace.Box
import net.time4tea.raytrace.Colour
import net.time4tea.raytrace.ConstantTexture
import net.time4tea.raytrace.DiffuseLight
import net.time4tea.raytrace.Hitable
import net.time4tea.raytrace.Metal
import net.time4tea.raytrace.Scene
import net.time4tea.raytrace.Sphere
import net.time4tea.raytrace.Vec3
import net.time4tea.raytrace.scenes.weekend.constantLighting
import kotlin.random.Random

object : Scene {

    override fun constantLight(): (Vec3) -> Colour = {it -> constantLighting(it) }

    override fun scene(): Hitable {

        val random = Random(0)

        val world = mutableListOf<Hitable>()

        for (x in 0 until 10) {
            val centre = Vec3(
                random.nextDouble(-500.0, 500.0),
                random.nextDouble(-500.0, 500.0),
                random.nextDouble(-500.0, 500.0)
            )

            val size = Vec3(20, 20, 20)
            world.add(
                Box(
                    centre - size,
                    centre + size,
                    DiffuseLight(
                        ConstantTexture(
                            Colour(random.nextFloat(), random.nextFloat(), random.nextFloat())
                        )
                    )
                )
            )
        }

        for (x in 0 until 150) {
            world.add(
                Sphere(
                    Vec3(
                        -200f + (x * 50),
                        200 * Angle.Deg(50.0 * x).sin(),
                        0f
                    ),
                    30f,
                    Metal(
                        Colour(
                            0.5f * (1f + random.nextFloat()),
                            0.5f * (1f + random.nextFloat()),
                            0.5f * (1f + random.nextFloat())
                        ), 0.5f * random.nextFloat()
                    )
                )
            )
        }

        return BVH(world)
    }

    override fun lookfrom() = Vec3(x = -50.0, y = 50.0, z = 50.0)

    override fun lookat() = Vec3(0, 0, 0)
}