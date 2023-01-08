package codes.wasabi.warp3d.math

data class Vector3D(var x: Double, var y: Double, var z: Double) {

    fun apply(other: Vector3D) {
        this.x = other.x
        this.y = other.y
        this.z = other.z
    }

    fun add(other: Vector3D): Vector3D {
        return Vector3D(x + other.x, y + other.y, z + other.z)
    }

    fun subtract(other: Vector3D): Vector3D {
        return Vector3D(x - other.x, y - other.y, z - other.z)
    }

}
