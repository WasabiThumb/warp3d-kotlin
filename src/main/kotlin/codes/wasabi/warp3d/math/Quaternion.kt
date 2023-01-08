package codes.wasabi.warp3d.math

import kotlin.math.*

data class Quaternion(val x: Double, val y: Double, val z: Double, val w: Double) {
    
    companion object {

        val IDENTITY = Quaternion(0.0, 0.0, 0.0, 1.0)

        fun fromAngles(xAngle: Double, yAngle: Double, zAngle: Double): Quaternion {
            var angle = zAngle * 0.5
            val sinZ = sin(angle)
            val cosZ = cos(angle)
            angle = yAngle * 0.5
            val sinY = sin(angle)
            val cosY = cos(angle)
            angle = xAngle * 0.5
            val sinX = sin(angle)
            val cosX = cos(angle)

            val cosYXcosZ = cosY * cosZ
            val sinYXsinZ = sinY * sinZ
            val cosYXsinZ = cosY * sinZ
            val sinYXcosZ = sinY * cosZ

            return Quaternion(
                cosYXcosZ * sinX + sinYXsinZ * cosX,
                sinYXcosZ * cosX + cosYXsinZ * sinX,
                cosYXsinZ * cosX - sinYXcosZ * sinX,
                cosYXcosZ * cosX - sinYXsinZ * sinX
            ).normalize()
        }

        fun slerp(a: Quaternion, b: Quaternion, amount: Double): Quaternion {
            return a.slerp(b, amount)
        }
        
    }

    fun fuzzyEquals(q2: Quaternion): Boolean {
        if (!MathUtil.fuzzyEquals(x, q2.x)) return false
        if (!MathUtil.fuzzyEquals(y, q2.y)) return false
        if (!MathUtil.fuzzyEquals(z, q2.z)) return false
        return MathUtil.fuzzyEquals(w, q2.w)
    }

    fun slerp(other: Quaternion, amount: Double): Quaternion {
        var q2 = other
        if (fuzzyEquals(q2)) return this

        var res = x * q2.x + y * q2.y + z * q2.z + w * q2.w
        if (res < 0.0) {
            q2 = q2.negate()
            res = -res
        }

        var scale0 = 1 - amount
        var scale1 = amount

        if ((1 - res) > 0.1) {
            val theta = acos(res)
            val invSinTheta = 1.0 / sin(theta)

            scale0 = sin((1 - amount) * theta) * invSinTheta
            scale1 = sin(amount * theta) * invSinTheta
        }

        return Quaternion(
            (scale0 * x) + (scale1 * q2.x),
            (scale0 * y) + (scale1 * q2.y),
            (scale0 * z) + (scale1 * q2.z),
            (scale0 * w) + (scale1 * q2.w)
        )
    }

    fun normalize(): Quaternion {
        val lenSqr = norm()
        val len: Double = if (lenSqr < MathUtil.ZERO_TOLERANCE) {
            return IDENTITY
        } else if (MathUtil.fuzzyEquals(lenSqr, 1.0)) {
            1.0
        } else {
            sqrt(lenSqr)
        }
        return Quaternion(x / len, y / len, z / len, w / len)
    }

    fun mult(q: Quaternion): Quaternion {
        return Quaternion(
            x * q.w + y * q.z - z * q.y + w * q.x,
            -x * q.z + y * q.w + z * q.x + w * q.y,
            x * q.y - y * q.x + z * q.w + w * q.z,
            -x * q.x - y * q.y - z * q.z + w * q.w
        )
    }

    fun mult(v: Vector3D): Vector3D {
        return Vector3D(
            w * w * v.x + 2 * y * w * v.z - 2 * z * w * v.y + x * x * v.x + 2 * y * x * v.y + 2 * z * x * v.z - z * z * v.x - y * y * v.x,
            2 * x * y * v.x + y * y * v.y + 2 * z * y * v.z + 2 * w * z * v.x - z * z * v.y + w * w * v.y - 2 * x * w * v.z - x * x * v.y,
            2 * x * z * v.x + 2 * y * z * v.y + z * z * v.z - 2 * w * y * v.x - y * y * v.z + 2 * w * x * v.y - x * x * v.z + w * w * v.z
        )
    }

    fun dot(q: Quaternion): Double {
        return (x * q.x) + (y * q.y) + (z * q.z) + (w * q.w)
    }

    fun norm(): Double {
        return dot(this)
    }

    fun inverse(): Quaternion {
        val norm = norm()
        if (norm > 0.0) {
            val invNorm = 1.0 / norm
            return Quaternion(
                -x * invNorm,
                -y * invNorm,
                -z * invNorm,
                w * invNorm
            )
        }
        return IDENTITY
    }

    fun negate(): Quaternion {
        return Quaternion(-x, -y, -z, -w)
    }
    
}
