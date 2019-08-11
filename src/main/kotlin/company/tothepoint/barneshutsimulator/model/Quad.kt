package company.tothepoint.barneshutsimulator.model

val minimumSize = 0.00001f

sealed class Quad {
    abstract val massX: Float
    abstract val massY: Float
    abstract val mass: Float
    abstract val centerX: Float
    abstract val centerY: Float
    abstract val size: Float
    abstract val total: Int
    abstract infix fun insert(body: Body): Quad
}

data class Empty(
        override val centerX: Float,
        override val centerY: Float,
        override val size: Float) : Quad() {
    override val total = 0
    override val mass = 0.0f
    override val massX = centerX
    override val massY = centerY
    override fun insert(body: Body) = Leaf(centerX, centerY, size, listOf(body))
}

data class Fork(
        val nw: Quad,
        val ne: Quad,
        val sw: Quad,
        val se: Quad
): Quad() {
    override val centerX: Float = nw.centerX + nw.size / 2
    override val centerY: Float = nw.centerY + nw.size / 2
    override val size = 2 * ne.size
    override val mass: Float = nw.mass + ne.mass + sw.mass + se.mass
    override val massX: Float = if(mass != 0f)  (nw.mass * nw.massX + ne.mass * ne.massX + sw.mass * sw.massX + se.mass * se.massX ) / mass else centerX
    override val massY: Float = if(mass != 0f)  (nw.mass * nw.massY + ne.mass * ne.massY + sw.mass * sw.massY + se.mass * se.massY) / mass else centerY
    override val total: Int = nw.total + ne.total + sw.total + se.total
    override fun  insert(body: Body): Fork =
        if (body.x < centerX + ne.size && body.y < centerY + ne.size) {
            if (body.x < centerX) {
                if (body.y < centerY) {
                    Fork(nw.insert(body), ne, sw, se)
                } else {
                    Fork(nw, ne, sw.insert(body), se)
                }
            } else {
                if (body.y < centerY) {
                    Fork(nw, ne.insert(body), sw, se)
                } else {
                    Fork(nw, ne, sw, se.insert(body))
                }
            }
        } else this
}

data class Leaf(override val centerX: Float,
                override val centerY: Float,
                override val size: Float,
                val bodies: List<Body>) : Quad() {

    val triplet = convert(
            bodies.fold(Triple(0f, 0f, 0f))
            { acc: Triple<Float, Float, Float>, body: Body -> Triple(acc.first + body.mass, acc.second + body.x, acc.third + body.y) })

    private fun convert(triple: Triple<Float, Float, Float>): Triple<Float, Float, Float> = kotlin.Triple(triple.first, triple.second / bodies.size, triple.third / bodies.size)

    override val mass: Float = triplet.first
    override val massX: Float = triplet.second
    override val massY: Float = triplet.third
    override val total = bodies.size
    override fun insert(body: Body): Quad  =
        if (size > minimumSize) {
            (bodies.fold(Fork(
                    Empty(centerX - size / 4, centerY - size / 4, size / 2),
                    Empty(centerX + size / 4, centerY - size / 4, size / 2),
                    Empty(centerX - size / 4, centerY + size / 4, size / 2),
                    Empty(centerX + size / 4, centerY + size / 4, size / 2)
                    ))
            { acc: Quad, b: Body -> acc.insert(b)}).insert(body)
        } else Leaf(centerX, centerY, size, bodies.plus(body))
}