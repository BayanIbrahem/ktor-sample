package com.example.app.module

import com.example.app.plugins.auth.configureAuthentication
import com.example.app.plugins.database.configureDatabase
import com.example.app.plugins.di.di
import com.example.app.plugins.double_receive.configureDoubleReceive
import com.example.app.plugins.logging.configureRequestLogging
import com.example.app.plugins.rate_limit.configureRateLimit
import com.example.app.plugins.routing.configureRouting
import com.example.app.plugins.serialization.configureSerialization
import com.example.app.plugins.status_pages.configureStatusPages
import com.example.app.plugins.websocket.configureWebSocket
import io.ktor.server.application.Application
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.request.uri
import io.ktor.util.logging.KtorSimpleLogger

/**
 * ## Documentation:
 * Ktor allows you to use modules to structure your application by defining a specific set of
 * routes inside a specific module. A module is an extension function of the Application class.
 * In the example below, the module1 extension function defines a module that accepts GET requests
 * made to the /module1 URL path.
 *
 * this is automatically used by the Engine main
 * - we set plugins configurations and routing configurations
 * - configurations order matters here, routing should be the last in most cases, eg [configureWebSocket]
 * should be before [configureRouting] cause routing uses the web socket within itself
 * - plugins can be installed within a route not for everyting
 *
 * ## Note
 * this module should be one of the ktor.application.module paths, for this one it is
 * package + file name (+Kt) + method name
 * 'com.example.app.module.ModuleKt.module'
 *
 * ## Note
 * Plugins installed in a specified module are in effect for other loaded modules.
 */

@Suppress("unused")
// module can be suspend
// suspend fun Application.module() {
fun Application.module() {
//    we can read environment configurations using the envi
//    val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString()
    di()
    configureWebSocket()
    configureSerialization()
    configureStatusPages()
    configureDatabase()
    configureRequestLogging()
    configureRateLimit()
//    configureDoubleReceive()
    configureAuthentication()

    configureRouting()
}
