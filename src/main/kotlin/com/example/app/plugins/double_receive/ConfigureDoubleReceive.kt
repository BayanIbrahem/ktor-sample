package com.example.app.plugins.double_receive

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.doublereceive.DoubleReceive

/**
 * this plugin is used if we need to get the body more than one time
 * (e.g one for the handler and one for the logger)
 */
fun Application.configureDoubleReceive() {
    install(DoubleReceive) {

    }

}