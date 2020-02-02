rootProject.name = "Outopia"

include(":server", ":js", ":shared")

pluginManagement {
    val kotlinVersion: String by settings
    val springBootVersion: String by settings
    val dependencyManagementVersion: String by settings

    repositories {
        gradlePluginPortal()
        maven("https://repo1.maven.org/maven2/")
        maven("https://repo.spring.io/plugins-release/")
        maven("https://oss.sonatype.org/content/repositories/releases/")
        maven("https://dl.bintray.com/kotlin/kotlin-dev/")
        mavenCentral()
    }

    plugins {
        id("org.jetbrains.kotlin.js") version kotlinVersion
        id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementVersion
    }
}
