rootProject.name = "Outopia"

include(":server")

pluginManagement {
    val kotlinVersion: String by settings
    val springBootVersion: String by settings
    val dependencyManagementVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementVersion
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace?.startsWith("org.jetbrains.kotlin") == true) useVersion(kotlinVersion)
        }
    }
}
