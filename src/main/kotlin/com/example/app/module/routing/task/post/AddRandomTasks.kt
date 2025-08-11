package com.example.app.module.routing.task.post

import com.example.data.repository.TaskRepository
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.addRandomTasks() {
    post("/random_tasks") {
        TaskRepository.random()
    }
}