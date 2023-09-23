package thoughtworks.main.component

import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import thoughtworks.main.api.*
import thoughtworks.main.graphql.OneHundredPullRequestBatchQuery

@SpringBootTest
class GithubGraphqlTests(@Autowired val property: SecretProperty) {
    private val webClient = WebClient.builder().baseUrl(GITHUB_GRAPHQL_ENDPOINT).build()
    private val owner = "devlooped"
    private val repositoryName = "moq"
    private val currentPrCount = 604

    @Test
    fun `should exchange view login successfully`() = runBlocking {
        val result = getGraphQlQueryResponse<ViewerDto>(GraphQlQueryRequest(graphqlQueryFormat(GET_LOGIN_NAME_QUERY)))
        assert(result.data.viewer.login == "HTWKS") { result }
    }

    @Test
    fun `should query repository by owner and repository name successfully`() = runBlocking {
        val result = getGraphQlQueryResponse<RepositoryDto>(
            GraphQlQueryRequest(
                repositoryQueryFormat(owner, repositoryName)
            )
        )

        assert(result.data.repository.pullRequests.nodes.isNotEmpty()) { result }
    }

    @Test
    fun `should query one hundred pull request batch successfully`() = runBlocking {
        val client = apolloClient(property)

        val response = client
            .query(OneHundredPullRequestBatchQuery(owner = owner, name = repositoryName))
            .execute()

        assert(response.data != null)
        assert(response.data!!.repository != null)
        assert(response.data!!.repository!!.pullRequests.nodes != null)
        assert(response.data!!.repository!!.pullRequests.nodes!!.isNotEmpty())
        assert(response.data!!.repository!!.pullRequests.nodes!!.first()!!.createdAt is String)
        assert(response.data!!.repository!!.pullRequests.pageInfo.endCursor != null)
        assert(response.data!!.repository!!.pullRequests.pageInfo.endCursor!!.isNotEmpty())

        val nextCursor = response.data!!.repository!!.pullRequests.pageInfo.endCursor!!
        val nextResponse = client
            .query(OneHundredPullRequestBatchQuery(owner, repositoryName, Optional.present(nextCursor)))
            .execute()

        assert(nextResponse.data != null)
        assert(nextResponse.data!!.repository != null)
        assert(nextResponse.data!!.repository!!.pullRequests.nodes != null)
        assert(nextResponse.data!!.repository!!.pullRequests.nodes!!.isNotEmpty())
        assert(nextResponse.data!!.repository!!.pullRequests.pageInfo.endCursor != null)
        assert(nextResponse.data!!.repository!!.pullRequests.pageInfo.endCursor!!.isNotEmpty())
    }

    @Test
    fun `should test component successfully`() = runBlocking {
        val sut = GitHubGraphQlProcessor(property)
        val actual = sut.getAllPullRequests(owner, repositoryName)
        assert(actual.count() >= currentPrCount) { actual.count() }
    }

    // The "inline" and "reified" keyword is necessary for Jackson to deserialize json response to kotlin class
    private suspend inline fun <reified T> getGraphQlQueryResponse(requestBody: GraphQlQueryRequest) = webClient
        .post()
        .headers { it.setBearerAuth(property.githubToken) }
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(requestBody))
        .retrieve()
        .awaitBody<GraphQlQueryResponse<T>>()
}

