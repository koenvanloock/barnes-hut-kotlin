package company.tothepoint.barneshutsimulator.view

import company.tothepoint.barneshutsimulator.controller.BarnesHutController
import company.tothepoint.barneshutsimulator.model.Fork
import company.tothepoint.barneshutsimulator.model.Quad
import company.tothepoint.barneshutsimulator.utils.ArrayUtils.emptyArrayOfSize
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class BarnesHutCanvas(val barnesHutController: BarnesHutController) : Canvas(800.0, 600.0) {

    val MAX_RES = 3000

    val pixels = emptyArrayOfSize<Int>(MAX_RES * MAX_RES)

    init {
        repaint()
    }

    fun repaint() {
        val graphics = this.graphicsContext2D
        val width = getWidth().toInt()
        val height = getHeight().toInt()
        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val imgGraphics = img.getGraphics() as Graphics2D
        clearCanvas()
        updatePixelsWithBodyIntensity()
        updateImageWithPixelIntensity(img)
        if (barnesHutController.shouldRenderQuad()) {
            renderQuad(imgGraphics)
        }
        graphics.drawImage(SwingFXUtils.toFXImage(img, null), 0.0, 0.0)
    }

    fun clearCanvas() {
        for (x in 0 until MAX_RES) {
            for (y in 0 until MAX_RES) {
                pixels[y * width.toInt() + x] = 0
            }
        }
    }

    fun updatePixelsWithBodyIntensity() {
        for (b in barnesHutController.bodies()) {
            val px = ((b.x - barnesHutController.screen().minX) / barnesHutController.screen().width() * width).toInt()
            val py = ((b.y - barnesHutController.screen().minY) / barnesHutController.screen().height() * height).toInt()
            if (px >= 0 && px < width && py >= 0 && py < height) pixels[py * width.toInt() + px] += 1
        }
    }

    fun updateImageWithPixelIntensity(img: BufferedImage) {
        for (y in 0 until height.toInt()) {
            for (x in 0 until width.toInt()) {
                val count = pixels[y * width.toInt() + x]
                val intensity = if (count > 0) Math.min(255, 70 + count * 50) else 0
                val color = (255 shl 24) or (intensity shl 16) or (intensity shl 8) or intensity
                img.setRGB(x, y, color)
            }
        }
    }

    fun renderQuad(graphics: Graphics2D) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val green = Color(0, 225, 80, 150)
        graphics.color = green
        fun drawQuad(depth: Int, quad: Quad): Unit {
            fun drawRect(fx: Float, fy: Float, fsz: Float, q: Quad, fill: Boolean = false) {
                val screen = barnesHutController.screen()
                val x = ((fx - screen.minX) / screen.width() * width).toInt()
                val y = ((fy - screen.minY) / screen.height() * height).toInt()
                val w = (((fx + fsz - screen.minX) / screen.width() * width) - x).toInt()
                val h = (((fy + fsz - screen.minY) / screen.height() * height) - y).toInt()
                graphics.drawRect(x, y, w, h)
                if (fill) graphics.fillRect(x, y, w, h)
                if (depth <= 5) graphics.drawString("#:" + q.total, x + w / 2, y + h / 2)
            }

            when (quad) {
                is Fork -> {
                    val cx = quad.centerX
                    val cy = quad.centerY
                    val sz = quad.size
                    drawRect(cx - sz / 2, cy - sz / 2, sz / 2, quad.nw)
                    drawRect(cx - sz / 2, cy, sz / 2, quad.sw)
                    drawRect(cx, cy - sz / 2, sz / 2, quad.ne)
                    drawRect(cx, cy, sz / 2, quad.se)
                    drawQuad(depth + 1, quad.nw)
                    drawQuad(depth + 1, quad.ne)
                    drawQuad(depth + 1, quad.sw)
                    drawQuad(depth + 1, quad.se)
                }
                else -> {
                }
            }
        }
        drawQuad(0, barnesHutController.quad())
    }

}