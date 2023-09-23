package thoughtworks.e2e

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.runBlocking
import net.bytebuddy.utility.RandomString
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

class ApiTests {
    private val baseUrl = System.getenv("WEB_HOST") ?: "http://localhost:8080"
    private val webClient = WebClient.builder().baseUrl(baseUrl).build()

    @Test
    fun `should return healthy when healthy end point is called`() = runBlocking {
        val result = webClient.get().uri("health").retrieve().awaitBody<StatusResponse>()
        assert(result == StatusResponse("Healthy"))
    }

    @Test
    fun `should return success when healthy end point is called`() = runBlocking {
        val result = webClient.get()
            .uri("/github/pullrequestmeantimetoresolve/${RandomString.make()}/${RandomString.make()}")
            .retrieve()
            .awaitBody<StatusResponse>()
        assert(result == StatusResponse("0s"))
    }
}

data class StatusResponse(@JsonProperty("data") val status: String)