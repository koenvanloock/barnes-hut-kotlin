package company.tothepoint.barneshutsimulator.simulator

import company.tothepoint.barneshutsimulator.model.*
import company.tothepoint.barneshutsimulator.model.BarnesHutConstants.SECTOR_PRECISION
import company.tothepoint.barneshutsimulator.model.BarnesHutConstants.eliminationThreshold
import company.tothepoint.barneshutsimulator.model.BarnesHutConstants.gee
import company.tothepoint.barneshutsimulator.utils.ArrayUtils.emptyArrayOfSize
import java.util.*
import kotlin.random.Random

class Simulator {

    val timeStatistics = TimeStatistics()

    var screen = Boundaries()

    var bodies: Array<Body> = emptyArray()

    var quad: Quad = Empty(screen.centerX(), screen.centerY(), Float.MAX_VALUE)

    var shouldRenderQuad = false

    var parallelismLevel = 1

    var totalBodies = 25000


    fun initialize() {
        init2Galaxies()
    }


    fun init2Galaxies() {
        val bodyArray = emptyArrayOfSize<Body>(totalBodies)
        val random = Random(213L)

        fun galaxy(from: Int, num: Int, maxradius: Float, cx: Float, cy: Float, sx: Float, sy: Float) {
            val totalM = 1.5f * num
            val blackHoleM = 1.0f * num
            val cubmaxradius = maxradius * maxradius * maxradius
            for (i in from until (from + num)) {
                val body = if (i == from) {
                    Body(blackHoleM, cx, cy, sx, sy)
                } else {
                    val angle = random.nextFloat() * 2 * Math.PI
                    val radius = 25 + maxradius * random.nextFloat()
                    val starx = (cx + radius * Math.sin(angle)).toFloat()
                    val stary = (cy + radius * Math.cos(angle)).toFloat()
                    val speed = Math.sqrt(((gee * blackHoleM / radius) + gee * totalM * radius * radius / cubmaxradius).toDouble())
                    val starspeedx = sx + (speed * Math.sin(angle + Math.PI / 2)).toFloat()
                    val starspeedy = sy + (speed * Math.cos(angle + Math.PI / 2)).toFloat()
                    val starmass = 1.0f + 1.0f * random.nextFloat()
                    Body(starmass, starx, stary, starspeedx, starspeedy)
                }
                bodyArray[i] = body
                screen = Boundaries()
                screen.minX = -2200.0f
                screen.minY = -1600.0f
                screen.maxX = 350.0f
                screen.maxY = 350.0f
            }
        }

        galaxy(0, bodyArray.size / 8, 300.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        galaxy(bodyArray.size / 8, bodyArray.size / 8 * 7, 350.0f, -1800.0f, -1200.0f, 0.0f, 0.0f)

        bodies = bodyArray
    }

    fun switchShowRenderQuad() {
        shouldRenderQuad = !shouldRenderQuad
    }

    fun updateTotalBodies(total: Int): Unit {
        this.totalBodies = total
        this.initialize()
    }

    fun step(bodies: Array<Body>): Pair<Array<Body>, Quad> {
        val boundaries = computeBoundaries(bodies)
        val sectorMatrix = computeSectorMatrix(bodies, boundaries)
        val quad = computeQuad(sectorMatrix)
        val filteredBodies = bodies  // eliminateOutliers(bodies, sectorMatrix, quad)
        val newBodies = updateBodies(filteredBodies, quad)

        return Pair(newBodies, quad)
    }

    fun computeBoundaries(bodies: Array<Body>): Boundaries = timeStatistics.timed("boundaries") {
        Arrays.stream(bodies).parallel().reduce(Boundaries(), this::updateBoundaries, this::mergeBoundaries)
    }

    fun updateBoundaries(boundaries: Boundaries, body: Body): Boundaries {
        boundaries.minX = Math.min(boundaries.minX, body.x)
        boundaries.minY = Math.min(boundaries.minY, body.y)
        boundaries.maxX = Math.max(boundaries.maxX, body.x)
        boundaries.maxY = Math.max(boundaries.maxY, body.y)
        return boundaries
    }

    fun mergeBoundaries(a: Boundaries, b: Boundaries): Boundaries {
        val bounds = Boundaries()
        bounds.minX = Math.min(a.minX, b.minX)
        bounds.minY = Math.min(a.minY, b.minY)
        bounds.maxX = Math.max(a.maxX, b.maxX)
        bounds.maxY = Math.max(a.maxY, b.maxY)
        return bounds
    }

    fun computeSectorMatrix(bodies: Array<Body>, boundaries: Boundaries): SectorMatrix = timeStatistics.timed("matrix") {
        Arrays.stream(bodies).reduce(SectorMatrix(boundaries, SECTOR_PRECISION), { a, b -> a add b }, { a, b -> a combine b })
    }

    fun computeQuad(sectorMatrix: SectorMatrix): Quad = timeStatistics.timed("quad") {
        sectorMatrix.toQuad(parallelismLevel)
    }

    fun eliminateOutliers(bodies: Array<Body>, sectorMatrix: SectorMatrix, quad: Quad): Array<Body> {
        fun isOutlier(b: Body): Boolean {
            val dx = quad.massX - b.x
            val dy = quad.massY - b.y
            val d = Math.sqrt((dx * dx + dy * dy).toDouble())
            // object is far away from the center of the mass
            return if (d > eliminationThreshold * sectorMatrix.boundaries.size()) {
                val nx = dx / d
                val ny = dy / d
                val relativeSpeed = b.xSpeed * nx + b.ySpeed * ny
                // object is moving away from the center of the mass
                if (relativeSpeed < 0) {
                    val escapeSpeed = Math.sqrt(2 * gee * quad.mass / d)
                    // object has the espace velocity
                    -relativeSpeed > 2 * escapeSpeed
                } else false
            } else false
        }

        /*
        val sectorPrecision = sectorMatrix.sectorPrecision
        val horizontalBorder = emptyArrayOfSize<Pair<Int, Int>>(sectorPrecision * 4)
        for (x in 0 until sectorPrecision) {
            for (y in arrayOf(0, sectorPrecision - 1)) {
                Pair(x, y)
            }
        }
        for (y in 1 until sectorPrecision - 1) {
            for (x in arrayOf(0, sectorPrecision - 1)) {
                Pair(x, y)
            }
        }
        val borderSectors = horizontalBorder verticalBorder

        // compute the set of outliers
        val outliers = Arrays.stream(borderSectors).map { pair -> outliersInSector(pair, y) }.reduce(_ combine _).result
            */
        // filter the bodies that are outliers
        return bodies
    }

    fun updateBodies(bodies: Array<Body>, quad: Quad): Array<Body> = timeStatistics.timed("update") {
        bodies.map { it.updated(quad) }.toTypedArray()
    }

    fun updateCores(numberOfCores: Int) {
        parallelismLevel = numberOfCores
        this.initialize()
    }
}