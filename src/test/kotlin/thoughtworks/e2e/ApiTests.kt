package thoughtworks.e2e

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import java.net.URI

class ApiTests {
    @Test
    fun shouldReturnHealthy_WhenHealthyEndPointIsCalled() {
        val restTemplate = RestTemplate()
        val uriString = System.getenv("WEB_HOST") ?: "http://localhost:8080"
        val uri = URI("$uriString/health")
        val result = restTemplate.getForEntity(uri, StatusResponse::class.java)
        assert(result.body == StatusResponse("Healthy"))
    }
}

data class StatusResponse(@JsonProperty("data") val status: String)