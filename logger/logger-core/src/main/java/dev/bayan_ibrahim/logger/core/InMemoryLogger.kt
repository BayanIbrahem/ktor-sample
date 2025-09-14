package dev.bayan_ibrahim.logger.core

import dev.bayan_ibrahim.logger.model.LogEntry
import dev.bayan_ibrahim.logger.model.LogEntryAction
import kotlinx.datetime.Instant

class InMemoryLogger : Logger {
    private val logsEntries: MutableList<LogEntry> = mutableListOf()

    override suspend fun log(entry: LogEntry): Long {
        val logID = logsEntries.size.toLong().inc()
        val newEntry = entry.copy(logID = logID)
        logsEntries.add(newEntry)
        return logID
    }

    override suspend fun getLog(id: Long): LogEntry? {
        return logsEntries.firstOrNull { it.logID == id }
    }

    override suspend fun getLogsOfUser(
        userID: Long,
        userName: String?,
        userEmail: String?,
        userPhoneNumber: String?,
        userUsername: String?,
        actions: Set<LogEntryAction>?,
        resources: Set<String>?,
        resourcesIdentifiers: Map<String, Set<Long>>?,
        before: Instant?,
        after: Instant?,
    ): List<LogEntry> = logsEntries.filter { entry ->
        (entry.userID == userID) &&
                (userName == null || entry.userName == userName) &&
                (userEmail == null || entry.userEmail == userEmail) &&
                (userPhoneNumber == null || entry.userPhoneNumber == userPhoneNumber) &&
                (userUsername == null || entry.userUsername == userUsername) &&
                (actions.isNullOrEmpty() || entry.action in actions) &&
                (resources.isNullOrEmpty() || entry.res in resources) &&
                (resourcesIdentifiers.isNullOrEmpty() || resources.isNullOrEmpty() || resources.all {
                    val ids = resourcesIdentifiers[it]
                    ids.isNullOrEmpty() || entry.resIDs.any { entryResIds ->
                        entryResIds in ids
                    }
                }) &&
                (before == null || entry.loggedAt <= before) &&
                (after == null || entry.loggedAt >= after)

    }
}