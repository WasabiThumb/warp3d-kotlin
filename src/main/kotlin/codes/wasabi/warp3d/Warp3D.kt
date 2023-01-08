package codes.wasabi.warp3d

import codes.wasabi.warp3d.math.Quaternion
import codes.wasabi.warp3d.math.Vector2D
import codes.wasabi.warp3d.math.Vector3D
import codes.wasabi.warp3d.render.Quad2D
import codes.wasabi.warp3d.render.Quad3D
import codes.wasabi.warp3d.util.Camera
import java.awt.image.BufferedImage

class Warp3D {

    companion object {

        fun renderQuad(canvas: BufferedImage, quad: Quad2D, position: Vector2D = Vector2D(0.0, 0.0), rotation: Double = 0.0) {
            quad.rotate(rotation).translate(position).render(canvas)
        }

        fun renderQuad(canvas: BufferedImage, quad: Quad3D, camera: Camera? = null, position: Vector3D = Vector3D(0.0, 0.0, 0.0), rotation: Quaternion = Quaternion.IDENTITY) {
            quad.rotate(rotation).translate(position).project(camera ?: Camera(canvas.width, canvas.height))?.render(canvas)
        }

    }

}