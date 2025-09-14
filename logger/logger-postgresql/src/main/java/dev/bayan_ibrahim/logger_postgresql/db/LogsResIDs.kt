package dev.bayan_ibrahim.logger_postgresql.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

// New table for resource identifiers
internal data object LogsResIDsTable : LongIdTable(
    name = "logs_resource_identifiers",
    columnName = "id"
) {
    val log = reference(
        name = "log_id",
        foreign = LogsTable,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
    )
    val resID = long("resource_id")
}

// DAO for the resource identifiers
internal class LogsResIDsEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<LogsResIDsEntity>(LogsResIDsTable)

    var log by LogsEntity referencedOn LogsResIDsTable.log
    var resID by LogsResIDsTable.resID
}
