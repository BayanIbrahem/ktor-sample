package com.example.app.plugins.database

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/ktor_tutorial_db",
        user = "postgres",
        password = "subpostfull"
    )
}
