// settings.gradle.kts

// Define repositories for resolving plugins
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "wipegadmin"
include ':app'