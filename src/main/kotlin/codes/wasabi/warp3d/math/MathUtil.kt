package codes.wasabi.warp3d.math

import kotlin.math.PI
import kotlin.math.abs

class MathUtil {

    companion object {

        const val EPSILON: Double = 1E-12
        const val ZERO_TOLERANCE: Double = 1E-16
        const val DEG_TO_RAD: Double = PI / 180.0

        fun fuzzyEquals(a: Double, b: Double): Boolean {
            return abs(a - b) <= ZERO_TOLERANCE
        }

    }

}