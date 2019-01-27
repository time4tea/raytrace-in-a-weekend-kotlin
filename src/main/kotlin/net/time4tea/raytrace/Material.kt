package net.time4tea.raytrace

import java.lang.Math.pow
import kotlin.math.sqrt
import kotlin.random.Random

data class Scattered(val attenuation: Vec3, val ray: Ray)

interface Material {
    fun scatter(input: Ray, hit: Hit): Scattered?
}

class Metal(private val albedo: Vec3, private val fuzz: Float) : Material {
    override fun scatter(input: Ray, hit: Hit): Scattered? {
        val reflected = input.direction().unit().reflect(hit.normal)
        val scattered = Ray(hit.p, reflected + Vec3.random_unit_sphere() * fuzz)
        return if (scattered.direction().dot(hit.normal) > 0.0) {
            Scattered(albedo, scattered)
        } else {
            null
        }
    }
}

class Lambertian(private val albedo: Texture) : Material {
    override fun scatter(input: Ray, hit: Hit): Scattered? {
        val target = hit.p + hit.normal + Vec3.random_unit_sphere()
        return Scattered(
            albedo.value(hit.u,hit.v, hit.p), Ray(hit.p, target - hit.p)
        )
    }
}

class Dielectric(private val ri: Float) : Material {

    private fun schlick(cosine: Float, ref_idx: Float): Float {
        var r0 = (1 - ref_idx) / (1 + ref_idx)
        r0 *= r0
        return r0 + (1 - r0) * pow(1 - cosine.toDouble(), 5.0).toFloat()
    }

    override fun scatter(input: Ray, hit: Hit): Scattered? {
        val reflected = input.direction().reflect(hit.normal)

        val attenuation = Vec3.UNIT

        val cosine: Float
        val outward_normal: Vec3
        val ni_over_nt: Float

        if (input.direction().dot(hit.normal) > 0.0f) {
            outward_normal = -hit.normal
            ni_over_nt = ri
            val cosinex = input.direction().dot(hit.normal) / input.direction().length()
            cosine = sqrt(1 - ri * ri * (1 - cosinex * cosinex))
        } else {
            outward_normal = hit.normal
            ni_over_nt = 1.0f / ri
            cosine = -(input.direction().dot(hit.normal)) / input.direction().length()
        }

        val refracted = input.direction().refract(outward_normal, ni_over_nt)

        val reflect_prob = if (refracted != null) {
            schlick(cosine, ri)
        } else {
            1.0f
        }

        return if (Random.nextFloat() < reflect_prob) {
            Scattered(attenuation, Ray(hit.p, reflected))
        } else {
            Scattered(attenuation, Ray(hit.p, refracted!!))
        }
    }
}