package company.tothepoint.barneshutsimulator.model

class Boundaries() {

    var minX = Float.MAX_VALUE

    var minY = Float.MAX_VALUE

    var maxX = Float.MAX_VALUE

    var maxY = Float.MAX_VALUE

    fun width() = maxX - minX

    fun height() = maxY - minY

    fun size() = Math.max(width(), height())

    fun centerX() = minX + width() / 2

    fun centerY() = minY + height() / 2

    override fun toString() = "Boundaries($minX, $minY, $maxX, $maxY)"
}