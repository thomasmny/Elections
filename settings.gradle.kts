rootProject.name = "Elections"

include("elections-api")
include("elections-core")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}