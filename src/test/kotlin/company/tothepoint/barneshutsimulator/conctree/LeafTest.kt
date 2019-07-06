package company.tothepoint.barneshutsimulator.conctree

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LeafTest {

    @Test
    fun `empty can be constructed and has level and size 0`() {
        val empty = Empty
        assertThat(empty).isNotNull()
        assertThat(empty.level).isEqualTo(0)
        assertThat(empty.size).isEqualTo(0)
    }

    @Test
    fun `can create a Single leaf instance with one elem`() {
        val single = Single(5)
        assertThat(single).isNotNull()
        assertThat(single.level).isEqualTo(0)
        assertThat(single.size).isEqualTo(1)
    }

    @Test
    fun `can create a Chunk leaf instance with an array of elems`() {
        val chunk = Chunk(arrayOf(1,2,3))
        assertThat(chunk).isNotNull()
        assertThat(chunk.level).isEqualTo(0)
        assertThat(chunk.size).isEqualTo(3)
    }
}