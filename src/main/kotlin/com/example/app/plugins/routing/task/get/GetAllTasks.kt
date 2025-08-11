package com.example.app.plugins.routing.task.get

import com.example.data.repository.TaskRepository
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getAllTasks(repository: TaskRepository) {

    get("/all") {
        call.respond(repository.allTasks())
    }
}