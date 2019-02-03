package net.time4tea.raytrace

import net.time4tea.raytrace.Hitable
import net.time4tea.raytrace.Vec3

interface Scene {
    fun scene(): Hitable
    fun constantLight(): (Vec3) -> Vec3 = { Vec3.ZERO }
    fun lookat(): Vec3
    fun lookfrom(): Vec3
}