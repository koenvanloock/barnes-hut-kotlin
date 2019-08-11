package company.tothepoint.barneshutsimulator.model

import company.tothepoint.barneshutsimulator.model.BarnesHutConstants.delta
import company.tothepoint.barneshutsimulator.model.BarnesHutConstants.gee
import company.tothepoint.barneshutsimulator.model.BarnesHutConstants.theta

data class Body (val mass: Float, val x: Float, val y: Float, val xSpeed: Float, val ySpeed: Float) {
    fun updated(quad: Quad): Body {
            var netforcex = 0.0f
            var netforcey = 0.0f

            fun addForce(thatMass: Float, thatMassX: Float, thatMassY: Float): Unit  {
            val dist = distance(thatMassX, thatMassY, x, y)
            /* If the distance is smaller than 1f, we enter the realm of close
             * body interactions. Since we do not model them in this simplistic
             * implementation, bodies at extreme proximities get a huge acceleration,
             * and are catapulted from each other's gravitational pull at extreme
             * velocities (something like this:
             * http://en.wikipedia.org/wiki/Interplanetary_spaceflight#Gravitational_slingshot).
             * To decrease the effect of this gravitational slingshot, as a very
             * simple approximation, we ignore gravity at extreme proximities.
             */
            if (dist > 1f) {
                val dforce = force(mass, thatMass, dist)
                val xn = (thatMassX - x) / dist
                val yn = (thatMassY - y) / dist
                val dforcex = dforce * xn
                val dforcey = dforce * yn
                netforcex += dforcex
                netforcey += dforcey
            }
        }

            fun traverse(quad: Quad): Unit =  when(quad) {
            is Empty -> {}
            is Leaf -> quad.bodies.forEach{b -> addForce(b.mass, b.x, b.y)}
            is Fork ->
            if (quad.size / distance(quad.massX, quad.massY, x, y) < theta) {
                addForce(quad.mass, quad.massX, quad.massY)
            } else {
                traverse(quad.nw)
                traverse(quad.ne)
                traverse(quad.sw)
                traverse(quad.se)
            }
        }

            traverse(quad)

            val nx = x + xSpeed * delta
            val ny = y + ySpeed * delta
            val nxspeed = xSpeed + netforcex / mass * delta
            val nyspeed = ySpeed + netforcey / mass * delta

            return Body(mass, nx, ny, nxspeed, nyspeed)
        }

    fun distance(x0: Float, y0: Float, x1: Float, y1: Float): Float =
            Math.sqrt(((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)).toDouble()).toFloat()

    fun force(m1: Float, m2: Float, dist: Float): Float = gee * m1 * m2 / (dist * dist)
}