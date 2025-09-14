plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    testImplementation(project(":authentication"))
    // authentication

    testApi(libs.junit.jupiter)
    testApi(libs.kotlin.test.junit)
//    testApi(libs.kotlinx.coroutines.test)
//    testApi(libs.kotlinx.datetime)
}
