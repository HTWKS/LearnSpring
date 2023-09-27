package thoughtworks.main.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import thoughtworks.main.core.averageDuration
import thoughtworks.main.core.toHumanReadableString
import thoughtworks.main.infra.GithubCachedQuery

data class Data(
    val data: String
)

@RestController
class Controllers(@Autowired private val githubCachedQuery: GithubCachedQuery) {

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
            githubCachedQuery
                .getAllPullRequests(owner, repositoryName)
                .await()
                .averageDuration()
                .toHumanReadableString()
        )
    }

    @GetMapping("/github/pullrequestmeantimetoresolve/v2/{owner}/{repositoryName}")
    suspend fun githubPullRequestMeantimeToResolveV2(
        @PathVariable owner: String,
        @PathVariable repositoryName: String
    ): Data {
        return Data(
            githubCachedQuery
                .getAllPullRequests(owner, repositoryName)
                .await()
                .averageDuration()
                .toString()
        )
    }
}
