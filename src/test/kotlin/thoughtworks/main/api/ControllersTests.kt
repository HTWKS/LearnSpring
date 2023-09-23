package thoughtworks.main.api

import org.junit.jupiter.api.Test

class ControllersTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyIsCalled() {
        val sut = Controllers(GitHubGraphQlProcessor(SecretProperty("token")))
        assert(Data("Healthy") == sut.healthy())
    }
}
