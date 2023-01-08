
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
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.SwingConstants

class RenderPanel(pitch: JSlider, yaw: JSlider, roll: JSlider): JPanel() {

    private val image: BufferedImage = BufferedImage(960, 960, BufferedImage.TYPE_INT_ARGB)
    private var rotation: Quaternion = Quaternion.IDENTITY
    private var position: Vector3D = Vector3D(0.0, 0.0, 3.0)
    private var camera: Camera = Camera(960)
    private var quad: Quad3D = Quad3D(
        Vector3D(-1.0, -1.0, -1.0),
        Vector3D(1.0, -1.0, -1.0),
        Vector3D(-1.0, 1.0, -1.0),
        Vector3D(1.0, 1.0, -1.0),
        color = Color.WHITE,
        texture = createTexture()
    )

    init {
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
        Warp3D.renderQuad(image, quad, camera, position, rotation)
        if (g is Graphics2D) {
            g.drawImage(image, AffineTransformOp(AffineTransform.getScaleInstance(0.5, 0.5), AffineTransformOp.TYPE_BILINEAR), 0, 0)
        } else {
            g?.drawImage(image, 0, 0, 480, 480, this)
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