package thoughtworks.main.api

import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import thoughtworks.main.graphql.OneHundredPullRequestBatchQuery
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GithubRepositoryTests {
    private val owner = "owner"
    private val repositoryName = "repositoryName"
    @Test
    fun `Should get empty pull requests successfully`() = runBlocking {
        val repository = GitHubGraphQlProcessor(SecretProperty(""))
        repository.setFetch { _, _, _ ->
            OneHundredPullRequestBatchQuery.PullRequests(
                nodes = null,
                pageInfo = OneHundredPullRequestBatchQuery.PageInfo(hasNextPage = false, endCursor = null)
            )
        }
        val allPullRequests = repository.getAllPullRequests(owner, repositoryName)
        assert(allPullRequests.isEmpty())
    }

    @Test
    fun `Should get one pull request successfully`() = runBlocking {
        val sut = GitHubGraphQlProcessor(SecretProperty(""))
        val givenOwner = "givenOwner"
        val givenRepository = "givenRepository"
        sut.setFetch { owner, repositoryName, _ ->
            if (owner == givenOwner && repositoryName == givenRepository)
                return@setFetch OneHundredPullRequestBatchQuery.PullRequests(
                    nodes = listOf(
                        OneHundredPullRequestBatchQuery.Node(
                            createdAt = "2012-03-12T09:50:00Z",
                            closedAt = null
                        )
                    ),
                    pageInfo = OneHundredPullRequestBatchQuery.PageInfo(hasNextPage = false, endCursor = "endCursor")
                )
            return@setFetch null
        }
        val actual = sut.getAllPullRequests(givenOwner, givenRepository)
        assert(actual.count() == 1)
        assert(actual.first().closedAt == null)
        assert(actual.first().createdAt == LocalDateTime.of(LocalDate.of(2012, 3, 12), LocalTime.of(9, 50)))
    }

    @Test
    fun `Should get 2 paginated pull requests successfully`() = runBlocking {
        val sut = GitHubGraphQlProcessor(SecretProperty(""))
        val afterCursor = Optional.Present("afterCursor")
        sut.setFetch { _, _, after ->
            if (after == afterCursor)
                return@setFetch OneHundredPullRequestBatchQuery.PullRequests(
                    nodes = listOf(
                        OneHundredPullRequestBatchQuery.Node(
                            createdAt = "2012-03-12T09:50:00Z",
                            closedAt = "2012-04-12T09:50:00Z"
                        )
                    ),
                    pageInfo = OneHundredPullRequestBatchQuery.PageInfo(hasNextPage = false, endCursor = null)
                )
            return@setFetch OneHundredPullRequestBatchQuery.PullRequests(
                nodes = listOf(
                    OneHundredPullRequestBatchQuery.Node(
                        createdAt = "2012-03-12T09:50:00Z",
                        closedAt = null
                    )
                ),
                pageInfo = OneHundredPullRequestBatchQuery.PageInfo(hasNextPage = true, endCursor = afterCursor.value)
            )
        }
        val actual = sut.getAllPullRequests(owner, repositoryName)
        assert(actual.count() == 2) { "Expected: 2, Actual: ${actual.count()}" }
    }

    @Test
    fun `Should get multiple paginated pull requests successfully`() = runBlocking {
        val sut = GitHubGraphQlProcessor(SecretProperty(""))
        val afterCursor = Optional.Present("afterCursor")
        val afterCursor1 = Optional.Present("afterCursor1")
        sut.setFetch { _, _, after ->
            if (after == afterCursor1)
                return@setFetch OneHundredPullRequestBatchQuery.PullRequests(
                    nodes = listOf(
                        OneHundredPullRequestBatchQuery.Node(
                            createdAt = "2012-03-12T09:50:00Z",
                            closedAt = "2012-04-12T09:50:00Z"
                        )
                    ),
                    pageInfo = OneHundredPullRequestBatchQuery.PageInfo(hasNextPage = false, endCursor = afterCursor1.value)
                )
            if (after == afterCursor)
                return@setFetch OneHundredPullRequestBatchQuery.PullRequests(
                    nodes = listOf(
                        OneHundredPullRequestBatchQuery.Node(
                            createdAt = "2012-03-12T09:50:00Z",
                            closedAt = "2012-04-12T09:50:00Z"
                        )
                    ),
                    pageInfo = OneHundredPullRequestBatchQuery.PageInfo(hasNextPage = true, endCursor = afterCursor1.value)
                )
            return@setFetch OneHundredPullRequestBatchQuery.PullRequests(
                nodes = listOf(
                    OneHundredPullRequestBatchQuery.Node(
                        createdAt = "2012-03-12T09:50:00Z",
                        closedAt = null
                    )
                ),
                pageInfo = OneHundredPullRequestBatchQuery.PageInfo(hasNextPage = true, endCursor = afterCursor.value)
            )
        }
        val actual = sut.getAllPullRequests(owner, repositoryName)
        assert(actual.count() == 3) { "Expected: 3, Actual: ${actual.count()}" }
    }
}