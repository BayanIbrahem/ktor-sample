package com.example.app.plugins.routing.task.get

import com.example.data.repository.TaskRepository
import com.example.model.Priority
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getTaskByPriority(repository: TaskRepository) {
    get("/byPriority/{priority?}") {
        val priority = call.parameters["priority"]?.let { priority ->
            Priority.entries.firstOrNull { it.name == priority }
        }
        if (priority == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val tasks = repository.tasksByPriority(priority)
        call.respond(tasks)
    }
}