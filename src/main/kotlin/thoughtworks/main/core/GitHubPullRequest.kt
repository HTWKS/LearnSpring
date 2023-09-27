package thoughtworks.main.core

import java.time.Duration
import java.time.LocalDateTime

data class GitHubPullRequest(val createdAt: LocalDateTime, val closedAt: LocalDateTime?) {
    fun duration(timeNow : LocalDateTime): Duration = when(closedAt == null) {
        true -> Duration.between(createdAt, timeNow)
        false -> Duration.between(createdAt, closedAt)
    }
}

fun List<GitHubPullRequest>.averageDuration(localDateTimeNow: LocalDateTime = LocalDateTime.now()): Duration {
    val count = count().toLong()
    return map { it.duration(localDateTimeNow) }
        .fold(Duration.ZERO) { acc, current -> acc + current.dividedBy(count) }
}

fun Duration.toHumanReadableString(): String = toString()
    .substring(2)
    .replace("(\\d[HMS])(?!$)", "$1 ")
    .lowercase()