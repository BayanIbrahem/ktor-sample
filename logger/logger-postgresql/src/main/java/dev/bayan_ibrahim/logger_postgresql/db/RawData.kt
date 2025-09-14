package dev.bayan_ibrahim.logger_postgresql.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

// New table for resource identifiers
internal data object RawDataTable : LongIdTable(
    name = "logs_raw_data",
    columnName = "id"
) {
    val log = reference(
        name = "log_id",
        foreign = LogsTable,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
    )
    val beforeData = text("before_data")
    val afterData = text("after_data")
}

// DAO for the resource identifiers
internal class RawDataEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RawDataEntity>(RawDataTable)

    var log by LogsEntity referencedOn RawDataTable.log
    var beforeData by RawDataTable.beforeData
    var afterData by RawDataTable.afterData
}
