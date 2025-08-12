plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
//    when we use the engine we set the main class from the engine throw this command
    mainClass = "io.ktor.server.netty.EngineMain"
}

ktor {
    // development mode can be specified by application.yaml properties using 'ktor.development' property
//    development = true
}
dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    // to use ktor yaml config
    implementation(libs.ktor.server.config.yaml)

    // for handling exceptions catching and remapping
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.host.common)
    // to automatically set the type of the content type of the request
    // like we install json to enable json format response
    implementation(libs.ktor.server.content.negotiation)
    // json and kotlinx serialization
    implementation(libs.ktor.serialization.kotlinx.json)
    // websocket plugin
    implementation(libs.ktor.server.websockets)

    // postgresql plugin
    implementation(libs.postgresql)
    implementation(libs.h2)
    // exposed database ---->
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    // <---- exposed database

    // dependency injection
    implementation(libs.ktor.server.di)
    // cros
    implementation(libs.ktor.server.cros)
////     sel signed ssl certification
//    implementation(libs.ktor.network.tsl.certificates)
    // logging:
    implementation(libs.ktor.server.call.logging)
    // type safe routes:
    implementation(libs.ktor.server.resources)
    // rate limit:
    implementation(libs.ktor.server.rate.limit)
    // double receive to be able to get response body more than once (like when we need it for the logger)
    implementation(libs.ktor.server.double.receive)
    // auth:
    implementation(libs.ktor.server.auth)
    // jwt tokens:
    implementation(libs.ktor.server.auth.jwt)


    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.client.content.negotiation.jvm)
    // testing the json response it it has the required format
    testImplementation(libs.json.path)
}
