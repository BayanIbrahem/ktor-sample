package com.example.app.plugins.routing.task.update

import com.example.data.repository.TaskRepository
import com.example.model.Task
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import kotlinx.serialization.SerializationException

fun Route.updateTask(repository: TaskRepository) {
    put {
        try {
            val task = call.receive<Task>()
            repository.updateTask(task)
            call.respond(HttpStatusCode.Created)
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: SerializationException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

}
