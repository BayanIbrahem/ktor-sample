package com.example.app.plugins.auth

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application

val applicationHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureOAuth() {
}
