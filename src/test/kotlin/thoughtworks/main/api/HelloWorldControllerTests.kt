package thoughtworks.main.api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HelloWorldControllerTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyIsCalled() {
        val sut = HelloWorldController()
        assert("Healthy" == sut.healthy())
    }
}
