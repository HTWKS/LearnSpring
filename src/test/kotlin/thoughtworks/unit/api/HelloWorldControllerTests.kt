package thoughtworks.unit.api

import org.junit.jupiter.api.Test
import thoughtworks.main.api.HelloWorldController
import thoughtworks.main.api.Status

class HelloWorldControllerTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyIsCalled() {
        val sut = HelloWorldController()
        assert(Status("Healthy") == sut.healthy())
    }
}
