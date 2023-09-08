package thoughtworks.e2e

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import java.net.URI

class ApiTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyEndPointIsCalled() {
        val restTemplate = RestTemplate()
        val uri = URI("http://localhost:8080/health")
        val result = restTemplate.getForEntity(uri, StatusResponse::class.java)
        assert(result.body == StatusResponse("Healthy"))
    }
}

data class StatusResponse(@JsonProperty("status") val status: String)