package codes.wasabi.warp3d.math

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

    fun rotate(radians: Double): Vector2D {
        if (abs(radians) < MathUtil.ZERO_TOLERANCE) return copy()
        val cosAng = cos(radians)
        val sinAng = sin(radians)
        return Vector2D(
            x * cosAng - y * sinAng,
            x * sinAng + y * cosAng
        )
    }

}
