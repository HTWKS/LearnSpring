package thoughtworks.main.api

import java.time.Duration
import java.time.LocalDateTime

data class GitHubPullRequest(val createdAt: LocalDateTime, val closedAt: LocalDateTime?)

fun List<GitHubPullRequest>.averageDuration(localDateTimeNow: LocalDateTime = LocalDateTime.now()): Duration {
    val count = count().toLong()
    return map { Duration.between(it.createdAt, it.closedAt ?: localDateTimeNow) }
        .fold(Duration.ZERO) { acc, current -> acc + current.dividedBy(count) }
}

fun Duration.toHumanReadableString(): String = toString()
    .substring(2)
    .replace("(\\d[HMS])(?!$)", "$1 ")
    .lowercase()