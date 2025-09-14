plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.kotlinx.datetime)
    // core:
    implementation(libs.ktor.server.core)
    // auth:
    implementation(libs.ktor.server.auth)
    // jwt tokens:
    implementation(libs.ktor.server.auth.jwt)
}