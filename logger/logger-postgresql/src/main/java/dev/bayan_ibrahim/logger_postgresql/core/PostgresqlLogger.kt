package dev.bayan_ibrahim.logger_postgresql.core

import dev.bayan_ibrahim.logger.core.Logger
import dev.bayan_ibrahim.logger.data_converter.DataConverter
import dev.bayan_ibrahim.logger.data_converter.NonDataConverter
import dev.bayan_ibrahim.logger.model.LogEntry
import dev.bayan_ibrahim.logger.model.LogEntryAction
import dev.bayan_ibrahim.logger_postgresql.db.LogsEntity
import dev.bayan_ibrahim.logger_postgresql.db.LogsResIDsEntity
import dev.bayan_ibrahim.logger_postgresql.db.LogsResIDsTable
import dev.bayan_ibrahim.logger_postgresql.db.LogsTable
import dev.bayan_ibrahim.logger_postgresql.db.RawDataEntity
import dev.bayan_ibrahim.logger_postgresql.db.RawDataTable
import dev.bayan_ibrahim.logger_postgresql.db.suspendTransaction
import dev.bayan_ibrahim.logger_postgresql.db.toModel
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andIfNotNull

class PostgresqlLogger(
    private val dataConverter: DataConverter = NonDataConverter,
) : Logger {
    fun createScheme() {
        SchemaUtils.create(LogsTable, LogsResIDsTable, RawDataTable)
    }

    override suspend fun log(entry: LogEntry): Long {
        return suspendTransaction {
            val log = LogsEntity.new {
                loggedAt = entry.loggedAt.toEpochMilliseconds()
                userID = entry.userID
                userName = entry.userName
                userEmail = entry.userEmail
                userPhoneNumber = entry.userPhoneNumber
                userUsername = entry.userUsername
                res = entry.res
                action = entry.action
            }
            entry.resIDs.forEach { resID ->
                LogsResIDsEntity.new {
                    this.log = log
                    this.resID = resID
                }
            }
            RawDataEntity.new {
                this.log = log
                this.beforeData = dataConverter.convert(entry.rawDataBefore)
                this.afterData = dataConverter.convert(entry.rawDataAfter)
            }
            log.id.value
        }
    }

    override suspend fun getLog(id: Long): LogEntry? = suspendTransaction {
        LogsEntity.findById(id)?.toModel(dataConverter)
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
    ): List<LogEntry> = suspendTransaction {
        LogsEntity.find {
            // Base condition: filter by userID always
            (LogsTable.userID eq userID)
                .andIfNotNull(userName?.let { LogsTable.userName eq userName })
                .andIfNotNull(userEmail?.let { LogsTable.userEmail eq userEmail })
                .andIfNotNull(userPhoneNumber?.let { LogsTable.userPhoneNumber eq userPhoneNumber })
                .andIfNotNull(userUsername?.let { LogsTable.userUsername eq userUsername })
                .andIfNotNull(actions?.takeIf { it.isNotEmpty() }?.let { LogsTable.action inList actions })
                .andIfNotNull(resources?.takeIf { it.isNotEmpty() }?.let { LogsTable.res inList resources })
                .andIfNotNull(before?.let { LogsTable.loggedAt lessEq before.toEpochMilliseconds() })
                .andIfNotNull(after?.let { LogsTable.loggedAt greaterEq it.toEpochMilliseconds() })
        }.orderBy(
            Pair(
                first = LogsTable.loggedAt,
                second = SortOrder.DESC_NULLS_LAST
            )
        ).mapNotNull {
            it.toModel(dataConverter).takeIf { log ->
                resourcesIdentifiers?.get(log.res)?.takeIf { it.isNotEmpty() }?.let { requiredResIDs ->
                    log.resIDs.any { resID -> resID in requiredResIDs }
                } ?: true
            }
        }
    }
}