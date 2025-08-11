package com.example.app.module.routing

import com.example.app.module.routing.form.taskForm
import com.example.app.module.routing.task.taskRouting
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        taskRouting()

        taskForm()

        test1()
    }
}

private fun Routing.test1() {
    get("/test1") {
        val text = "<h1>Hello From Ktor</h1>"
        val type = ContentType.parse("text/html")
        call.respondText(text, type)
    }
}

