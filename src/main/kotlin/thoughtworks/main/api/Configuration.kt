package thoughtworks.main.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:secret.properties")
class SecretProperty(
    @Value("\${github.token}")
    val githubToken: String
)