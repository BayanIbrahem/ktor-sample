package com.example.app.plugins.routing

import com.example.app.plugins.routing.form.taskForm
import com.example.app.plugins.routing.task.taskRouting
import com.example.data.repository.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.resources.href
import io.ktor.resources.serialization.ResourcesFormat
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.Resources
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * routing configurations, we can pass general route
 * we can use nested routing with nested endpoints
 *
 * @see dummyRouting
 * @see safeRouting
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

    // for type safe routing
    install(Resources)

//    install(RoutingRoot) {...}
    routing {
        taskRouting(repository)

        taskForm()

//        dummyRouting()
    }
}

private fun Routing.dummyRouting() {
    get("/sth") {/* single path segment */ }
    get("/sth1/sth2") {/* multiple path segments */ }
    get("/sth1") {
        /* first path segment */
        get("/sth2") {
            /* second path segment */
        }
    }
    get("/sth/{param}/{param2}") {
        val param = call.parameters["param"]
        /* param is a required path parameter, and param2 is optional
        optional param can only be used at the end of the path*/
    }
    get("/sth/*") {
        /* A path with a wildcard character that matches any path segment.
        match '/sth/sth2' but not '/sth' alone */
    }
    get("/sth/{...}") {
        /* A path with a tailcard that matches all the rest of the URL path.
         matches '/sth' and any thing starts with '/sth' like '/sth/sth2' */
    }
    get("/sth/{params...}") {
        val params = call.parameters.getAll("params") ?: emptyList()
        /* A path containing a path parameter with tailcard. */
    }
    get(Regex("/.+/hello")) {
        /* A path containing a regular expression that matches path segments up to
        and including the last occurrence of the /hello. */
    }

    get(Regex("""(?<id>\d+)/hello""")) {
        val id = call.parameters["id"] // of type number
        /* A path containing a regular expression that matches path segments up to
        and including the last occurrence of the /hello. */
    }
}

/**
 * this resources is used like a query param which can:
 * 1) fetch all articles [Articles]
 * 2) post article using [Articles.New]
 * 3) get or edit one by id using [Articles.Id]
 */
@Resource("/articles")
private class Articles(val sort: String? = "new") {
    @Resource("new")
    class New(val parent: Articles = Articles())

    @Resource("{id}")
    class Id(val parent: Articles = Articles(), val id: Long) {
        @Resource("edit")
        class Edit(val parent: Id)
    }
}

private fun Routing.safeRouting() {
    /**
     * value is /articles/123/edit?sort=new
     */
    val link: String = href(ResourcesFormat(), Articles.Id.Edit(Articles.Id(id = 123)))
    get<Articles> { article ->
        // Get all articles ...
        call.respondText("List of articles sorted starting from ${article.sort}")
    }
    get<Articles.New> {
        // Show a page with fields for creating a new article ...
        call.respondText("Create a new article")
    }
    post<Articles> {
        // Save an article ...
        call.respondText("An article is saved", status = HttpStatusCode.Created)
    }
    get<Articles.Id> { article ->
        // Show an article with id ${article.id} ...
        call.respondText("An article with id ${article.id}", status = HttpStatusCode.OK)
    }
    get<Articles.Id.Edit> { article ->
        // Show a page with fields for editing an article ...
        call.respondText("Edit an article with id ${article.parent.id}", status = HttpStatusCode.OK)
    }
    put<Articles.Id> { article ->
        // Update an article ...
        call.respondText("An article with id ${article.id} updated", status = HttpStatusCode.OK)
    }
    delete<Articles.Id> { article ->
        // Delete an article ...
        call.respondText("An article with id ${article.id} deleted", status = HttpStatusCode.OK)
    }
}
