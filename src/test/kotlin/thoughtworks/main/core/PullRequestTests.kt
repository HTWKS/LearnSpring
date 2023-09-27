package thoughtworks.main.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.Duration
import java.time.LocalDateTime

class PullRequestTests {
    @ParameterizedTest
    @ValueSource(longs = [1, 2])
    fun `should calculate average of one pull request correctly`(hour: Long) {
        val createdAt = LocalDateTime.of(2023, 9, 10, 10, 0)
        val closedAt = createdAt.plusHours(hour)
        val expected = Duration.ofHours(hour)

        val gitHubPullRequests = listOf(GitHubPullRequest(createdAt, closedAt))
        val actual = gitHubPullRequests.averageDuration()

        assert(expected == actual) { "Expected: ${expected.seconds}, Actual: ${actual.seconds}" }
    }

    @Test
    fun `should calculate average of two pull requests correctly`() {
        val baseTime = LocalDateTime.of(2023, 9, 10, 10, 0)
        val plusOneHour = baseTime.plusHours(1)
        val plusTwoHour = baseTime.plusHours(2)
        val expected = Duration.ofMinutes(90)

        val gitHubPullRequests = listOf(
            GitHubPullRequest(baseTime, plusOneHour),
            GitHubPullRequest(baseTime, plusTwoHour)
        )
        val actual = gitHubPullRequests.averageDuration()

        assert(expected == actual) { "Expected: ${expected.seconds}, Actual: ${actual.seconds}" }
    }

    @Test
    fun `should calculate average by server local date time now when closedAt is null`() {
        val baseTime = LocalDateTime.of(2023, 9, 10, 10, 0)
        val plusOneHour = baseTime.plusHours(1)
        val expected = Duration.ofHours(1)
        val gitHubPullRequests = listOf(GitHubPullRequest(baseTime, null))

        val actual = gitHubPullRequests.averageDuration(localDateTimeNow = plusOneHour)
        assert(expected == actual) { "Expected: ${expected.seconds}, Actual: ${actual.seconds}" }
    }
}
