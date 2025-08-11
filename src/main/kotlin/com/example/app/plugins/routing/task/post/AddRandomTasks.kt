package com.example.app.plugins.routing.task.post

import com.example.data.repository.TaskRepository
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.addRandomTasks(repository: TaskRepository) {
    post("/random_tasks") {
        repository.random()
    }
}