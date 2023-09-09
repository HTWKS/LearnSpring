package thoughtworks.main.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/health")
    fun healthy(): Status {
        return Status("Healthy")
    }
}

data class Status(
    val status: String
)