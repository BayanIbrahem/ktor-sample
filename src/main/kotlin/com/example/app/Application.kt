package com.example.app

import io.ktor.server.netty.EngineMain

/**
 * main class where the app starts, we have two options here:
 * - configure with file 'application.yaml' so we use engine main **Current option**
 * - configure with code then we call embedded server
 */
fun main(args: Array<String>) {
    EngineMain.main(args)

}