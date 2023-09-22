package thoughtworks.main.api

import org.junit.jupiter.api.Test

class HealthControllerTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyIsCalled() {
        val sut = HealthController()
        assert(Data("Healthy") == sut.healthy())
    }
}
