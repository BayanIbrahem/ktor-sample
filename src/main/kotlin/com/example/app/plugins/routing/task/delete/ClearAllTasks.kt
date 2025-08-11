package com.example.app.plugins.routing.task.delete

import com.example.data.repository.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete

fun Route.clearAllTasks(repository: TaskRepository) {
    delete("/clear_all") {
        repository.clear()
        call.respond(HttpStatusCode.NoContent)
    }
}