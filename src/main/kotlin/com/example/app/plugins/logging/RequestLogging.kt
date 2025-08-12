package com.example.app.plugins.logging

import io.ktor.server.application.Application
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.util.logging.KtorSimpleLogger
import org.slf4j.event.Level

/**
 * custom logger
 */
private val LOGGER = KtorSimpleLogger("com.example.app.module.RequestTracePlugin")

private val RequestTracePlugin = createRouteScopedPlugin(
    name = "RequestTracePlugin",
    createConfiguration = {}
) {
    onCall { call ->
        LOGGER.trace("Processing call: ${call.request.uri}")
        // we can not use receive more than once unless we are using double receive
//        call.receive()
    }
}

/**
 * add logging for request executing
 */
fun Application.configureRequestLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            true
//            call.request.path().startsWith("/api/v1")
        }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }
}

