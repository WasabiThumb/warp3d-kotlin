package codes.wasabi.warp3d.render

import codes.wasabi.warp3d.math.Quaternion
import codes.wasabi.warp3d.math.Vector2D
import codes.wasabi.warp3d.math.Vector3D
import codes.wasabi.warp3d.util.Camera
import java.awt.Color
import java.awt.image.BufferedImage

data class Quad3D(
    val topLeft: Vector3D,
    val topRight: Vector3D,
    val bottomLeft: Vector3D,
    val bottomRight: Vector3D,

    val uvTopLeft: Vector2D = Vector2D(0.0, 0.0),
    val uvTopRight: Vector2D = Vector2D(1.0, 0.0),
    val uvBottomLeft: Vector2D = Vector2D(0.0, 1.0),
    val uvBottomRight: Vector2D = Vector2D(1.0, 1.0),

    var texture: BufferedImage? = null,
    var color: Color = Color.WHITE
) {

    fun translate(vector: Vector3D): Quad3D {
        return Quad3D(
            topLeft.add(vector),
            topRight.add(vector),
            bottomLeft.add(vector),
            bottomRight.add(vector),
            uvTopLeft, uvTopRight, uvBottomLeft, uvBottomRight, texture, color
        )
    }

    fun rotate(rotation: Quaternion): Quad3D {
        if (rotation.fuzzyEquals(Quaternion.IDENTITY)) return copy()
        return Quad3D(
            rotation.mult(topLeft),
            rotation.mult(topRight),
            rotation.mult(bottomLeft),
            rotation.mult(bottomRight),
            uvTopLeft, uvTopRight, uvBottomLeft, uvBottomRight, texture, color
        )
    }

    fun project(camera: Camera): Quad2D? {
        return Quad2D(
            camera.project(topLeft) ?: return null,
            camera.project(topRight) ?: return null,
            camera.project(bottomLeft) ?: return null,
            camera.project(bottomRight) ?: return null,
            uvTopLeft, uvTopRight, uvBottomLeft, uvBottomRight, texture, color
        )
    }

}
