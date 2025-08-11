package com.example.app.module.routing.task.delete

import com.example.data.repository.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete

fun Route.clearAllTasks() {
    delete("/clear_all") {
        TaskRepository.clear()
        call.respond(HttpStatusCode.NoContent)
    }
}