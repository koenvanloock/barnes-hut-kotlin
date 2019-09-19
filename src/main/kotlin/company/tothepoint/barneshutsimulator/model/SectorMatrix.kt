package company.tothepoint.barneshutsimulator.model

import company.tothepoint.barneshutsimulator.conctree.ConcBuffer
import company.tothepoint.barneshutsimulator.parallel.ParallelUtils.parallel
import company.tothepoint.barneshutsimulator.parallel.Quadruple
import company.tothepoint.barneshutsimulator.utils.ArrayUtils.emptyArrayOfSize

class SectorMatrix(val boundaries: Boundaries, val sectorPrecision: Int) {
    val sectorSize = boundaries.size() / sectorPrecision
    val matrix = emptyArrayOfSize<ConcBuffer>(sectorPrecision * sectorPrecision)

    init{
        for (i in 0 until matrix.size) matrix[i] = ConcBuffer()
    }

    infix fun add(b: Body): SectorMatrix {
        val rowCoord = ((b.x - boundaries.minX) / sectorSize).toInt()
        val colCoord = ((b.y - boundaries.minY) / sectorSize).toInt()
        val x = if (rowCoord == sectorPrecision) sectorPrecision - 1 else rowCoord
        val y = if (colCoord == sectorPrecision) sectorPrecision - 1 else colCoord
        matrix[x + y * sectorPrecision] add b
        return this
    }

    operator fun invoke(x: Int, y: Int) = matrix[y * sectorPrecision + x]


    // breaks when size = 0
    infix fun combine(that: SectorMatrix): SectorMatrix {
            val m = SectorMatrix(boundaries, sectorPrecision)
            for (i in 0 until matrix.size) m.matrix[i] = this.matrix[i] combine that.matrix[i]
            return m
    }

    fun toQuad(parallelism: Int): Quad {
        val BALANCING_FACTOR = 4

        fun quad(x: Int, y: Int, span: Int, achievedParallelism: Int): Quad {
            if (span == 1) {
                val sectorSize = boundaries.size() / sectorPrecision
                val centerX = boundaries.minX + x * sectorSize + sectorSize / 2
                val centerY = boundaries.minY + y * sectorSize + sectorSize / 2
                var emptyQuad: Quad = Empty(centerX, centerY, sectorSize)
                val sectorBodies = this (x, y)
                return sectorBodies.foldLeft(emptyQuad){ a, b -> a.insert(b)}
            } else {
                val nspan = span / 2
                val nAchievedParallelism = achievedParallelism * 4
                val (nw, ne, sw, se) =
                        if (parallelism > 1 && achievedParallelism < parallelism * BALANCING_FACTOR)
                            parallel(
                                    {quad(x, y, nspan, nAchievedParallelism)},
                                    {quad(x + nspan, y, nspan, nAchievedParallelism)},
                                    {quad(x, y + nspan, nspan, nAchievedParallelism)},
                                    {quad(x + nspan, y + nspan, nspan, nAchievedParallelism)}
                        ) else {
                            Quadruple(quad(x, y, nspan, nAchievedParallelism),
                            quad(x + nspan, y, nspan, nAchievedParallelism),
                            quad(x, y + nspan, nspan, nAchievedParallelism),
                            quad(x + nspan, y + nspan, nspan, nAchievedParallelism))
                        }
                return Fork(nw, ne, sw, se)
            }
        }

        return quad(0, 0, sectorPrecision, 1)
    }

    override fun toString() = "SectorMatrix(#bodies: ${matrix.map { x -> x.size() }.sum()}})"
}