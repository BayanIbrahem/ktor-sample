package com.example.app.plugins.routing.task.post

import com.example.data.repository.TaskRepository
import com.example.model.Task
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.SerializationException

fun Route.addTask(repository: TaskRepository) {
    post {
        try {
            val task = call.receive<Task>()
            repository.addTask(task)
            call.respond(HttpStatusCode.Created)
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: SerializationException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

}