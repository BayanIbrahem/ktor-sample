package com.example.app.module.routing.task.post

import com.example.data.repository.TaskRepository
import com.example.model.TaskGenerator
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.addRandomTask() {
    post("/random_task") {
        val task = TaskGenerator.generate()
        TaskRepository.addTask(task)
    }
}