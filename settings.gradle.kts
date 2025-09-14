rootProject.name = "ktor-sample"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
include(":authentication")
include(":logger:logger-core")
include(":logger:logger-postgresql")
include(":test:logger:core")
include(":test:logger:postgres")
include(":test:auth:core")
include(":authorization")
include(":test:auth:authentication")
