plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    testImplementation(project(":logger:logger-postgresql"))
    testImplementation(project(":logger:logger-core"))
    testImplementation(project(":test:logger:core"))

//    testImplementation("io.zonky.test:embedded-postgres:1.3.1")

    // exposed database ---->
    implementation(libs.h2)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    // <---- exposed database
}
