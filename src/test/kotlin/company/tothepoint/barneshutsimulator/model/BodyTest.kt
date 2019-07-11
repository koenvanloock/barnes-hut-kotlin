package company.tothepoint.barneshutsimulator.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BodyTest {

    @Test
    fun `Body updated should do nothing for Empty quad trees`() {
        val b1 = Body (123f, 18f, 26f, 0f, 0f)
        val body = b1.updated(Empty(50f, 60f, 5f))

        assertThat(body.xSpeed).isEqualTo(0f)
        assertThat(body.ySpeed).isEqualTo(0f)
    }

    @Test
    fun `Body updated should take bodies in a Leaf into account`() {
        val b1 = Body (123f, 18f, 26f, 0f, 0f)
        val b2 = Body (524.5f, 24.5f, 25.5f, 0f, 0f)
        val b3 = Body (245f, 22.4f, 41f, 0f, 0f)

        val quad = Leaf(15f, 30f, 20f, listOf(b2, b3))

        val body = b1.updated(quad)

        assertThat(body.xSpeed).isEqualTo(12.587037f)
        assertThat(body.ySpeed).isEqualTo(0.015557117f)
    }

    // test cases for sector matrix
  /*
    test("'SectorMatrix.+=' should add a body at (25,47) to the correct bucket of a sector matrix of size 96")
    {
        val body = new Body (5, 25, 47, 0.1f, 0.1f)
        val boundaries = new Boundaries ()
        boundaries.minX = 1
        boundaries.minY = 1
        boundaries.maxX = 97
        boundaries.maxY = 97
        val sm = new SectorMatrix (boundaries, SECTOR_PRECISION)
        sm += body
        val res = sm(2, 3).size == 1 && sm(2, 3).find(_ == body).isDefined
        assert(res, s"Body not found in the right sector")
    }

}*/
}