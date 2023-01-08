package codes.wasabi.warp3d.util

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

import codes.wasabi.warp3d.math.Vector2D
import codes.wasabi.warp3d.math.MathUtil
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class UVSampler(topLeft: Vector2D, topRight: Vector2D, bottomLeft: Vector2D, bottomRight: Vector2D) {

    private val p: Double
    private val o: Double
    private val u: Double
    private val t: Double

    private val l: Double
    private val k: Double
    private val j: Double
    private val h: Double

    private val type: Int

    init {
        val topHorizontal = MathUtil.fuzzyEquals(topLeft.y, topRight.y)
        val bottomHorizontal = MathUtil.fuzzyEquals(bottomLeft.y, bottomRight.y)
        val horizontal = topHorizontal && bottomHorizontal
        val yAdd = if (topHorizontal xor bottomHorizontal) MathUtil.EPSILON else 0.0

        val leftVertical = MathUtil.fuzzyEquals(topLeft.x, bottomLeft.x)
        val rightVertical = MathUtil.fuzzyEquals(topRight.x, bottomRight.x)
        val vertical = leftVertical && rightVertical
        val xAdd = if (leftVertical xor rightVertical) MathUtil.EPSILON else 0.0

        p = topLeft.x
        l = topLeft.y
        o = topRight.x
        k = topRight.y + yAdd
        u = bottomLeft.x + xAdd
        j = bottomLeft.y
        t = bottomRight.x + xAdd
        h = bottomRight.y + yAdd

        var t = makeFlag(horizontal, 1) or makeFlag(vertical, 0)
        if (t == 0) {
            // check angles for special case
            val dotA = topRight.subtract(topLeft).dot(bottomLeft.subtract(topLeft))
            if (abs(dotA) < MathUtil.EPSILON) {
                val dotB = topLeft.subtract(topRight).dot(bottomRight.subtract(topRight))
                if (abs(dotB) < MathUtil.EPSILON) {
                    t = 4
                }
            }
        }
        type = t
    }

    fun getType(): Int {
        return type
    }

    private fun makeFlag(value: Boolean, place: Int): Int {
        return if (value) 1 shl place else 0
    }

    private fun sampleU(samplePos: Vector2D): Double {
        val v = samplePos.x
        val b = samplePos.y

        val v6 = (h - j) * o - (h - j) * p - (k - l) * t + (k - l) * u
        if (v6 == 0.0) return -1.0

        val b2 = b.pow(2.0)
        val j2 = j.pow(2.0)
        val h2 = h.pow(2.0)
        val l2 = l.pow(2.0)
        val k2 = k.pow(2.0)

        val v1 = b2 - 2 * b * h
        var v5 = (b2 - 2 * b * j + j2) * o.pow(2.0) - 2 * (b2 - b * h - (b - h) * j) * o * p + (v1 + h2) * p.pow(2.0) + (b2 - 2 * b * l + l2) * t.pow(2.0) + (b2 - 2 * b * k + k2) * u.pow(2.0) + (h2 - 2 * h * j + j2 - 2 * (h - j) * k + k2 + 2 * (h - j - k) * l + l2) * v.pow(2.0) - 2 * ((b2 - b * j - (b - j) * l) * o - (b2 + b * h - 2 * b * j - 2 * (b - j) * k + (b - h) * l) * p) * t + 2 * ((v1 + b * j + (b - j) * k - 2 * (b - h) * l) * o - (b2 - b * h - (b - h) * k) * p - (b2 - b * k - (b - k) * l) * t) * u + 2 * ((b * h - (b - h) * j - j2 - (b - j) * k + (b - 2 * h + j) * l) * o - (b * h + h2 - (b + h) * j - (b + h - 2 * j) * k + (b - h) * l) * p - (b * h - b * j - (b - 2 * j) * k + (b - h - j - k) * l + l2) * t + (b * h - b * j - (b - h - j) * k - k2 + (b - 2 * h + k) * l) * u) * v

        if (v5 < 0.0) return -1.0

        val v2 = (b - j) * o - (b + h - 2 * j) * p - (b - l) * t
        val v3 = (b + k - 2 * l) * u
        val v4 = (h - j - k + l) * v
        val v7 = v2 + v3 + v4

        v5 = sqrt(v5)

        var ret = 0.5 * (v7 - v5) / v6
        if (ret in 0.0..1.0) return ret

        ret = 0.5 * (v7 + v5) / v6
        return if (ret.isNaN()) -1.0 else ret
    }

    private fun sampleV(samplePos: Vector2D, sampleU: Double): Double {
        val v = samplePos.x
        val b = samplePos.y

        val tx = p + (o - p) * sampleU
        val bx = u + (t - u) * sampleU

        val diffX = bx - tx
        if (abs(diffX) < MathUtil.ZERO_TOLERANCE) {
            val ty = l + (k - l) * sampleU
            val by = j + (h - j) * sampleU
            return (b - ty) / (by - ty)
        }
        return (v - tx) / diffX
    }

    private fun safeDiv(a: Double, b: Double): Double {
        if (abs(b) < MathUtil.ZERO_TOLERANCE) return 0.0
        return a / b
    }

    fun sampleUV(samplePos: Vector2D, out: DoubleArray): Boolean {
        if (type < 1) {
            val u = sampleU(samplePos)
            if (u !in 0.0..1.0) return false
            val v = sampleV(samplePos, u)
            if (v !in 0.0..1.0) return false
            out[0] = u
            out[1] = v
            return true
        } else if ((type and 1) == 1) {
            val u = safeDiv(samplePos.x - p, o - p)
            if (u !in 0.0..1.0) return false
            val v = if (type == 3) {
                safeDiv(samplePos.y - l, j - l)
            } else {
                sampleV(samplePos, u)
            }
            if (v !in 0.0..1.0) return false
            out[0] = u
            out[1] = v
            return true
        } else if (type == 4) {
            // Quad is a rectangle, but no edges are aligned with any axes
            val mt = (k - l) / (o - p)
            val ms = -1.0 / mt
            val bt = samplePos.y - (mt * samplePos.x)
            val bs1 = l - (ms * p)
            val bs2 = k - (ms * o)
            val dm = mt - ms
            val min = (bs1 - bt) / dm
            val max = (bs2 - bt) / dm
            val u = (samplePos.x - min) / (max - min)

            if (u !in 0.0..1.0) return false
            val v = sampleV(samplePos, u)
            if (v !in 0.0..1.0) return false
            out[0] = u
            out[1] = v
            return true
        } else {
            val v = safeDiv(samplePos.y - l, j - l)
            if (v !in 0.0..1.0) return false
            val leftX = p + ((u - p) * v)
            val rightX = o + ((t - o) * v)
            val u = safeDiv(samplePos.x - leftX, rightX - leftX)
            if (u !in 0.0..1.0) return false
            out[0] = u
            out[1] = v
            return true
        }
    }

    fun sampleUV(samplePos: Vector2D): Vector2D? {
        val out = DoubleArray(2)
        if (!sampleUV(samplePos, out)) return null
        return Vector2D(out[0], out[1])
    }

}