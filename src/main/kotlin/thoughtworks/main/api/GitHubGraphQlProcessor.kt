package thoughtworks.main.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import thoughtworks.main.graphql.OneHundredPullRequestBatchQuery
import thoughtworks.main.graphql.OneHundredPullRequestBatchQuery.PullRequests
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

typealias FetchFromGithub = suspend (owner: String, repositoryName: String, after: Optional<String?>) -> PullRequests?

@Component
class GitHubGraphQlProcessor(@Autowired property: SecretProperty) {


    private val _apolloClient = apolloClient(property)

    private var _fetch: FetchFromGithub = ::defaultFetchFromGithub


    suspend fun getAllPullRequests(owner: String, repositoryName: String): List<GitHubPullRequest> {
        var response = firstPage(owner, repositoryName)
        val allPullRequests = response.toGitHubPullRequests().toMutableList()
        while (response.shouldContinueFetching()) {
            response = response!!.nextPage(owner, repositoryName)
            allPullRequests.addAll(response.toGitHubPullRequests())
        }
        return allPullRequests
    }

    fun setFetch(fetch: FetchFromGithub) {
        _fetch = fetch
    }

    private suspend fun defaultFetchFromGithub(owner: String,
                                               repositoryName: String,
                                               after: Optional<String?>): PullRequests? =
        _apolloClient.query(OneHundredPullRequestBatchQuery(owner, repositoryName, after))
            .execute()
            .data?.repository?.pullRequests

    private suspend fun firstPage(owner: String, repositoryName: String): PullRequests? =
        _fetch(owner, repositoryName, Optional.Absent)

    private suspend fun PullRequests.nextPage(owner: String, repositoryName: String): PullRequests? =
        _fetch(owner, repositoryName, Optional.present(this.pageInfo.endCursor!!))

    private fun PullRequests?.toGitHubPullRequests(): List<GitHubPullRequest> {
        if (this?.nodes == null) {
            return emptyList()
        }
        return this.nodes.map {
            GitHubPullRequest(
                it?.createdAt.toString().toLocalDateTime(),
                it?.closedAt?.toString()?.toLocalDateTime()
            )
        }
    }

    private fun PullRequests?.shouldContinueFetching() =
        this != null && this.pageInfo.hasNextPage && this.pageInfo.endCursor != null
}


const val GITHUB_GRAPHQL_ENDPOINT = "https://api.github.com/graphql"

fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)

fun apolloClient(property: SecretProperty) = ApolloClient.Builder()
    .serverUrl(GITHUB_GRAPHQL_ENDPOINT)
    .addHttpHeader("Authorization", "Bearer ${property.githubToken}")
    .build()