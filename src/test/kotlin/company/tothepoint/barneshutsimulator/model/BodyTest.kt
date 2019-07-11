package company.tothepoint.barneshutsimulator.model

import org.junit.jupiter.api.Test

class BodyTest {


    @Test
    fun `a body should be created by mass, x, y position, xspeed and yspeed`() {
        val body = Body(12.4f, 14.3, 8.7, 30.5, 12.9)
    }
}