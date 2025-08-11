package com.example.app.plugins.status_pages

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.plugins.statuspages.statusFile
import io.ktor.server.response.respondText

/**
 * this configurations is for exception handling and statuses.
 * we put here exception mapping results
 *
 * ## we can map:
 * 1) a certain exception response using [StatusPagesConfig.exception]
 * 2) a certain status code using [StatusPagesConfig.status]
 * 3) or linking a certain file with a certain status code [StatusPagesConfig.statusFile]
 */
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