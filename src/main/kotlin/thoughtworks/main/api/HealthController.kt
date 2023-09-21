package thoughtworks.main.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class HealthController{

    @GetMapping("/health")
    fun healthy(): Data {
        return Data("Healthy")
    }
}

data class Data(
    val data: String
)