package net.time4tea.raytrace

interface Scene {
    fun scene(): Hitable
    fun constantLight(): (Vec3) -> Colour = { Colour.BLACK }
    fun lookat(): Vec3
    fun lookfrom(): Vec3
}