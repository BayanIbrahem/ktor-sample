plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    // A Java Library
    `java-library`
}

dependencies {
    // kotlinx data-time
    api(libs.kotlinx.datetime)
    // core:
    api(libs.ktor.server.core)

    // dependency injection
    api(libs.ktor.server.di)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
