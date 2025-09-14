package dev.bayan_ibrahim.logger.core

import dev.bayan_ibrahim.logger.model.LogEntry
import dev.bayan_ibrahim.logger.model.LogEntryAction
import kotlinx.datetime.Instant

/**
 * this is logger for everything,
 * actions,
 */
interface Logger {
    /**
     * store a log entry
     */
    suspend fun log(entry: LogEntry): Long

    /**
     * get a specific log
     */
    suspend fun getLog(id: Long): LogEntry?

    /**
     * get logs of user according to data order by [LogEntry.loggedAt] desc
     * @param userID id of the user
     * @param userName name of the user (null will ignore this filed)
     * @param userEmail email of the user (null will ignore this filed)
     * @param userPhoneNumber phone number of the user (null will ignore this field)
     * @param userUsername username of the user (null will ignore this field)
     * @param actions actions set filter of the logs (null or empty list will ignore this field)
     * @param resources resources types of the logs (null or empty list will ignore this field)
     * @param resourcesIdentifiers identifiers for every resource type:
     * - if a type has no identifiers value or empty set then all entries match
     * - resources types mentioned in [resourcesIdentifiers] and not mentioned in [resources] will be ignored
     * @param before max timestamp of the log entry (null will ignore this value)
     * @param after min timestamp of the log entry (null will ignore this value)
     */
    suspend fun getLogsOfUser(
        userID: Long,
        userName: String? = null,
        userEmail: String? = null,
        userPhoneNumber: String? = null,
        userUsername: String? = null,
        actions: Set<LogEntryAction>? = null,
        resources: Set<String>? = null,
        resourcesIdentifiers: Map<String, Set<Long>>? = null,
        before: Instant? = null,
        after: Instant? = null,
    ): List<LogEntry>
}