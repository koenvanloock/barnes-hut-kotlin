package company.tothepoint.barneshutsimulator.conctree

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ConcTest {

    @Test
    fun `a Fork with two leaves has level 1, size is the combined size of the leaves`() {
        val fork  = Fork(Single(5), Chunk(arrayOf(9,0,2)))
        assertThat(fork.size).isEqualTo(4)
        assertThat(fork.level).isEqualTo(1)
    }

    @Test
    fun `an Append with two leaves has level 1, size is the combined size of the leaves`() {
        val append  = Append(Single(5), Chunk(arrayOf(9,0,2)))
        assertThat(append.size).isEqualTo(4)
        assertThat(append.level).isEqualTo(1)
    }

    @Test
    fun `concatTop two Concs of equal level returns a fork of both`() {
        val result = Conc.concatTop(Single(5), Single(8))
        assertThat(result.level).isEqualTo(1)
        assertThat(result).isInstanceOf(Fork::class.java)
        assertThat(result.left()).isEqualTo(Single(5))
        assertThat(result.right()).isEqualTo(Single(8))
    }

    @Test
    fun `concatTop two Concs where first has level 1 higher returns a fork of both`() {
        val result = Conc.concatTop(Fork(Single(99), Single(5)), Single(8))
        assertThat(result.level).isEqualTo(2)
        assertThat(result).isInstanceOf(Fork::class.java)
        assertThat(result.left()).isEqualTo(Fork(Single(99), Single(5)))
        assertThat(result.right()).isEqualTo(Single(8))
    }

    @Test
    fun `concatTopting two Concs where first has level 1 lower returns a fork of both`() {
        val result = Conc.concatTop(Single(8), Fork(Single(99), Single(5)))
        assertThat(result.level).isEqualTo(2)
        assertThat(result).isInstanceOf(Fork::class.java)
        assertThat(result.left()).isEqualTo(Single(8))
        assertThat(result.right()).isEqualTo(Fork(Single(99), Single(5)))
    }

    /**
     *              /  \
     *       8  +       5      =>
     *            /  \               /    \      /    \
     *           99  1              99    1      5    8
     * */
    @Test
    fun `concatTop two Concs where first has level 2 higher returns a fork with the second one appendeded to the rights right, old right is rights left`() {
        val result = Conc.concatTop(Fork(Fork(Single(99), Single(1)), Single(5)), Single(8))
        assertThat(result.level).isEqualTo(2)
        assertThat(result).isInstanceOf(Fork::class.java)
        assertThat(result.left()).isEqualTo(Fork(Single(99), Single(1)))
        assertThat(result.right()).isEqualTo(Fork(Single(5), Single(8)))
    }

    /**
     *              /  \
     *       3  + 2              =>
     *               /   \               /    \      /    \
     *              99   1               3    2     99    1
     * */
    @Test
    fun `concatTop two Concs where first has level 2 lower returns a fork with the second one appendeded to the lefts left, old left is lefts left`() {
        val result = Conc.concatTop(Single(3), Fork(Single(2), Fork(Single(99), Single(1))))
        assertThat(result.level).isEqualTo(2)
        assertThat(result).isInstanceOf(Fork::class.java)
        assertThat(result.left()).isEqualTo(Fork(Single(3), Single(2)))
        assertThat(result.right()).isEqualTo(Fork(Single(99), Single(1)))
    }


    /**
     *
     *    /          5                   /          \
     *  /   \                   =>     /  \        /  \
     *  1  /   \                      1   2       3    5
     *     2   3
     */
    @Test
    fun `a rebalance happens the 3 and 5 are forked, unbalancing of over 2 levels detected and reforking to restore balance again`() {
        val result = Conc.concatTop(Fork(Single(1), Fork(Single(2), Single(3))), Single(5))
        assertThat(result.left()).isEqualTo(Fork(Single(1), Single(2)))
        assertThat(result.right()).isEqualTo(Fork(Single(3), Single(5)))
    }

    /**
     *  1     +                                  /          \
     *              /   \               =>     /  \        /  \
     *            /   \  5                    1   2       3    5
     *           2   3
     */
    @Test
    fun `a rebalance happens the 2 and 2 are forked, unbalancing of over 2 levels detected and reforking to restore balance again`() {
        val result = Conc.concatTop(Fork(Single(1), Fork(Single(2), Single(3))), Single(5))
        assertThat(result.left()).isEqualTo(Fork(Single(1), Single(2)))
        assertThat(result.right()).isEqualTo(Fork(Single(3), Single(5)))
    }

    /**
     *  1     +                                  /          \
     *              /   \               =>     /  \        /  \
     *            /   \                      1   2       3    5
     *           2
     *               / \ / \
     *               3 4 5 6
     */
    @Test
    fun `a rebalance happens the 2 and 2 are forked, unbalancing of level 3 levels detected`() {
        val result = Conc.concatTop(Fork(Fork(Single(1), Fork(Single(2), Single(3))), Single(4)), Fork(Single(5), Single(6)))
        assertThat(result.left()).isEqualTo(Fork(Single(1), Fork(Single(2), Single(3))))
        assertThat(result.right()).isEqualTo(Fork(Single(4), Fork(Single(5), Single(6))))
    }

    @Test
    fun `concatTop with right Empty results in the other one (neutral element)`() {
        assertThat(Conc.concatTop(Single(4), Empty as Conc<Int>)).isEqualTo(Single(4))
    }

    @Test
    fun `concatTop with left Empty results in the other one (neutral element)`() {
        assertThat(Conc.concatTop(Empty as Conc<Int>, Single(4))).isEqualTo(Single(4))
    }
}