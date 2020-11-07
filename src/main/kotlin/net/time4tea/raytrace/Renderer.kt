package net.time4tea.raytrace

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant
import kotlin.random.Random

class Renderer(
    private val world: Hitable,
    private val samples: Int,
    private val max_depth: Int,
    private val constantLight: (Vec3) -> Colour = { Colour.BLACK }
) {

    private fun colour(ray: Ray, world: Hitable, depth: Int = 0): Colour {
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

        val jobs = mutableListOf<Deferred<*>>()

        val step = 50

        for (j in 0 until ny step step) {
            for (i in 0 until nx step step) {
                jobs.add(GlobalScope.async {
                    for (y in j until j + step) {
                        for (x in i until i + step) {
                            val colour = (0 until samples).fold(
                                Colour.BLACK
                            ) { running, _ ->
                                val u = (x + Random.nextFloat()) / nx.toFloat()
                                val v = (y + Random.nextFloat()) / ny.toFloat()
                                running + colour(camera.get_ray(u, v), world)
                            } / samples

                            display.plot(x, y, colour.sqrt())
                        }
                    }
                })
            }
        }

        println("Waiting for ${jobs.size} jobs to complete")
        val start = Instant.now()
        runBlocking {
            jobs.awaitAll()
        }
        val duration = Duration.between(start, Instant.now())
        println("Render took $duration")
    }
}