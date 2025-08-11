package com.example.app.module.routing.task.websocket

import com.example.data.repository.TaskRepository
import com.example.model.Task
import io.ktor.server.routing.Route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.delay
import java.util.Collections
import kotlin.time.Duration.Companion.seconds

fun Route.tasksStream() {
    val sessions = Collections.synchronizedList<WebSocketServerSession>(
        // should be a mutable list here
        ArrayList()
    )
    webSocket("/stream/outbound") {
        sendAllTasks()
    }
    webSocket("/stream/inbound") {
        sessions.add(this)
        sendAllTasks()
        while (true) {
            val newTask = receiveDeserialized<Task>()
            TaskRepository.addTask(newTask)
            sessions.forEach { session ->
                session.sendSerialized(newTask)
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.sendAllTasks() {
    TaskRepository.allTasks().forEach { task ->
        sendSerialized(task)
        delay(3.seconds)
    }
}
