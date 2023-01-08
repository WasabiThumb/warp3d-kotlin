package codes.wasabi.warp3d

/*
   Copyright 2022 Wasabi Codes

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import codes.wasabi.warp3d.math.Quaternion
import codes.wasabi.warp3d.math.Vector2D
import codes.wasabi.warp3d.math.Vector3D
import codes.wasabi.warp3d.render.Quad2D
import codes.wasabi.warp3d.render.Quad3D
import codes.wasabi.warp3d.util.Camera
import java.awt.image.BufferedImage

class Warp3D {

    companion object {

        fun renderQuad(canvas: BufferedImage, quad: Quad2D, position: Vector2D = Vector2D(0.0, 0.0), rotation: Double = 0.0): Int {
            return quad.rotate(rotation).translate(position).render(canvas)
        }

        fun renderQuad(canvas: BufferedImage, quad: Quad3D, camera: Camera? = null, position: Vector3D = Vector3D(0.0, 0.0, 0.0), rotation: Quaternion = Quaternion.IDENTITY): Int {
            return (quad.rotate(rotation).translate(position).project(camera ?: Camera(canvas.width, canvas.height))?.render(canvas)) ?: -1
        }

    }

}