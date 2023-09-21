package thoughtworks.unit.api

import org.junit.jupiter.api.Test
import thoughtworks.main.api.Data
import thoughtworks.main.api.HealthController

class HealthControllerTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyIsCalled() {
        val sut = HealthController()
        assert(Data("Healthy") == sut.healthy())
    }
}
