package com.example.app.plugins.routing.task.post

import com.example.data.repository.TaskRepository
import com.example.model.TaskGenerator
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.addRandomTask(repository: TaskRepository) {
    post("/random_task") {
        val task = TaskGenerator.generate()
        repository.addTask(task)
    }
}