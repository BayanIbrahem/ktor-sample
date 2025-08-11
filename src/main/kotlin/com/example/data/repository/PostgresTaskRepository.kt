package com.example.data.repository

import com.example.data.db.TaskDAO
import com.example.data.db.TaskTable
import com.example.data.db.daoToModel
import com.example.data.db.suspendTransaction
import com.example.model.Priority
import com.example.model.Task
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere

class PostgresTaskRepository : TaskRepository {
    override suspend fun clear() {
        TaskTable.deleteAll()
    }

    override suspend fun random() {
    }

    override suspend fun allTasks(): List<Task> = suspendTransaction {
        TaskDAO.all().map(::daoToModel)
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> = suspendTransaction {
        TaskDAO.find {
            TaskTable.priority eq priority.toString()
        }.map(::daoToModel)
    }

    override suspend fun taskByName(name: String): Task? = suspendTransaction {
        TaskDAO
            .find {
                TaskTable.name eq name
            }.limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addTask(task: Task): Unit = suspendTransaction {
        TaskDAO.new {
            name = task.name
            description = task.description
            priority = task.priority.toString()
        }
    }

    override suspend fun updateTask(task: Task) {
        val existingTask = TaskDAO.find { TaskTable.name eq task.name }.firstOrNull() ?: return
        // Update mutable fields (excluding the primary key 'name')
        existingTask.description = task.description
        existingTask.priority = task.priority.toString()
    }

    override suspend fun removeTask(name: String): Boolean = suspendTransaction {
        val rowsDeleted = TaskTable.deleteWhere {
            TaskTable.name eq name
        }
        rowsDeleted == 1
    }
}