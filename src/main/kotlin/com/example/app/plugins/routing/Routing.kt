package com.example.app.plugins.routing

import com.example.app.plugins.routing.form.taskForm
import com.example.app.plugins.routing.task.taskRouting
import com.example.data.repository.TaskRepository
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * routing configurations, we can pass general route
 * we can use nested routing with nested endpoints
 */
fun Application.configureRouting() {
//    like this:
//    get("/ep1/ep2"){}

//    or this:
//    routing {
//        route("/ep1") {
//            route("/ep2") {
//                get { /* ep1/ep2 *// }
//            }
//        }
//    }

    val repository: TaskRepository by dependencies
    routing {
        taskRouting(repository)

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

