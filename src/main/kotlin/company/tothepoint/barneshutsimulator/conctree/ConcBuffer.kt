package company.tothepoint.barneshutsimulator.conctree

import company.tothepoint.barneshutsimulator.conctree.ConcOps.merge
import company.tothepoint.barneshutsimulator.model.Body
import company.tothepoint.barneshutsimulator.utils.ArrayUtils.emptyArrayOfSize

class ConcBuffer(val k: Int, var conc: Conc<Body>) {

    constructor() : this(128, Empty as Conc<Body>)

    private var chunk: Array<Body> = emptyArrayOfSize(k)
    private var lastSize: Int = 0
    fun size() = lastSize

    fun <U> foreach(f: (Body) -> U) {
        conc.forEach(f)

        var i = 0
        while (i < lastSize) {
            f(chunk[i])
            i += 1
        }
    }

    infix fun add(elem: Body): ConcBuffer {
        if (lastSize >= k) expand()
        chunk[lastSize] = elem
        lastSize += 1
        return this
    }

    infix fun combine(that: ConcBuffer): ConcBuffer {
        val combinedConc = this.result() merge that.result()
        this.clear()
        that.clear()
        return ConcBuffer(k, combinedConc)
    }

    private fun pack() {
        conc = Conc.appendTop(conc, Chunk(chunk, lastSize, k))
    }

    private fun expand() {
        pack()
        chunk = emptyArrayOfSize<Body>(k)
        lastSize = 0
    }

    fun clear() {
        conc = Empty as Conc<Body>
        chunk = emptyArrayOfSize(k)
        lastSize = 0
    }

    fun result(): Conc<Body> {
        pack()
        return conc
    }

    fun <T, U> Conc<T>.forEach(f: (T) -> U) = Conc.traverse(this, f)

    fun <U> foldLeft(quad: U, f: (U, Body) -> U): U {
        var result = quad
        this.foreach { result = f(result, it) }
        return result
    }

    fun filter(f: (Body) -> Boolean): Conc<Body>  = foldLeft(Empty as Conc<Body>) {
        conc: Conc<Body>, b: Body ->
            if(f(b)) {
                Conc.concat(conc, Single(b))
            } else conc
        }
}