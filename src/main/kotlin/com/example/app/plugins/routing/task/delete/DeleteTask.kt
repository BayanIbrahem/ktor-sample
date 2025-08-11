package com.example.app.plugins.routing.task.delete

import com.example.data.repository.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete

fun Route.deleteTask(repository: TaskRepository) {
    delete("/{name?}") {
        val name = call.parameters["name"] ?: let {
            call.respond(HttpStatusCode.BadRequest, "Missing name")
            return@delete
        }
        val removed = repository.removeTask(name)
        call.respond(
            message = if (removed) HttpStatusCode.OK else HttpStatusCode.NotFound
        )
    }
}