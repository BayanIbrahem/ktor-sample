rootProject.name = "ktor-sample"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
include(":authentaction")
include(":logger:logger-core")
include(":logger:logger-postgresql")
include(":test:logger:core")
include(":test:logger:postgres")
