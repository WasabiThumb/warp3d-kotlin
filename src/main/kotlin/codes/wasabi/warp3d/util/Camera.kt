package codes.wasabi.warp3d.util

import codes.wasabi.warp3d.math.MathUtil
import codes.wasabi.warp3d.math.Quaternion
import codes.wasabi.warp3d.math.Vector2D
import codes.wasabi.warp3d.math.Vector3D
import kotlin.math.PI
import kotlin.math.tan

class Camera (
    width: Int,
    height: Int = width,
    position: Vector3D = Vector3D(0.0, 0.0, 0.0),
    rotation: Quaternion = Quaternion.IDENTITY,
    fov: Double = PI / 4.0
) {

    private var width = 0
    private var height = 0
    private var widthLarger = false
    private var padding = 0
    private var maxDimension = 0
    private var halfMaxDimension = 0.0

    private val position: Vector3D
    private var rotation: Quaternion
    private var fov: Double
    private var computedInverseRotation = false
    private var computedTanFov = false
    private var shouldProcessRotation = false
    private var inverseRotation: Quaternion = Quaternion.IDENTITY
    private var tanFov: Double = 0.0

    init {
        setSize(width, height)
        this.position = position
        this.rotation = rotation
        this.fov = fov
    }

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
        if (width > height) {
            padding = (width - height).floorDiv(2)
            widthLarger = true
            maxDimension = width
            halfMaxDimension = width / 2.0
            return
        } else if (width < height) {
            padding = (height - width).floorDiv(2)
        } else {
            padding = 0
        }
        widthLarger = false
        maxDimension = height
        halfMaxDimension = height / 2.0
    }

    fun setWidth(width: Int) {
        setSize(width, this.height)
    }

    fun getWidth(): Int {
        return width
    }

    fun setHeight(height: Int) {
        setSize(this.width, height)
    }

    fun getHeight(): Int {
        return height
    }

    fun setPosition(pos: Vector3D) {
        position.apply(pos)
    }

    fun getPosition(): Vector3D {
        return position.copy()
    }

    fun setRotation(rot: Quaternion) {
        if (rotation.fuzzyEquals(rot)) return
        rotation = rot
        computedInverseRotation = false
    }

    fun getRotation(): Quaternion {
        return rotation
    }

    fun setFOV(fov: Double) {
        if (MathUtil.fuzzyEquals(this.fov, fov)) return
        this.fov = fov
        computedTanFov = false
    }

    fun getFOV(): Double {
        return fov
    }

    private fun getInverseRotation(): Quaternion {
        if (!computedInverseRotation) {
            inverseRotation = rotation.inverse()
            shouldProcessRotation = !inverseRotation.fuzzyEquals(Quaternion.IDENTITY)
            computedInverseRotation = true
        }
        return inverseRotation
    }

    private fun getTanFov(): Double {
        if (!computedTanFov) {
            tanFov = tan(fov)
            computedTanFov = true
        }
        return tanFov
    }

    fun project(point: Vector3D): Vector2D? {
        var relative = point.subtract(position)

        val invRot = getInverseRotation()
        if (shouldProcessRotation) { // this boolean is set within getInverseRotation
            relative = invRot.mult(relative)
        }

        if (relative.z <= 0.0) return null

        val tanFov = getTanFov()
        val product = 1.0 / (relative.z * tanFov)

        val pcX = relative.x * product
        val pcY = relative.y * product

        val screenX = ((pcX * halfMaxDimension) + halfMaxDimension)
        val screenY = ((pcY * halfMaxDimension) + halfMaxDimension)

        return if (widthLarger) {
            Vector2D(screenX, screenY - padding)
        } else {
            Vector2D(screenX - padding, screenY)
        }
    }

}