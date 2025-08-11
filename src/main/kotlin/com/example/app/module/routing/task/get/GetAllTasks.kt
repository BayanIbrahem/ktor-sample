package com.example.app.module.routing.task.get

import com.example.data.repository.TaskRepository
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getAllTasks() {
    get("/all") {
        call.respond(TaskRepository.allTasks())
    }
}