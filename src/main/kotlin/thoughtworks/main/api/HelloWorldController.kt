package thoughtworks.main.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {
    @GetMapping("/health")
    fun healthy(): String {
        return "Healthy"
    }
}