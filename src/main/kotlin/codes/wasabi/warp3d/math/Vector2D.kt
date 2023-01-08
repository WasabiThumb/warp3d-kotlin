package codes.wasabi.warp3d.math

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

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class Vector2D(var x: Double, var y: Double) {

    fun lerp(other: Vector2D, amount: Double): Vector2D {
        val inv = 1.0 - amount
        return Vector2D(
            x * inv + other.x * amount,
            y * inv + other.y * amount
        )
    }

    fun add(other: Vector2D): Vector2D {
        return Vector2D(x + other.x, y + other.y)
    }

    fun subtract(other: Vector2D): Vector2D {
        return Vector2D(x - other.x, y - other.y)
    }

    fun rotate(radians: Double): Vector2D {
        if (abs(radians) < MathUtil.ZERO_TOLERANCE) return copy()
        val cosAng = cos(radians)
        val sinAng = sin(radians)
        return Vector2D(
            x * cosAng - y * sinAng,
            x * sinAng + y * cosAng
        )
    }

    fun dot(other: Vector2D): Double {
        return (x * other.x) + (y * other.y)
    }

}
