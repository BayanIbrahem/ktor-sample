plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    // A Java Library
    `java-library`
    // which produces test fixtures
    `java-test-fixtures`
}

dependencies {
    implementation(project(":logger:logger-core"))

    // exposed database ---->
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    // <---- exposed database
}

tasks.withType<Test> {
    useJUnitPlatform()
}
