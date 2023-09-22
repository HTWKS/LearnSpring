package thoughtworks.main.api

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

fun graphqlQueryFormat(queryBody : String) = "query{$queryBody}"

private const val GET_LOGIN_NAME_QUERY = "viewer{login}"

@SpringBootTest
class GithubGraphQlTests(@Autowired val property: SecretProperty) {
    private val webClient = WebClient.builder().baseUrl("https://api.github.com/graphql").build()

    @Test
    fun `should exchange view login successfully`() = runBlocking {
        val result = getGraphQlQueryResponse<ViewerDto>(GraphQlQueryRequest(graphqlQueryFormat(GET_LOGIN_NAME_QUERY)))
        assert(result.data.viewer.login == "HTWKS") { result }
    }

    @Test
    fun `should query repository by owner and repository name successfully`() = runBlocking {
        val owner = "devlooped"
        val repositoryName = "moq"
        val result = getGraphQlQueryResponse<RepositoryDto>(GraphQlQueryRequest("""query {
  repository(owner: "$owner", name: "$repositoryName") {
    pullRequests(first: 100) {
      nodes {
        createdAt
        closedAt
      }
      pageInfo {
        hasNextPage
        endCursor
      }
    }
  }
}"""))
        assert(result.data.repository.pullRequests.nodes.isNotEmpty()) { result }
    }

    @Test
    fun `should use graphql client successfully`() = runBlocking {

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