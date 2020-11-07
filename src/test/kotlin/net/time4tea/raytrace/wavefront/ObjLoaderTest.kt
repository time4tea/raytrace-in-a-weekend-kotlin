package net.time4tea.raytrace.wavefront

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import net.time4tea.raytrace.wavefront.ObjLoader.Loading.*
import org.junit.jupiter.api.Test
import java.io.File

class ObjLoaderTest {

    @Test
    fun `loads a simple object file`() {
        val loader = ObjLoader()
        println(File(".").absolutePath)

        val vertexes = mutableListOf<Triple<Float, Float, Float>>()
        val normals = mutableListOf<Triple<Float, Float, Float>>()
        val faces = mutableListOf<Triple<Geom, Geom, Geom>>()

        loader.load(File("src/main/resources/cube.obj"), object : ObjLoader.Loading() {
            override fun startgroup(vararg names: String) {
                assertThat(names[0], equalTo("cube"))
            }

            override fun vertex(x: Float, y: Float, z: Float) {
                vertexes.add(Triple(x, y, z))
            }

            override fun vertexNormal(x: Float, y: Float, z: Float) {
                normals.add(Triple(x, y, z))
            }

            override fun face(v1: Geom, v2: Geom, v3: Geom) {
                faces.add(Triple(v1, v2, v3))
            }
        })
        assertThat(vertexes, hasSize(equalTo(8)))
        assertThat(normals, hasSize(equalTo(6)))
        assertThat(faces, hasSize(equalTo(12)))
    }
}