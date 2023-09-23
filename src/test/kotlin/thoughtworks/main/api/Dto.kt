package thoughtworks.main.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GraphQlQueryRequest(val query: String)
data class GraphQlQueryResponse<T>(@JsonProperty("data") val data: T)
data class ViewerDto(@JsonProperty("viewer") val viewer: LoginDto)
data class LoginDto(@JsonProperty("login") val login: String)
data class RepositoryDto(@JsonProperty("repository") val repository: PullRequestDto)
data class PullRequestDto(@JsonProperty("pullRequests") val pullRequests: NodesDto)
data class NodesDto(@JsonProperty("nodes") val nodes: List<PullRequestNodeDto>)
data class PullRequestNodeDto(
    @JsonProperty("createdAt") val createdAt: LocalDateTime,
    @JsonProperty("closedAt") val closedAt: LocalDateTime?
)

fun graphqlQueryFormat(queryBody: String) = "query{$queryBody}"
fun repositoryQueryFormat(owner: String, repositoryName: String) = graphqlQueryFormat("""repository(owner:"$owner",name:"$repositoryName"){pullRequests(first:100){nodes{createdAt closedAt}pageInfo{hasNextPage endCursor}}}""")

const val GET_LOGIN_NAME_QUERY = "viewer{login}"
