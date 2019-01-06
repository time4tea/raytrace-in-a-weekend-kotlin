package net.time4tea.raytrace


fun main() {

    fun hit_sphere(centre: Vec3, radius: Float, r: Ray): Boolean {
        val oc = r.origin() - centre
        val a = r.direction().dot(r.direction())
        val b = 2.0f * oc.dot(r.direction())
        val c = oc.dot(oc) - radius * radius
        val discriminant = b*b - 4*a*c
        return discriminant > 0
    }

    fun colour(ray: Ray): Vec3 {

        if ( hit_sphere(Vec3(0f, 0f, -1f), 0.5f, ray)) {
            return Vec3(1.0f, 0.0f, 0.0f)
        }
        val unit_direction = ray.direction().unit()
        val t = 0.5f * (unit_direction.y() + 1.0f)
        return ((1.0f - t) * Vec3.UNIT()) + (t * Vec3(0.5f, 0.7f, 1.0f))
    }

    val display = BufferedImageDisplay(600, 300)
    SwingFrame(display.image())

    val nx = display.size().width
    val ny = display.size().height

    val lower_left_corner = Vec3(-2.0f, -1.0f, -1.0f)
    val horizontal = Vec3(4.0f, 0.0f, 0.0f)
    val vertical = Vec3(0.0f, 2.0f, 0.0f)
    val origin = Vec3(0.0f, 0.0f, 0.0f)

    for (j in 0..ny - 1) {
        for (i in 0..nx - 1) {
            val u = i.toFloat() / nx.toFloat()
            val v = j.toFloat() / ny.toFloat()

            val r = Ray(origin, lower_left_corner + (u * horizontal) + (v * vertical))
            val col = colour(r)

            display.plot(i, j, col)
        }
    }
}