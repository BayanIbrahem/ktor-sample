package dev.bayan_ibrahim.logger_postgresql.db

import dev.bayan_ibrahim.logger.data_converter.DataConverter
import dev.bayan_ibrahim.logger.model.LogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

internal fun LogsEntity.toModel(
    dataConverter: DataConverter,
) = LogEntry(
    logID = id.value,
    loggedAt = Instant.fromEpochMilliseconds(loggedAt),
    userID = userID,
    userName = userName,
    userEmail = userEmail,
    userUsername = userUsername,
    userPhoneNumber = userPhoneNumber,
    res = res,
    resIDs = resIDs.map { it.resID }.toSet(),
    action = action,
    rawDataBefore = dataConverter.unconvert(data.beforeData),
    rawDataAfter = dataConverter.unconvert(data.afterData),
)