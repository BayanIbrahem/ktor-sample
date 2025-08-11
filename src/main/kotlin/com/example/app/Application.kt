package com.example.app

import com.example.app.plugins.database.configureDatabase
import com.example.app.plugins.routing.configureRouting
import com.example.app.plugins.serialization.configureSerialization
import com.example.app.plugins.status_pages.configureStatusPages
import com.example.app.plugins.websocket.configureWebSocket
import com.example.data.repository.FakeTaskRepository
import com.example.data.repository.TaskRepository
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val repository: TaskRepository = FakeTaskRepository
    // order matters here, routing should be the last
    configureWebSocket()
    configureSerialization()
    configureStatusPages()
    configureDatabase()

    configureRouting(repository)
}
