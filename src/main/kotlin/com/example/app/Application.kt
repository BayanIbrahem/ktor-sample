package com.example.app

import com.example.app.plugins.database.configureDatabase
import com.example.app.plugins.routing.configureRouting
import com.example.app.plugins.serialization.configureSerialization
import com.example.app.plugins.status_pages.configureStatusPages
import com.example.app.plugins.websocket.configureWebSocket
import com.example.data.repository.FakeTaskRepository
import com.example.data.repository.TaskRepository
import io.ktor.server.application.Application
import io.ktor.server.config.property
import io.ktor.server.netty.EngineMain
import io.netty.util.internal.SocketUtils.connect

/**
 * main class where the app starts, we have two options here:
 * - configure with file 'application.yaml' so we use engine main **Current option**
 * - configure with code then we call embedded server
 */
fun main(args: Array<String>) {
    EngineMain.main(args)

}