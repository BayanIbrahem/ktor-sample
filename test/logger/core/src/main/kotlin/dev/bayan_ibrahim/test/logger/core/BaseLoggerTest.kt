package dev.bayan_ibrahim.test.logger.core

import dev.bayan_ibrahim.logger.core.Logger
import dev.bayan_ibrahim.logger.model.LogEntry
import dev.bayan_ibrahim.logger.model.LogEntryAction
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertContains
import kotlin.time.Duration.Companion.seconds


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseLoggerTest<L : Logger> {
    abstract val logger: L
    val testTime get() = Clock.System.now()

    open fun loggerSetup() {}
    open fun loggerAfter() {}

    @Before
    fun setup() {
        loggerSetup()
    }

    @After
    fun after() {
        loggerAfter()
    }

    // Helper function to create test logs
    private fun createLogEntry(
        id: Long = 0L,
        userID: Long = 1L,
        userName: String = "Alice",
        resource: String = "task",
        identifiers: Set<Long> = setOf(101),
        action: LogEntryAction = LogEntryAction.CREATE,
        time: Instant = testTime,
    ) = LogEntry(
        logID = id,
        loggedAt = time,
        userID = userID,
        userName = userName,
        userEmail = "alice@example.com",
        userUsername = "alice",
        userPhoneNumber = "1234567890",
        res = resource,
        resIDs = identifiers,
        action = action,
        rawDataBefore = "{}",
        rawDataAfter = "{}"
    )

    @Test
    fun `log should assign sequential IDs`() = runTest {
        val entry = createLogEntry()
        val id1 = logger.log(entry)
        val id2 = logger.log(entry.copy(userName = "Bob"))

        assertEquals(1L, id1)
        assertEquals(2L, id2)
    }

    @Test
    fun `getLog should retrieve stored entry`() = runTest {
        val entry = createLogEntry()
        val id = logger.log(entry)
        val retrieved = logger.getLog(id)
        assertEqualsIgnoringNanos(
            entry.copy(logID = id),
            retrieved,
        )
    }

    @Test
    fun `getLog should return null for invalid ID`() = runTest {
        assertNull(logger.getLog(999))
    }

    @Test
    fun `getLogsOfUser should filter by userID`() = runTest {
        logger.log(createLogEntry(userID = 1))
        logger.log(createLogEntry(userID = 2))

        val logs = logger.getLogsOfUser(userID = 1)
        assertEquals(1, logs.size)
        assertEquals(1L, logs[0].userID)
    }

    @Test
    fun `getLogsOfUser should filter by userName`() = runTest {
        logger.log(createLogEntry(userName = "Alice"))
        logger.log(createLogEntry(userName = "Bob"))

        val logs = logger.getLogsOfUser(userID = 1, userName = "Alice")
        assertEquals(1, logs.size)
        assertEquals("Alice", logs[0].userName)
    }

    @Test
    fun `getLogsOfUser should filter by actions`() = runTest {
        logger.log(createLogEntry(action = LogEntryAction.CREATE))
        logger.log(createLogEntry(action = LogEntryAction.UPDATE))

        val logs = logger.getLogsOfUser(
            userID = 1,
            actions = setOf(LogEntryAction.UPDATE)
        )
        assertEquals(1, logs.size)
        assertEquals(LogEntryAction.UPDATE, logs[0].action)
    }

    @Test
    fun `getLogsOfUser should filter by resources`() = runTest {
        logger.log(createLogEntry(resource = "task"))
        logger.log(createLogEntry(resource = "user"))

        val logs = logger.getLogsOfUser(
            userID = 1,
            resources = setOf("user")
        )
        assertEquals(1, logs.size)
        assertEquals("user", logs[0].res)
    }

    @Test
    fun `getLogsOfUser should filter by resource identifiers`() = runTest {
        logger.log(createLogEntry(resource = "task", identifiers = setOf(101)))
        logger.log(createLogEntry(resource = "task", identifiers = setOf(102)))

        val logs = logger.getLogsOfUser(
            userID = 1,
            resources = setOf("task"),
            resourcesIdentifiers = mapOf("task" to setOf(101))
        )
        assertEquals(1, logs.size)
        assertContains(logs[0].resIDs, 101)
    }

    @Test
    fun `getLogsOfUser should ignore resource identifiers for unspecified resources`() = runTest {
        logger.log(createLogEntry(resource = "task", identifiers = setOf(101)))

        val logs = logger.getLogsOfUser(
            userID = 1,
            resources = setOf("task"), // Different resource
            resourcesIdentifiers = mapOf("user" to setOf(102))
        )
        assertEquals(1, logs.size) // Identifier filter ignored
    }

    @Test
    fun `getLogsOfUser should handle empty resource identifiers`() = runTest {
        logger.log(createLogEntry(resource = "task", identifiers = setOf(101)))

        val logs = logger.getLogsOfUser(
            userID = 1,
            resources = setOf("task"),
            resourcesIdentifiers = mapOf("task" to emptySet())
        )
        assertEquals(1, logs.size)
    }

    @Test
    fun `getLogsOfUser should filter by time range`() = runTest {
        val baseTime = Instant.parse("2025-01-01T00:00:00Z")
        logger.log(createLogEntry(time = baseTime - 10.seconds)) // Too early
        logger.log(createLogEntry(time = baseTime))
        logger.log(createLogEntry(time = baseTime + 10.seconds)) // Too late

        val logs = logger.getLogsOfUser(
            userID = 1,
            after = baseTime - 5.seconds,
            before = baseTime + 5.seconds
        )
        assertEquals(1, logs.size)
        assertEquals(baseTime, logs[0].loggedAt)
    }

    fun assertEqualsIgnoringNanos(expected: LogEntry, actual: LogEntry?) {
        // we are overriding logged at to round into milliseconds, milliseconds fractions are not required
        assertEquals(
            expected.copy(loggedAt = expected.loggedAt.roundToMillis()),
            actual?.copy(loggedAt = actual.loggedAt.roundToMillis())
        )
    }

    private fun Instant.roundToMillis(): Instant {
        return Instant.fromEpochMilliseconds(this.toEpochMilliseconds())
    }
}
