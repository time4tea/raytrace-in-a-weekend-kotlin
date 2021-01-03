package net.time4tea.raytrace.scenes.week

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
import net.time4tea.raytrace.RotateY
import net.time4tea.raytrace.Scene
import net.time4tea.raytrace.Sphere
import net.time4tea.raytrace.Translate
import net.time4tea.raytrace.Vec3
import net.time4tea.raytrace.XY_Rect
import net.time4tea.raytrace.XZ_Rect
import net.time4tea.raytrace.YZ_Rect

open class CornellBox : Scene {
    override fun scene(): Hitable {

        val red = Lambertian(ConstantTexture(Colour(0.65f, 0.05f, 0.05f)))
        val white = Lambertian(ConstantTexture(Colour(0.73f, 0.73f, 0.73f)))
        val green = Lambertian(ConstantTexture(Colour(0.12f, 0.73f, 0.15f)))

        val light = DiffuseLight(ConstantTexture(Colour(7.0,7.0,7.0)))

        return HitableList(listOf(
            FlipNormals(YZ_Rect( 0.0f, 555.0f, 0.0f, 555.0f, 555.0f, green)),
            YZ_Rect( 0.0f, 555.0f, 0.0f, 555.0f, 0.0f, red),
            XZ_Rect( 150.0f, 410.0f, 150.0f, 400.0f, 5.0f, light),
            XZ_Rect( 0.0f, 555.0f, 0.0f, 555.0f, 0.0f, white),
            FlipNormals(XZ_Rect( 0.0f, 555.0f, 0.0f, 555.0f, 555.0f, white)),
            FlipNormals(XY_Rect( 0.0f, 555.0f, 0.0f, 555.0f, 555.0f, white))
        ))
    }

    override fun lookat() = Vec3(278,278,0)

    override fun lookfrom() = Vec3(278, 278, -800)

    override fun constantLight(): (Vec3) -> Colour {
        return { Colour.BLACK }
    }
}

class CornellBoxWithBox : CornellBox() {
    override fun scene(): Hitable {
        val cornellBox = super.scene();

        val glass = Dielectric(1.5168)

        val size = 150

        val rotation = 45

        val box = Translate(
            RotateY(
                Box(Vec3.ZERO, Vec3(size), glass),
                rotation.toFloat()
            ),
            Vec3(278 - (size/2))
        )

        return BVH(
            listOf(
                cornellBox,
                box
            )
        )
    }
}