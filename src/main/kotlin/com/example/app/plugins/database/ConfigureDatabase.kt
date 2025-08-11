package com.example.app.plugins.database

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database

/**
 * configurations for database, now it is for test only and configured in code,
 * later it would be configured by file.
 */
fun Application.configureDatabase() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/ktor_tutorial_db",
        user = "postgres",
        password = "subpostfull"
    )
}
