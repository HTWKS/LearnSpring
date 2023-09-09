package thoughtworks.unit.api

import org.junit.jupiter.api.Test
import thoughtworks.main.api.HealthController
import thoughtworks.main.api.Status

class HealthControllerTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyIsCalled() {
        val sut = HealthController()
        assert(Status("Healthy") == sut.healthy())
    }
}
