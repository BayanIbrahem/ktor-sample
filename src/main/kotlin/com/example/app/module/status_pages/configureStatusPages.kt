package com.example.app.module.status_pages

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.statusFile
import io.ktor.server.response.respondText

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if (cause is IllegalArgumentException) {
                call.respondText(
                    text = "500: $cause",
                    status = HttpStatusCode.Forbidden
                )
            } else {
                call.respondText(
                    text = "500: $cause",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(
                text = "404: Page Not Found",
                status = status
            )
        }
        statusFile(
            HttpStatusCode.Unauthorized,
            HttpStatusCode.PaymentRequired,
            filePattern = "error#.html"
        )
    }
}