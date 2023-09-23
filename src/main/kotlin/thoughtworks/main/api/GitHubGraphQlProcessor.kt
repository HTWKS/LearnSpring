package thoughtworks.main.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import thoughtworks.main.graphql.OneHundredPullRequestBatchQuery
import thoughtworks.main.graphql.OneHundredPullRequestBatchQuery.Node
import thoughtworks.main.graphql.OneHundredPullRequestBatchQuery.PullRequests
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class GitHubGraphQlProcessor(@Autowired private val property: SecretProperty) {

    private val _apolloClient: ApolloClient = apolloClient(property)

    private var _fetch: suspend (owner: String, repositoryName: String, after: Optional<String?>) -> PullRequests? =
        { owner: String,
          repositoryName: String,
          after: Optional<String?> ->
            _apolloClient.query(OneHundredPullRequestBatchQuery(owner, repositoryName, after))
                .execute()
                .data?.repository?.pullRequests
        }


    suspend fun getAllPullRequests(owner: String, repositoryName: String): List<GitHubPullRequest> {
        var currentResponse = firstPage(owner, repositoryName)
        if (currentResponse.shouldTakeNodes()) {
            val pullRequests = currentResponse.toGitHubPullRequests().toMutableList()
            while (currentResponse!!.shouldContinueFetching()) {
                val nextPage = currentResponse.nextPage(owner, repositoryName)
                pullRequests.addAll(nextPage.toGitHubPullRequests())
                currentResponse = nextPage
            }
            return pullRequests
        }
        return emptyList()
    }

    fun setFetch(fetch: suspend (owner: String, repositoryName: String, after: Optional<String?>) -> PullRequests?) {
        _fetch = fetch
    }

    private suspend fun firstPage(owner: String, repositoryName: String): PullRequests? =
        _fetch(owner, repositoryName, Optional.Absent)

    private suspend fun PullRequests.nextPage(owner: String, repositoryName: String): PullRequests? =
        _fetch(owner, repositoryName, Optional.present(this.pageInfo.endCursor!!))

    private fun PullRequests?.toGitHubPullRequests(): List<GitHubPullRequest> {
        if (this.shouldTakeNodes()) return this!!.nodes!!.toGitHubPullRequests()
        return emptyList()
    }

    private fun List<Node?>.toGitHubPullRequests(): List<GitHubPullRequest> = this.map {
        GitHubPullRequest(
            it?.createdAt.toString().toLocalDateTime(),
            it?.closedAt?.toString()?.toLocalDateTime()
        )
    }

    private fun PullRequests.shouldContinueFetching() =
        this.pageInfo.hasNextPage && (this.pageInfo.endCursor != null)

    private fun PullRequests?.shouldTakeNodes() = this?.nodes != null
}

const val GITHUB_GRAPHQL_ENDPOINT = "https://api.github.com/graphql"

fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)

fun apolloClient(property: SecretProperty) = ApolloClient.Builder()
    .serverUrl(GITHUB_GRAPHQL_ENDPOINT)
    .addHttpHeader("Authorization", "Bearer ${property.githubToken}")
    .build()