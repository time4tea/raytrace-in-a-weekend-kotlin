package net.time4tea.raytrace

import java.awt.event.KeyEvent
import java.awt.event.KeyListener


class KeyMovement(val scene: ControllableScene) : KeyListener {

    override fun keyTyped(p0: KeyEvent) {
    }

    override fun keyPressed(event: KeyEvent) {
        when (event.keyCode) {
            KeyEvent.VK_RIGHT -> scene.moveCamera { from, at ->
                Mat4.rotate(
                    Vec3(0, 1, 0),
                    Angle.Deg(10.0)
                ).transform(from)
            }
            KeyEvent.VK_LEFT -> scene.moveCamera { from, at ->
                Mat4.rotate(
                    Vec3(0, 1, 0),
                    Angle.Deg(-10.0)
                ).transform(from)
            }
            KeyEvent.VK_UP -> scene.moveCamera { from, at ->
                Mat4.rotate(Vec3(1, 0, 0), Angle.Deg(10.0))
                    .transform(from)
            }
            KeyEvent.VK_DOWN -> scene.moveCamera { from, at ->
                Mat4.rotate(
                    Vec3(1, 0, 0),
                    Angle.Deg(-10.0)
                ).transform(from)
            }
            KeyEvent.VK_EQUALS -> scene.moveCamera { from, at ->
                val direction = at - from
                val unit = direction / direction.length()
                from + unit
            }
            KeyEvent.VK_MINUS -> scene.moveCamera { from, at ->
                val direction = at - from
                val unit = direction / direction.length()
                from - unit
            }
            KeyEvent.VK_1 -> scene.samples { it - 1 }
            KeyEvent.VK_2 -> scene.samples { it + 1 }
            KeyEvent.VK_9 -> scene.depth { it - 1 }
            KeyEvent.VK_0 -> scene.depth { it + 1 }
        }
    }

    override fun keyReleased(p0: KeyEvent) {

    }
}