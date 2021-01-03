package net.time4tea.raytrace.scenes.found

import net.time4tea.raytrace.BVH
import net.time4tea.raytrace.Box
import net.time4tea.raytrace.Colour
import net.time4tea.raytrace.ConstantTexture
import net.time4tea.raytrace.Dielectric
import net.time4tea.raytrace.DiffuseLight
import net.time4tea.raytrace.FlipNormals
import net.time4tea.raytrace.Hitable
import net.time4tea.raytrace.HitableList
import net.time4tea.raytrace.Lambertian
import net.time4tea.raytrace.Metal
import net.time4tea.raytrace.RotateY
import net.time4tea.raytrace.Scene
import net.time4tea.raytrace.Sphere
import net.time4tea.raytrace.SurfaceNormal
import net.time4tea.raytrace.Translate
import net.time4tea.raytrace.Vec3
import net.time4tea.raytrace.XY_Rect
import net.time4tea.raytrace.XZ_Rect
import net.time4tea.raytrace.YZ_Rect
import net.time4tea.raytrace.scenes.weekend.constantLighting

// Kotlin conversion of some scenes in https://github.com/define-private-public/PSRayTracing/
// to see how they look on this implementation.
class ThreeSpheres : Scene {

    private val skyBlue = Colour(0.7, 0.8, 1.0)

    override fun scene(): Hitable {
        val glass = Dielectric(1.5)
        return BVH(listOf(
            Sphere(Vec3(-1, 0, -1), 0.5f, glass),
            Sphere(Vec3(-1, 0, -1), 0.35f, glass),
            Sphere(Vec3(-1, 0, -1), 0.3f, glass),
            Sphere(Vec3(-1, 0, -1), 0.2f, glass),
            Sphere(Vec3(-1, 0, -1), 0.15f, glass),
            Sphere(Vec3(-1, 0, -1), 0.1f, DiffuseLight(ConstantTexture(Colour(0.1,0.2,1.0)))),

            Sphere(Vec3(0.0, 0.15, -1.0), 0.5f, SurfaceNormal(0.6f, 0.75f)),
            Sphere(Vec3(1, 0, -1), 0.5f, Metal(Colour(0.8, 0.6, 0.2), 1.0f)),
            Sphere(Vec3(0, -100, -1), 100.0f, Lambertian(ConstantTexture(Colour(0.8, 0.8, 0.0))))
        ))
    }

    override fun lookat(): Vec3 {
       return Vec3(-1, 0, -1)
    }

    override fun lookfrom(): Vec3 {
        return Vec3(0.0, 0.25, 3.25)
    }

    override fun constantLight(): (Vec3) -> Colour {
        return { constantLighting(it ) }
    }
}

class CornellGlassBoxes : Scene {
    override fun scene(): Hitable {

        val red = Lambertian(ConstantTexture(Colour(0.65f, 0.05f, 0.05f)))
        val white = Lambertian(ConstantTexture(Colour(0.73f, 0.73f, 0.73f)))
        val green = Lambertian(ConstantTexture(Colour(0.12f, 0.73f, 0.15f)))

        val light = DiffuseLight(ConstantTexture(Colour(7.0,7.0,7.0)))

        val emptyBox = listOf(
            FlipNormals(YZ_Rect(0.0f, 555.0f, 0.0f, 555.0f, 555.0f, green)),
            YZ_Rect(0.0f, 555.0f, 0.0f, 555.0f, 0.0f, red),
            XZ_Rect(150.0f, 410.0f, 150.0f, 400.0f, 5.0f, light),
            XZ_Rect(0.0f, 555.0f, 0.0f, 555.0f, 0.0f, white),
            FlipNormals(XZ_Rect(0.0f, 555.0f, 0.0f, 555.0f, 555.0f, white)),
            FlipNormals(XY_Rect(0.0f, 555.0f, 0.0f, 555.0f, 555.0f, white))
        )

        val glass = Dielectric(1.5)

        val cubeDepth = 5
        val boxSize = 50
        val boxSpacing = 50
        val boxOffset = 50

        val boxes = ( 0..cubeDepth ).flatMap { x ->
            ( 0.. cubeDepth).flatMap { y ->
                ( 0..cubeDepth ).map { z ->
                    val location = Vec3(
                        boxOffset + (( boxSize + boxSpacing) * x ).toDouble(),
                        boxOffset + (( boxSize + boxSpacing) * y ).toDouble(),
                        -42.5 + (( boxSize + boxSpacing) * z )
                    )
                    val rotation = ( 23 * x) + ( 13 * y) + ( 3 * z)

                    val box = Box(Vec3.ZERO, Vec3(boxSize), glass)
                    Translate(RotateY(box, rotation.toFloat() ), location)
                }
            }
        }

        return BVH(emptyBox + boxes)
    }

    override fun lookat() = Vec3(278,278,0)

    override fun lookfrom() = Vec3(278, 278, -800)
}