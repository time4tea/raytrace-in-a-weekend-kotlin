package net.time4tea.raytrace.wavefront

import java.io.File

class ObjLoader {

    open class Loading {

        data class Geom(val vertex: Int, val normal: Int?, val texture: Int?)

        open fun startgroup(vararg names: String) {}
        open fun vertex(x: Float, y: Float, z: Float) {}
        open fun vertexNormal(x: Float, y: Float, z: Float) {}
        open fun face(v1: Geom, v2: Geom, v3: Geom) {}
    }

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