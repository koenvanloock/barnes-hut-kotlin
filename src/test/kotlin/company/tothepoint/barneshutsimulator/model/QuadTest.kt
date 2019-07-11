package company.tothepoint.barneshutsimulator.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QuadTest {


    @Test
    fun `inserting a body in an Empty node is a Leaf with that body`() {
        val body = Body(12.4f, 14.3f, 8.7f, 30.5f, 12.9f)

        val emtpy = Empty(5.0f, 10.0f, 8.0f)
        assertThat(emtpy.insert(body)).isEqualTo(Leaf(5.0f, 10.0f, 8.0f, listOf(body)))
    }

    @Test
    fun `inserting a body in a Leaf with one body greater that minimumSize results in a Fork with two bodies`() {
        val body = Body(12.4f, 8.3f, 8.7f, 30.5f, 12.9f)
        val bodyTwo = Body(11.4f, 2.3f, 11.7f, 30.5f, 12.9f)

        val leaf = Leaf(5.0f, 10.0f, 8.0f, listOf(bodyTwo))
        assertThat(leaf.insert(body)).isEqualTo(Fork(nw=Empty(centerX=3.0f, centerY=8.0f, size=4.0f), ne=Leaf(centerX=7.0f, centerY=8.0f, size=4.0f, bodies=listOf(body)), sw=Leaf(centerX=3.0f, centerY=12.0f, size=4.0f, bodies=listOf(bodyTwo)), se=Empty(centerX=7.0f, centerY=12.0f, size=4.0f)))
    }

    @Test
    fun `empty center of mass should be the center of the cell`() {
        val quad = Empty(51f, 46.3f, 5f)
        assertThat(quad.massX).isEqualTo(51f)
        assertThat(quad.massY).isEqualTo(46.3f)
    }

    @Test
    fun `empty mass should be 0`() {
        val quad = Empty(51f, 46.3f, 5f)
        assertThat(quad.mass).isEqualTo(0f)
    }

    @Test
    fun `Empty total should be 0`() {
        val quad = Empty(51f, 46.3f, 5f)
        assertThat(quad.total).isEqualTo(0)
    }

    @Test
    fun `Leaf with 1 body`() {
        val b = Body(123f, 18f, 26f, 0f, 0f)
        val quad = Leaf(17.5f, 27.5f, 5f, listOf(b))

        assertThat(quad.mass).isEqualTo(123f)
        assertThat(quad.massX).isEqualTo(18f)
        assertThat(quad.massY).isEqualTo(26f)
        assertThat(quad.total).isEqualTo(1)
    }

    @Test
    fun `Fork with 4 empty quads still has values`() {
        val nw = Empty(17.5f, 27.5f, 5f)
        val ne = Empty(22.5f, 27.5f, 5f)
        val sw = Empty(17.5f, 32.5f, 5f)
        val se = Empty(22.5f, 32.5f, 5f)
        val quad = Fork(nw, ne, sw, se)

        assertThat(quad.centerX).isEqualTo(20f)
        assertThat(quad.centerY).isEqualTo(30f)
        assertThat(quad.mass).isEqualTo(0.0f)
        assertThat(quad.massX).isEqualTo(20f)
        assertThat(quad.massY).isEqualTo(30f)
        assertThat(quad.total).isEqualTo(0)
    }

    @Test
    fun `Fork with 3 empty quadrants and 1 leaf (nw)`() {
        val b = Body (123f, 18f, 26f, 0f, 0f)
        val nw = Leaf(17.5f, 27.5f, 5f, listOf(b))
        val ne = Empty(22.5f, 27.5f, 5f)
        val sw = Empty(17.5f, 32.5f, 5f)
        val se = Empty(22.5f, 32.5f, 5f)
        val quad = Fork(nw, ne, sw, se)

        assertThat(quad.centerX).isEqualTo(20f)
        assertThat(quad.centerY).isEqualTo(30f)
        assertThat(quad.mass).isEqualTo(123f)
        assertThat(quad.massX).isEqualTo(18f)
        assertThat(quad.massY).isEqualTo(26f)
        assertThat(quad.total).isEqualTo(1)
    }
}