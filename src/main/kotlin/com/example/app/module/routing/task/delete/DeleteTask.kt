package com.example.app.module.routing.task.delete

import com.example.data.repository.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete

fun Route.deleteTask() {
    delete("/{name?}") {
        val name = call.parameters["name"] ?: let {
            call.respond(HttpStatusCode.BadRequest, "Missing name")
            return@delete
        }
        val removed = TaskRepository.removeTask(name)
        call.respond(
            message = if (removed) HttpStatusCode.OK else HttpStatusCode.NotFound
        )
    }
}