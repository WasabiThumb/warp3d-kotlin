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

import kotlin.math.PI
import kotlin.math.abs

class MathUtil {

    companion object {

        const val EPSILON: Double = 1E-6
        const val ZERO_TOLERANCE: Double = 1E-12
        const val DEG_TO_RAD: Double = PI / 180.0

        fun fuzzyEquals(a: Double, b: Double): Boolean {
            return abs(a - b) <= EPSILON
        }

    }

}