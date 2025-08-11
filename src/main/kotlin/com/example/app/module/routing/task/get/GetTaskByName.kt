package com.example.app.module.routing.task.get

import com.example.data.repository.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getTaskByName() {
    get("/byName/{name?}") {
        val name = call.parameters["name"]
        if (name == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val task = TaskRepository.taskByName(name)
        if (task == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(task)
        }
    }
}