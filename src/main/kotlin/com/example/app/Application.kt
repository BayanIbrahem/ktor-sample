package com.example.app

import com.example.app.module.routing.configureRouting
import com.example.app.module.serialization.configureSerialization
import com.example.app.module.status_pages.configureStatusPages
import com.example.app.module.websocket.configureWebSocket
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    // order matters here, routing should be the last
    configureWebSocket()
    configureSerialization()
    configureStatusPages()

    configureRouting()
}
