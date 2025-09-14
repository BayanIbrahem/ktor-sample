package dev.bayan_ibrahim.logger_postgresql.db

import dev.bayan_ibrahim.logger.model.LogEntryAction
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

internal data object LogsTable : LongIdTable(
    name = "logs",
    columnName = "id"
) {
    // TODO, specify table columns here
    val loggedAt = long("logged_at_timestamp")
    val userID = long("user_id")
    val userName = varchar("user_name", 50)
    val userEmail = varchar("user_email", 50).nullable()
    val userPhoneNumber = varchar("user_phone_number", 50).nullable()
    val userUsername = varchar("user_username", 50).nullable()
    val res = varchar("resource", 50)
    val action = enumerationByName<LogEntryAction>("action", 20)
}

internal class LogsEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<LogsEntity>(LogsTable)

    var loggedAt by LogsTable.loggedAt
    var userID by LogsTable.userID
    var userName by LogsTable.userName
    var userEmail by LogsTable.userEmail
    var userPhoneNumber by LogsTable.userPhoneNumber
    var userUsername by LogsTable.userUsername
    var res by LogsTable.res
    var action by LogsTable.action
    val resIDs by LogsResIDsEntity referrersOn LogsResIDsTable.log
    val data by RawDataEntity backReferencedOn RawDataTable.log
}

