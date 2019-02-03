package net.time4tea.raytrace

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class Renderer(
    private val world: Hitable,
    private val samples: Int,
    private val max_depth: Int,
    private val constantLight: (Vec3) -> Vec3 = { Vec3.ZERO }
) {

    private fun colour(ray: Ray, world: Hitable, depth: Int): Vec3 {
        return world.hit(ray, 0.001f, Float.MAX_VALUE)?.let { hit ->
            val emitted = hit.material.emitted(hit.u, hit.v, hit.p)
            if (depth < max_depth) {
                hit.material.scatter(ray, hit)?.let { (attenuation, scattered) ->
                    emitted + attenuation * colour(scattered, world, depth + 1)
                } ?: emitted
            } else {
                emitted
            }
        } ?: constantLight(ray.direction())
    }

    fun render(camera: Camera, display: Display) {

        val nx = display.size().width
        val ny = display.size().height

        val jobs = mutableListOf<Deferred<Pair<Int, List<Vec3>>>>()

        for (j in 0 until ny) {
            jobs.add(GlobalScope.async {
                val row = mutableListOf<Vec3>()

                for (i in 0 until nx) {

                    val colour = (0 until samples).fold(
                        Vec3.ZERO
                    ) { running, _ ->
                        val u = (i + Random.nextFloat()) / nx.toFloat()
                        val v = (j + Random.nextFloat()) / ny.toFloat()
                        running + colour(camera.get_ray(u, v), world, 0)
                    } / samples

                    row.add(colour.sqrt())
                }

                Pair(j, row)
            })
        }

        runBlocking {
            jobs.forEach {
                val result = it.await()
                for ((index, colour) in result.second.withIndex()) {
                    display.plot(index, result.first, colour)
                }
            }
        }
    }
}