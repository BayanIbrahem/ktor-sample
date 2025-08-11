package com.example.app.plugins.serialization

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

/**
 * configure json serialization we can customize the json object (pretty print, explicit nulls, etc)
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
