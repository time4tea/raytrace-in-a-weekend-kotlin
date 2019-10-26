package net.time4tea.raytrace.intros

import net.time4tea.raytrace.*


fun main() {

    class SimpleCamera {
        val lower_left_corner = Vec3(-2.0f, -1.0f, -1.0f)
        val horizontal = Vec3(4.0f, 0.0f, 0.0f)
        val vertical = Vec3(0.0f, 2.0f, 0.0f)
        val origin = Vec3(0.0f, 0.0f, 0.0f)

        fun get_ray(u: Float, v: Float): Ray {
            return Ray(origin, lower_left_corner + u * horizontal + v * vertical - origin)
        }
    }

    fun colour(ray: Ray, world: Hitable, depth: Int): Colour {
        val hit = world.hit(ray, 0.0001f, Float.MAX_VALUE)

        if (hit != null) {
            return if (depth < 50) {
                hit.material.scatter(ray, hit)?.let { (attenuation, scattered) ->
                    attenuation * colour(scattered, world, depth + 1)
                } ?: Colour.BLACK
            } else {
                Colour.BLACK
            }
        } else {
            val unit_direction = ray.direction().unit()
            val t = 0.5f * (unit_direction.y() + 1.0f)
            return ((1.0f - t) * Colour.WHITE) + (t * Colour(
                0.5f,
                0.7f,
                1.0f
            ))
        }
    }

    val display = BufferedImageDisplay(600, 300)
    SwingFrame(display.image())

    val nx = display.size().width
    val ny = display.size().height
    val ns = 100

    val camera = SimpleCamera()

    val world = HitableList(
        listOf(
            Sphere(
                Vec3(0.0, 0.0, -1.0),
                0.5f,
                Lambertian(ConstantTexture(Colour(0.8, 0.3, 0.3)))
            ),
            Sphere(
                Vec3(0.0, -100.5, -1.0),
                100f,
                Lambertian(ConstantTexture(Colour(0.8, 0.8, 0.0)))
            ),
            Sphere(
                Vec3(1.0, 0.0, -1.0),
                0.5f,
                Metal(Colour(0.8, 0.6, 0.2), 0.0f)
            ),
            Sphere(
                Vec3(-1.0, 0.0, -1.0),
                0.5f,
                Metal(Colour(0.8, 0.8, 0.8), 0.0f)
            )
        )
    )

    for (j in 0 until ny) {
        for (i in 0 until nx) {
            val colour = (0 until ns).fold(
                Colour.BLACK
            ) { running, _ ->
                val u = i.toFloat() / nx.toFloat()
                val v = j.toFloat() / ny.toFloat()
                val ray = camera.get_ray(u, v)
                running + colour(ray, world, 0)
            } / ns
            display.plot(i, j, colour.sqrt())
        }
    }
}