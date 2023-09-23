package thoughtworks.main.api

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class UtilTests {
    @Test
    fun `Should convert to local datetime successfully for pull request created at`() {
        val timeFetchFromGitHub = "2012-03-12T09:50:00Z"
        val expected = LocalDateTime.of(LocalDate.of(2012, 3, 12), LocalTime.of(9, 50))
        val actual = timeFetchFromGitHub.toLocalDateTime()
        assert(expected == actual)
    }

    @ParameterizedTest
    @CsvSource(value = ["2,2s", "600,10m", "3600,1h", "5400,1h30m", "172800,48h"])
    fun `Should convert time duration to human readable form`(durationInSecond: Long, expected: String) {
        val testData = Duration.ofSeconds(durationInSecond)
        val actual = testData.toHumanReadableString()
        assert(expected == actual) { "Expected: $expected, Actual: $actual" }
    }
}