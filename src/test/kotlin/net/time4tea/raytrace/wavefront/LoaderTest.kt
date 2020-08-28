package net.time4tea.raytrace.wavefront

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import org.junit.jupiter.api.Test
import java.io.File

class LoaderTest {

    interface Loading {

        data class Geom(val vertex: Int, val normal: Int?, val texture: Int?)

        fun startgroup(vararg names: String)
        fun vertex(x: Float, y: Float, z: Float)
        fun vertexNormal(x: Float, y: Float, z: Float)
        fun face(v1: Geom, v2: Geom, v3: Geom)
    }

    class Loader {

        private fun floats(string: String): List<Float> = string.split(Regex(" +")).map { it.toFloat() }

        fun load(file: File, loading: Loading) {
            file.readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .filter { !it.startsWith("#") }
                .map { it.split(Regex(" +"), limit = 2) }
                .forEach {
                    when (it.first()) {
                        "g" -> {
                            loading.startgroup(* it.last().split(Regex(" +")).toTypedArray())
                        }
                        "v" -> {
                            val v = floats(it.last())
                            loading.vertex(v[0], v[1], v[2])
                        }
                        "vn" -> {
                            val vn = floats(it.last())
                            loading.vertexNormal(vn[0], vn[1], vn[2])
                        }
                        "f" -> {
                            val f = it.last()
                                .split(Regex(" +"))
                                .map { str -> str.split("/").map { i -> i.toIntOrNull() } }
                                .map { gs -> Loading.Geom(gs[0]!!, gs.getOrNull(1), gs.getOrNull(2)) }
                            loading.face(f[0], f[1], f[2])
                        }
                        else -> println("${it.first()} not handled")
                    }
                }
        }
    }

    @Test
    fun `loads a simple object file`() {
        val loader = Loader()
        println(File(".").absolutePath)

        val vertexes = mutableListOf<Triple<Float, Float, Float>>()
        val normals = mutableListOf<Triple<Float, Float, Float>>()
        val faces = mutableListOf<Triple<Loading.Geom, Loading.Geom, Loading.Geom>>()

        loader.load(File("src/test/resources/cube.obj"), object : Loading {
            override fun startgroup(vararg names: String) {
                assertThat(names[0], equalTo("cube"))
            }

            override fun vertex(x: Float, y: Float, z: Float) {
                vertexes.add(Triple(x, y, z))
            }

            override fun vertexNormal(x: Float, y: Float, z: Float) {
                normals.add(Triple(x, y, z))
            }

            override fun face(v1: Loading.Geom, v2: Loading.Geom, v3: Loading.Geom) {
                faces.add(Triple(v1, v2, v3))
            }
        })
        assertThat(vertexes, hasSize(equalTo(8)))
        assertThat(normals, hasSize(equalTo(6)))
        assertThat(faces, hasSize(equalTo(12)))
    }
}