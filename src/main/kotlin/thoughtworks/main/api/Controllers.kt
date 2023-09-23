package thoughtworks.main.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

data class Data(
    val data: String
)

@RestController
class Controllers(@Autowired private val gitHubGraphQlProcessor: GitHubGraphQlProcessor) {

    @GetMapping("/health")
    fun healthy(): Data {
        return Data("Healthy")
    }

    @GetMapping("/github/pullrequestmeantimetoresolve/{owner}/{repositoryName}")
    suspend fun githubPullRequestMeantimeToResolve(
        @PathVariable owner: String,
        @PathVariable repositoryName: String
    ): Data {
        return Data(
            gitHubGraphQlProcessor.getAllPullRequests(owner, repositoryName).averagePullRequestDuration().toHumanReadableString()
        )
    }
}
