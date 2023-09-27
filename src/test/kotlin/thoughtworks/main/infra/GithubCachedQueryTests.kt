package thoughtworks.main.infra

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import thoughtworks.main.core.GitHubPullRequest
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

@SpringBootTest
class CacheTest(@Autowired private val sut: Cache) {
    @Test
    fun `should cache`() = runBlocking {
        val actual = measureTimeMillis {
            sut.getCacheable().await()
            sut.getCacheable().await()
        }
        assert(actual < 200) { actual }
    }
}


@Component
class Cache {
    @Cacheable("getCacheable")
    fun getCacheable(): Deferred<Unit> {
        return CoroutineScope(Dispatchers.Default).async {
            delay(100)
        }
    }
}

@SpringBootTest
class GithubCachedQueryTests(@Autowired private val sut: GithubCachedQuery) {

    val owner = UUID.randomUUID().toString()
    val name = UUID.randomUUID().toString()

    companion object {
        const val NET_WORK_CALL_TIME_MILLIS = 50L
    }

    @Test
    fun `Should save time for subsequence queries successfully`() = runBlocking {
        val timeDelayMillis = 100L
        val repeatTimes = 3
        sut.githubQuery = GithubSuspendedQueryMock(timeDelayMillis)
        val actual = measureTimeMillis {
            repeat(repeatTimes) {
                sut.getAllPullRequests(owner, name).await()
            }
        }
        assert(actual < repeatTimes * timeDelayMillis) { actual }
    }

    @Test
    fun `Should return cached value instead of new value`() = runBlocking {
        val expected = listOf(GitHubPullRequest(LocalDateTime.now(), LocalDateTime.now()))
        val mock = GithubWithDataQueryMock(expected)
        sut.githubQuery = mock

        sut.getAllPullRequests(owner, name).await()
        mock.gitHubPullRequests = listOf(GitHubPullRequest(LocalDateTime.MAX, LocalDateTime.MIN))
        val actual = sut.getAllPullRequests(owner, name).await()

        assert(actual == expected) { actual }
    }

    @Test
    fun `Should cache by all parameters`() = runBlocking {
        fun randomGithubPullRequests() = run { listOf(GitHubPullRequest(LocalDateTime.now(), LocalDateTime.now())) }
        val mock = GithubWithDataQueryMock(randomGithubPullRequests())
        sut.githubQuery = mock
        val actual = sut.getAllPullRequests("owner", "repository").await()
        mock.gitHubPullRequests = randomGithubPullRequests()
        val actual1 = sut.getAllPullRequests("owner", "").await()
        mock.gitHubPullRequests = randomGithubPullRequests()
        val actual2 = sut.getAllPullRequests("", "repository").await()

        assert(actual != actual1)
        assert(actual1 != actual2)
        assert(actual != actual2)
    }

    @Test
    fun `should run in multi threaded environment`() = runBlocking {
        val hashSet = HashSet<String>()
        simulateRaceCondition(10) { hashSet.add(Thread.currentThread().id.toString()) }
        assert(hashSet.count() > 5)
        { hashSet.count() }
    }

    @ParameterizedTest
    @ValueSource(ints = [2, 4, 6, 8, 10])
    fun `should simulate race condition successfully`(concurrency: Int) = runBlocking {
        val setter = NonThreadSafeSetter()
        simulateRaceCondition(concurrency) { setter.set() }
        assert(setter.isSet)
    }

    @Test
    fun `should call factory function only once in race condition`() = runBlocking {
        val mock = GithubSuspendedQueryMockAllowToBeCalledOnce()
        sut.githubQuery = mock
        simulateRaceCondition(10) { sut.getAllPullRequests(owner, name).await() }
        assert(mock.callCount() == 1) { mock.callCount() }
    }

    class NonThreadSafeSetter {
        var isSet = false
        suspend fun set() {
            if (isSet) {
                throw Exception("Should not be set twice")
            }
            delay(NET_WORK_CALL_TIME_MILLIS)
            isSet = true
        }
    }

    class GithubWithDataQueryMock(var gitHubPullRequests: List<GitHubPullRequest>) : GithubQuery {
        override suspend fun getAllPullRequests(owner: String, repositoryName: String): List<GitHubPullRequest> {
            return gitHubPullRequests
        }
    }

    class GithubSuspendedQueryMock(private val timeDelayMillis: Long) : GithubQuery {
        override suspend fun getAllPullRequests(owner: String, repositoryName: String): List<GitHubPullRequest> {
            delay(timeDelayMillis)
            return emptyList()
        }
    }

    class GithubSuspendedQueryMockAllowToBeCalledOnce : GithubQuery {
        private val callCount = AtomicInteger()
        override suspend fun getAllPullRequests(owner: String, repositoryName: String): List<GitHubPullRequest> {
            delay(NET_WORK_CALL_TIME_MILLIS)
            callCount.incrementAndGet()
            return emptyList()
        }
        fun callCount() = callCount.get()
    }

    private suspend fun simulateRaceCondition(concurrency: Int, action: suspend () -> Unit) {
        val semaphore = Semaphore(0)
        val multiThreadedContext = Executors.newFixedThreadPool(concurrency).asCoroutineDispatcher()
        coroutineScope {
            val allJobs = List(concurrency) {
                launch(multiThreadedContext) {
                    semaphore.acquire()
                    action()
                }
            }
            semaphore.release(concurrency)
            allJobs.joinAll()
        }
    }
}
