package codes.wasabi.warp3d.render

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

import codes.wasabi.warp3d.math.MathUtil
import codes.wasabi.warp3d.math.Vector2D
import codes.wasabi.warp3d.util.UVSampler
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

data class Quad2D(
    val topLeft: Vector2D,
    val topRight: Vector2D,
    val bottomLeft: Vector2D,
    val bottomRight: Vector2D,

    val uvTopLeft: Vector2D = Vector2D(0.0, 0.0),
    val uvTopRight: Vector2D = Vector2D(1.0, 0.0),
    val uvBottomLeft: Vector2D = Vector2D(0.0, 1.0),
    val uvBottomRight: Vector2D = Vector2D(1.0, 1.0),

    var texture: BufferedImage? = null,
    var color: Color = Color.WHITE
) {

    private fun transformUV(uv: Vector2D?): Vector2D? {
        if (uv == null) return null
        val a = uvTopLeft.lerp(uvTopRight, uv.x)
        val b = uvBottomLeft.lerp(uvBottomRight, uv.x)
        val ret = a.lerp(b, uv.y)
        if (ret.x !in 0.0..1.0) return null
        if (ret.y !in 0.0..1.0) return null
        return ret
    }

    fun translate(vector: Vector2D): Quad2D {
        return Quad2D(
            topLeft.add(vector),
            topRight.add(vector),
            bottomLeft.add(vector),
            bottomRight.add(vector),
            uvTopLeft, uvTopRight, uvBottomLeft, uvBottomRight, texture, color
        )
    }

    fun rotate(radians: Double): Quad2D {
        if (abs(radians) < MathUtil.ZERO_TOLERANCE) return copy()
        return Quad2D(
            topLeft.rotate(radians),
            topRight.rotate(radians),
            bottomLeft.rotate(radians),
            bottomRight.rotate(radians),
            uvTopLeft, uvTopRight, uvBottomLeft, uvBottomRight, texture, color
        )
    }

    fun render(canvas: BufferedImage): Int {
        if (texture == null) {
            val g2d = canvas.createGraphics()
            g2d.color = this.color
            g2d.fillPolygon(intArrayOf(
                topLeft.x.toInt(),
                topRight.x.toInt(),
                bottomRight.x.toInt(),
                bottomLeft.x.toInt()
            ), intArrayOf(
                topLeft.y.toInt(),
                topRight.y.toInt(),
                bottomRight.y.toInt(),
                bottomLeft.y.toInt()
            ), 4)
            g2d.dispose()
            return 4
        } else {
            val pcRed = this.color.red / 255.0
            val pcGreen = this.color.green / 255.0
            val pcBlue = this.color.blue / 255.0

            val tex = texture!!
            val sampler = UVSampler(topLeft, topRight, bottomLeft, bottomRight)

            val maxWidth = canvas.width - 1
            val maxHeight = canvas.height - 1
            val minX = max(minOf(maxWidth, topLeft.x.toInt(), topRight.x.toInt(), bottomLeft.x.toInt(), bottomRight.x.toInt()), 0)
            val minY = max(minOf(maxHeight, topLeft.y.toInt(), topRight.y.toInt(), bottomLeft.y.toInt(), bottomRight.y.toInt()), 0)
            val maxX = min(maxOf(0, topLeft.x.toInt(), topRight.x.toInt(), bottomLeft.x.toInt(), bottomRight.x.toInt()), maxWidth)
            val maxY = min(maxOf(0, topLeft.y.toInt(), topRight.y.toInt(), bottomLeft.y.toInt(), bottomRight.y.toInt()), maxHeight)
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    val uv = transformUV(sampler.sampleUV(Vector2D(x.toDouble(), y.toDouble()))) ?: continue
                    val rgb = tex.getRGB(
                        min(floor(uv.x * tex.width).toInt(), tex.width - 1),
                        min(floor(uv.y * tex.height).toInt(), tex.height - 1)
                    )
                    val red = (((rgb shr 16) and 0xFF) * pcRed).toInt()
                    val green = (((rgb shr 8) and 0xFF) * pcGreen).toInt()
                    val blue = ((rgb and 0xFF) * pcBlue).toInt()
                    canvas.setRGB(x, y, (0xFF shl 24) or ((red and 0xFF) shl 16) or ((green and 0xFF) shl 8) or ((blue and 0xFF) shl 0))
                }
            }
            return sampler.getType()
        }
    }

}
