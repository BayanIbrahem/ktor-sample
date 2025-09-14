package dev.bayan_ibrahim.logger.model

import kotlinx.datetime.Instant

/**
 * single log entry,
 * @param logID id of the log
 * @param loggedAt time of the log
 * @param userID user id of the log (at the log time)
 * @param userName name of the user (at the log time)
 * @param userEmail email of the user (at the log time)
 * @param userUsername username of the user (at the log time
 * @param userPhoneNumber
 * we are using all [userID], [userName], [userEmail], [userUsername], [userPhoneNumber] to identify the user since the logs are historical
 * data and user object may be deleted
 * @param res what is the resource type that this log is applied to like 'task', 'privilege', 'user'
 * @param action action type of the log
 * @param rawDataBefore raw data before applying the log
 * @param rawDataAfter rawData after applying the log
 */
data class LogEntry(
    val logID: Long,
    val loggedAt: Instant,
    val userID: Long,
    val userName: String,
    val userEmail: String?,
    val userUsername: String?,
    val userPhoneNumber: String?,
    val res: String,
    val resIDs: Set<Long>,
    val action: LogEntryAction,
    val rawDataBefore: String,
    val rawDataAfter: String,
)

