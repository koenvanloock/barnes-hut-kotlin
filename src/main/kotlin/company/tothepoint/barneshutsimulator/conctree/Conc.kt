package company.tothepoint.barneshutsimulator.conctree


sealed class Conc<T> {
    abstract val level: Int
    abstract val size: Int
    abstract fun left(): Conc<T>
    abstract fun right(): Conc<T>
    fun normalized() = this

    companion object {

        fun <T> concatTop(xs: Conc<T>, ys: Conc<T>): Conc<T> {
            return if (xs == Empty) ys
            else if (ys == Empty) xs
            else concat(xs, ys)
        }

        fun <T> concat(xs: Conc<T>, ys: Conc<T>): Conc<T> {
            val diff = ys.level - xs.level
            return if (diff >= -1 && diff <= 1) Fork(xs, ys)
            else if (diff < -1) {
                if (xs.left().level >= xs.right().level) {
                    val nr = concat(xs.right(), ys)
                    Fork(xs.left(), nr)
                } else {
                    val nrr = concat(xs.right().right(), ys)
                    if (nrr.level == xs.level - 3) {
                        val nl = xs.left()
                        val nr = Fork(xs.right().left(), nrr)
                        Fork(nl, nr)
                    } else {
                        val nl = Fork(xs.left(), xs.right().left())
                        val nr = nrr
                        Fork(nl, nr)
                    }
                }
            } else {
                if (ys.right().level >= ys.left().level) {
                    val nl = concat(xs, ys.left())
                    Fork(nl, ys.right())
                } else {
                    val nll = concat(xs, ys.left().left())
                    if (nll.level == ys.level - 3) {
                        val nl = Fork(nll, ys.left().right())
                        val nr = ys.right()
                        Fork(nl, nr)
                    } else {
                        val nl = nll
                        val nr = Fork(ys.left().right(), ys.right())
                        Fork(nl, nr)
                    }
                }
            }
        }

        fun <T, U> traverse(conc: Conc<T>, f: (T) -> U): Unit {
            when (conc) {
                is Fork -> {
                    traverse(conc.left, f)
                    traverse(conc.right, f)
                }
                is Single -> f(conc.elem)
                is Chunk -> {
                    val a = conc.elems
                    val size = conc.size
                    var i = 0
                    while (i < size) {
                        f(a[i])
                        i += 1
                    }
                }
                is Empty -> {
                }
                is Append -> {
                    traverse(conc.left, f)
                    traverse(conc.right, f)
                }
            }
        }

        fun <T> appendTop(xs: Conc<T>, ys: Leaf<T>): Conc<T> = when (xs) {
            is Append -> append(xs, ys)
            is Fork -> Append(xs, ys)
            is Empty -> ys
            is Leaf -> Fork(xs, ys)
        }

        private fun <T> append(xs: Append<T>, ys: Conc<T>): Conc<T> =
                if (xs.right.level > ys.level) Append(xs, ys)
                else {
                    val zs = Fork(xs.right, ys)
                    val lefts = xs.left
                    when (lefts) {
                        is Append -> append(lefts, zs)
                        else -> if (lefts.level <= zs.level) Fork(lefts, zs) else Append(lefts, zs)
                    }
                }
    }
}

data class Fork<T>(val left: Conc<T>, val right: Conc<T>) : Conc<T>() {
    override fun left(): Conc<T> = left
    override fun right(): Conc<T> = right
    override val level = 1 + Math.max(left.level, right.level)
    override val size = left.size + right.size
}

data class Append<T>(val left: Conc<T>, val right: Conc<T>) : Conc<T>() {
    override fun left(): Conc<T> = left
    override fun right(): Conc<T> = right
    override val level = 1 + Math.max(left().level, right().level)
    override val size = left().size + right().size
}

sealed class Leaf<T> : Conc<T>() {
    override fun left() = throw RuntimeException("Leaves do not have children.")
    override fun right() = throw RuntimeException("Leaves do not have children.")

}

object Empty : Leaf<Nothing>() {
    override val level = 0
    override val size = 0
}

data class Single<T>(val elem: T) : Leaf<T>() {
    override val level = 0
    override val size = 1
    override fun toString() = "Single($elem)"
}

data class Chunk<T>(val elems: Array<T>, override val size: Int, val k: Int) : Leaf<T>() {
    override val level = 0
    override fun toString() = "Chunk(${elems.joinToString(",")}, size: $size, $k)"
}