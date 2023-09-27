package thoughtworks.main.infra

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import thoughtworks.main.core.GitHubPullRequest

@Component
class GithubCachedQuery(@Autowired var githubQuery: GithubQuery) {

    @Cacheable("allPullRequests")
    fun getAllPullRequests(owner: String, repositoryName: String): Deferred<List<GitHubPullRequest>> =
        CoroutineScope(Dispatchers.IO).async { githubQuery.getAllPullRequests(owner, repositoryName) }
}