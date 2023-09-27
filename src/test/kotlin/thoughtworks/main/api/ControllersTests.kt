package thoughtworks.main.api

import org.junit.jupiter.api.Test
import thoughtworks.main.infra.GitHubGraphQlProcessor
import thoughtworks.main.infra.GithubCachedQuery
import thoughtworks.main.infra.SecretProperty

class ControllersTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyIsCalled() {
        val sut = Controllers(GithubCachedQuery(GitHubGraphQlProcessor(SecretProperty("token"))))
        assert(Data("Healthy") == sut.healthy())
    }
}
