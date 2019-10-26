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
    private val constantLight: (Vec3) -> Colour = { Colour.BLACK }
) {

    private fun colour(ray: Ray, world: Hitable, depth: Int): Colour {
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

        val jobs = mutableListOf<Deferred<Pair<Int, List<Colour>>>>()

        for (j in 0 until ny) {
            jobs.add(GlobalScope.async {
                val row = mutableListOf<Colour>()

                for (i in 0 until nx) {

                    val colour = (0 until samples).fold(
                        Colour.BLACK
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