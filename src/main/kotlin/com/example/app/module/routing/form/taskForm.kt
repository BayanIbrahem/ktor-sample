package com.example.app.module.routing.form

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing

fun Routing.taskForm() {
    /**
     * this provide an endpoint links to a data directory
     */
    staticResources(
        remotePath = "/task_ui", basePackage = "task_ui" // directory
    )
    staticResources(
        remotePath = "/static", basePackage = "static" // directory
    )
}