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

import codes.wasabi.warp3d.Warp3D
import codes.wasabi.warp3d.math.MathUtil
import codes.wasabi.warp3d.math.Quaternion
import codes.wasabi.warp3d.math.Vector3D
import codes.wasabi.warp3d.render.Quad3D
import codes.wasabi.warp3d.util.Camera
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.text.DecimalFormat
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.SwingConstants
import kotlin.math.pow

class RenderPanel(pitch: JSlider, yaw: JSlider, roll: JSlider): JPanel() {

    private val image: BufferedImage = BufferedImage(960, 960, BufferedImage.TYPE_INT_ARGB)
    private var rotation: Quaternion = Quaternion.IDENTITY
    private var position: Vector3D = Vector3D(0.0, 0.0, 3.0)
    private var camera: Camera = Camera(960)
    private var quads: Array<Quad3D>
    private var numToRender = 2
    private var decimalFormat = DecimalFormat("0.##")

    init {
        val tex = createTexture()
        quads = arrayOf(
            Quad3D(
                Vector3D(-1.0, -1.0, -1.0),
                Vector3D(1.0, -1.0, -1.0),
                Vector3D(-1.0, 1.0, -1.0),
                Vector3D(1.0, 1.0, -1.0),
                texture = tex
            ),
            Quad3D(
                Vector3D(1.0, -1.0, 1.0),
                Vector3D(-1.0, -1.0, 1.0),
                Vector3D(1.0, 1.0, 1.0),
                Vector3D(-1.0, 1.0, 1.0),
                texture = tex
            )
        )

        pitch.addChangeListener { updateRotation(pitch, yaw, roll) }
        yaw.addChangeListener { updateRotation(pitch, yaw, roll) }
        roll.addChangeListener { updateRotation(pitch, yaw, roll) }
    }

    private fun createTexture(): BufferedImage {
        val size = 16
        val ret = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until size) {
            var c = x.mod(2) == 1
            for (y in 0 until size) {
                c = !c
                ret.setRGB(x, y, if (c) 0xff00ff else 0)
            }
        }
        return ret
    }

    private fun updateRotation(pitch: JSlider, yaw: JSlider, roll: JSlider) {
        rotation = Quaternion.fromAngles(
            pitch.value * MathUtil.DEG_TO_RAD,
            yaw.value * MathUtil.DEG_TO_RAD,
            roll.value * MathUtil.DEG_TO_RAD
        )
        repaint()
    }

    override fun paint(g: Graphics?) {
        val g2 = image.createGraphics()
        g2.background = Color(40, 40, 40)
        g2.clearRect(0, 0, 960, 960)
        g2.dispose()
        val dict = HashMap<Quad3D, Double>()
        for (quad in quads) {
            val q = quad.rotate(rotation).translate(position)
            val sum = q.topLeft.add(q.topRight).add(q.bottomLeft).add(q.bottomRight)
            dict[q] = (sum.x / 4.0).pow(2.0) + (sum.y / 4.0).pow(2.0) + (sum.z / 4.0).pow(2.0)
        }
        val list = dict.keys.sortedByDescending { dict[it] }
        val startTime = System.nanoTime()
        var i = -1
        for (q in list) {
            i++
            if (i >= numToRender) return
            q.project(camera)?.render(image);
        }
        val elapsed = System.nanoTime() - startTime
        val elapsedMillis = elapsed / 1.0e6
        if (g is Graphics2D) {
            g.drawImage(image, AffineTransformOp(AffineTransform.getScaleInstance(0.5, 0.5), AffineTransformOp.TYPE_BILINEAR), 0, 0)
        } else {
            g?.drawImage(image, 0, 0, 480, 480, this)
        }
        val formatted = decimalFormat.format(elapsedMillis)
        g?.let {
            it.font = Font(Font.MONOSPACED, Font.PLAIN, 20)
            it.color = Color.RED
            it.drawString("$formatted ms", 5, 20)
        }
    }

}

fun main(args: Array<String>) {
    val frame = JFrame("Warp3D")
    frame.setSize(800, 480)
    frame.layout = null
    frame.menuBar = null
    frame.isResizable = false
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    val form = JPanel()
    val layout = GridLayout(10, 0)
    layout.vgap = 0
    form.layout = layout
    form.setSize(320, 480)
    form.setLocation(0, 0)
    form.background = Color(255, 255, 190)

    val label = JLabel("Warp3D Demo")
    label.font = Font(Font.SANS_SERIF, Font.BOLD, 20)
    label.horizontalAlignment = SwingConstants.CENTER
    label.preferredSize = Dimension(0, 0)
    form.add(label)

    val pitchSlider = createSlider("Pitch", form)
    val yawSlider = createSlider("Yaw", form)
    val rollSlider = createSlider("Roll", form)

    frame.add(form)

    val imageArea = RenderPanel(pitchSlider, yawSlider, rollSlider)
    imageArea.setSize(480, 480)
    imageArea.setLocation(320, 0)
    frame.add(imageArea)

    frame.isVisible = true
    frame.requestFocus()
}

private fun createSlider(title: String, form: JPanel): JSlider {
    val sliderLabel = JLabel(title)
    sliderLabel.horizontalAlignment = SwingConstants.CENTER
    val slider = JSlider(0, 360, 0)
    slider.background = Color(255, 255, 190)
    form.add(sliderLabel)
    form.add(slider)
    return slider
}